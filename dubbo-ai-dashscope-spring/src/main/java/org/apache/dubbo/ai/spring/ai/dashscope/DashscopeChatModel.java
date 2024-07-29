/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.ai.spring.ai.dashscope;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.tools.ToolCallBase;
import com.alibaba.dashscope.tools.ToolCallFunction;
import org.apache.dubbo.ai.spring.ai.dashscope.api.DashscopeApi;
import org.apache.dubbo.ai.spring.ai.dashscope.metadata.DashscopeChatResponseMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.metadata.ChatGenerationMetadata;
import org.springframework.ai.chat.model.AbstractToolCallSupport;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class DashscopeChatModel extends AbstractToolCallSupport implements ChatModel {

    private static final Logger logger = LoggerFactory.getLogger(DashscopeChatModel.class);

    /**
     * Low-level access to the Dashscope API
     */
    private final DashscopeApi dashscopeApi;

    /**
     * The retry template used to retry the OpenAI API calls.
     */
    public final RetryTemplate retryTemplate;

    /**
     * The default options used for the chat completion requests.
     */
    private DashscopeChatOptions defaultOptions;

    public DashscopeChatModel(DashscopeApi dashscopeApi) {
        this(dashscopeApi,
                DashscopeChatOptions.builder()
                        .withModel(DashscopeApi.DEFAULT_CHAT_MODEL)
                        .withTemperature(0.7f)
                        .build());
    }

    public DashscopeChatModel(DashscopeApi dashscopeApi, DashscopeChatOptions options) {
        this(dashscopeApi, options, null, RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    public DashscopeChatModel(DashscopeApi dashscopeApi, DashscopeChatOptions options,
                              FunctionCallbackContext functionCallbackContext, RetryTemplate retryTemplate) {
        super(functionCallbackContext);
        Assert.notNull(dashscopeApi, "DashscopeApi must not be null");
        Assert.notNull(options, "Options must not be null");
        Assert.notNull(retryTemplate, "RetryTemplate must not be null");

        this.dashscopeApi = dashscopeApi;
        this.defaultOptions = options;
        this.retryTemplate = retryTemplate;
    }


    @Override
    public ChatResponse call(Prompt prompt) {
        DashscopeApi.ChatCompletionRequest request = createRequest(prompt, false);

        return this.retryTemplate.execute(ctx -> {
            ResponseEntity<GenerationResult> completionEntity = this.dashscopeApi.chatCompletionEntity(request);

            var chatCompletion = completionEntity.getBody();
            if (chatCompletion == null) {
                logger.warn("No chat completion returned for prompt: {}", prompt);
                return new ChatResponse(List.of());
            }
            
            List<Generation> generations = chatCompletion.getOutput().getChoices().stream()
                    .map(choice -> new Generation(new AssistantMessage(choice.getMessage().getContent(),
                            toMap(chatCompletion.getRequestId(), choice), toTollCalls(choice)),
                            ChatGenerationMetadata.from(choice.getFinishReason(), null)))
                    .toList();

            var chatResponse = new ChatResponse(generations,
                    DashscopeChatResponseMetadata.from(chatCompletion.getUsage(), chatCompletion.getRequestId()));

            if (isToolCall(chatResponse, Collections.singleton(DashscopeApi.ChatCompletionFinishReason.TOOL_CALLS.name()))) {
                var toolCallConversation = handleToolCalls(prompt, chatResponse);
                // Recursively call the call method with the tool call message
                // conversation that contains the call responses.
                return this.call(new Prompt(toolCallConversation, prompt.getOptions()));
            }
            return chatResponse;
        });
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return DashscopeChatOptions.fromOptions(this.defaultOptions);
    }

    private Map<String, Object> toMap(String id, com.alibaba.dashscope.aigc.generation.GenerationOutput.Choice choice) {
        Map<String, Object> map = new HashMap<>();

        var message = choice.getMessage();
        if (message.getRole() != null) {
            map.put("role", message.getRole());
        }
        if (choice.getMessage() != null) {
            map.put("finishReason", choice.getFinishReason());
        }
        map.put("id", id);
        return map;
    }

    private List<AssistantMessage.ToolCall> toTollCalls(com.alibaba.dashscope.aigc.generation.GenerationOutput.Choice choice) {
        List<ToolCallBase> toolCalls = choice.getMessage().getToolCalls();
        List<AssistantMessage.ToolCall> res = new ArrayList<>();
        if (toolCalls == null) {
            return res;
        }

        for (ToolCallBase toolCall : toolCalls) {
            if (toolCall instanceof ToolCallFunction function) {
                res.add(new AssistantMessage.ToolCall(function.getId(), function.getType(), function.getFunction().getName(), function.getFunction().getArguments()));
            }
        }
        return res;
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        DashscopeApi.ChatCompletionRequest request = createRequest(prompt, true);
        return this.retryTemplate.execute(ctx -> {
            Flux<GenerationResult> chatCompletionFlux = this.dashscopeApi.chatCompletionStream(request);
            ConcurrentHashMap<String, String> roleMap = new ConcurrentHashMap<>();
            return chatCompletionFlux.map(chatCompletion -> {
                String id = chatCompletion.getRequestId();
                List<Generation> generations = chatCompletion.getOutput().getChoices().stream().map(choice -> {
                    if (choice.getMessage().getRole() != null) {
                        roleMap.putIfAbsent(id, choice.getMessage().getRole());
                    }
                    String finish = (choice.getFinishReason() != null ? choice.getFinishReason() : "");
                    var generation = new Generation(choice.getMessage().getContent(),
                            Map.of("requestId", id, "role", roleMap.get(id), "finishReason", finish));
                    if (choice.getFinishReason() != null) {
                        generation = generation
                                .withGenerationMetadata(ChatGenerationMetadata.from(choice.getFinishReason(), null));
                    }
                    return generation;
                }).toList();

                return new ChatResponse(generations,
                        DashscopeChatResponseMetadata.from(chatCompletion.getUsage(), chatCompletion.getRequestId()));
            });
        });
    }

//    @Override
//    protected ChatCompletionRequest doCreateToolResponseRequest(ChatCompletionRequest previousRequest,
//                                                                ChatCompletionMessage responseMessage, List<ChatCompletionMessage> conversationHistory) {
//        // Every tool-call item requires a separate function call and a response (TOOL)
//        // message.
//        for (ToolCall toolCall : responseMessage.toolCalls()) {
//
//            var functionName = toolCall.function().name();
//            String functionArguments = toolCall.function().arguments();
//
//            if (!this.functionCallbackRegister.containsKey(functionName)) {
//                throw new IllegalStateException("No function callback found for function name: " + functionName);
//            }
//
//            String functionResponse = this.functionCallbackRegister.get(functionName).call(functionArguments);
//
//            // Add the function response to the conversation.
//            conversationHistory
//                    .add(new ChatCompletionMessage(ChatCompletionMessage.Role.TOOL, functionResponse, functionName, null));
//        }
//
//        // Recursively call chatCompletionWithTools until the model doesn't call a
//        // functions anymore.
//        ChatCompletionRequest newRequest = new ChatCompletionRequest(
//                new ChatCompletionRequestInput(conversationHistory), false);
//        newRequest = ModelOptionsUtils.merge(newRequest, previousRequest, ChatCompletionRequest.class);
//
//        return newRequest;
//    }
//
//    @Override
//    protected List<ChatCompletionMessage> doGetUserMessages(DashscopeApi.ChatCompletionRequest request) {
//        return request.chatCompletionInput().messages();
//    }
//
//
//    protected ChatCompletionMessage doGetToolResponseMessage(ResponseEntity<ChatCompletion> chatCompletion) {
//        return chatCompletion.getBody().output().choices().iterator().next().message();
//    }
//
//
//    protected ResponseEntity<GenerationResult> doChatCompletion(DashscopeApi.ChatCompletionRequest request) {
//        return this.dashscopeApi.chatCompletionEntity(request);
//    }
//    
//

    protected boolean isToolFunctionCall(ResponseEntity<GenerationResult> chatCompletion) {
        var body = chatCompletion.getBody();
        if (body == null) {
            return false;
        }

        var choices = body.getOutput().getChoices();
        if (CollectionUtils.isEmpty(choices)) {
            return false;
        }

        var choice = choices.get(0);
        return !CollectionUtils.isEmpty(choice.getMessage().getToolCalls());
    }

    private DashscopeApi.ChatCompletionRequest createRequest(Prompt prompt, boolean isStream) {
        Set<String> functionsForThisRequest = new HashSet<>();
        String model = this.defaultOptions.getModel();


        List<Message> chatCompletionInputs = prompt.getInstructions().stream().map(m -> {
            if (m.getMessageType() == MessageType.USER || m.getMessageType() == MessageType.SYSTEM) {
                Message msg = Message.builder().role(m.getMessageType().getValue()).content(m.getContent()).build();
                return List.of(msg);
            } else if (m.getMessageType() == MessageType.ASSISTANT) {
                var assistantMessage = (AssistantMessage) m;
                List<ToolCallBase> toolCalls = null;
                if (!CollectionUtils.isEmpty(assistantMessage.getToolCalls())) {
                    toolCalls = assistantMessage.getToolCalls().stream().map(toolCall -> {
                        ToolCallFunction toolCallFunction = new ToolCallFunction();
                        var callFunc = toolCallFunction.new CallFunction();
                        callFunc.setArguments(toolCall.arguments());
                        callFunc.setName(toolCall.name());
                        callFunc.setOutput(toolCall.arguments());
                        toolCallFunction.setFunction(callFunc);
                        return (ToolCallBase) toolCallFunction;
                    }).toList();
                }
                var msg = Message.builder()
                        .content(assistantMessage.getContent())
                        .role(m.getMessageType().getValue())
                        .toolCalls(toolCalls).build();
                return List.of(msg);
            } else if (m.getMessageType() == MessageType.TOOL) {
                ToolResponseMessage toolMessage = (ToolResponseMessage) m;

                toolMessage.getResponses().forEach(response -> {
                    Assert.isTrue(response.id() != null, "ToolResponseMessage must have an id");
                    Assert.isTrue(response.name() != null, "ToolResponseMessage must have a name");
                });
                return toolMessage.getResponses().stream().map(tr -> Message.builder().
                        content(tr.responseData()).role(m.getMessageType().getValue())
                        .name(tr.name()).toolCallId(tr.id()).build()).toList();
            } else {
                throw new IllegalArgumentException("Unsupported message type: " + m.getMessageType());
            }
        }).flatMap(List::stream).collect(Collectors.toList());

        DashscopeChatOptions updateRuntimeOptions = null;
        if (prompt.getOptions() != null) {
            if (prompt.getOptions() instanceof DashscopeChatOptions) {
                model = ((DashscopeChatOptions) prompt.getOptions()).getModel();
            }

            var runtimeOptions = prompt.getOptions();
            updateRuntimeOptions = ModelOptionsUtils.copyToTarget(runtimeOptions,
                    ChatOptions.class, DashscopeChatOptions.class);

            Set<String> promptEnabledFunctions = this.handleFunctionCallbackConfigurations(updateRuntimeOptions,
                    IS_RUNTIME_CALL);
            functionsForThisRequest.addAll(promptEnabledFunctions);
        }

        if (this.defaultOptions != null) {
            Set<String> defaultEnabledFunctions = this.handleFunctionCallbackConfigurations(this.defaultOptions,
                    !IS_RUNTIME_CALL);

            functionsForThisRequest.addAll(defaultEnabledFunctions);

//            chatCompletionRequestParameters = ModelOptionsUtils.merge(chatCompletionRequestParameters,
//                    this.defaultOptions, ChatCompletionRequestParameters.class);
        }

        return new DashscopeApi.ChatCompletionRequest(model, isStream, chatCompletionInputs, updateRuntimeOptions.toGenerationParam());
    }

}
