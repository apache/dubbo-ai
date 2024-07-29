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
package org.apache.dubbo.ai.spring.ai.dashscope.api;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.boot.context.properties.bind.ConstructorBinding;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import reactor.adapter.rxjava.RxJava2Adapter;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class DashscopeApi {

    private static final Logger logger = LoggerFactory.getLogger(DashscopeApi.class);

    /**
     * Default chat model
     */
    public static final String DEFAULT_CHAT_MODEL = ChatModel.QWEN_PLUS.getModel();

    /**
     * Default embedding model
     */
    public static final String DEFAULT_EMBEDDING_MODEL = EmbeddingModel.TEXT_EMBEDDING_V1.getModel();

    private static final Predicate<String> SSE_DONE_PREDICATE = "event:result"::equals;


    private Generation generation;
    private final String apiKey;

    public DashscopeApi(String apiKey) {
        this("", apiKey);
    }

    public DashscopeApi(String baseUrl, String apiKey) {
        this(baseUrl, apiKey, new Generation());
    }

    public DashscopeApi(String baseUrl, String apiKey, Generation generation) {
        this.apiKey = apiKey;
        this.generation = generation;
    }


    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ChatCompletionRequest(@JsonProperty("model") String model, @JsonProperty("steam") Boolean stream,
                                        @JsonProperty("messages") List<Message> chatCompletionInput,
                                        @JsonProperty("parameters") GenerationParam parameters) {

    }


    public ResponseEntity<GenerationResult> chatCompletionEntity(ChatCompletionRequest chatRequest) {
        Assert.notNull(chatRequest, "The request body can not be null.");
        Assert.isTrue(!chatRequest.stream(), "Request must set the steam property to false.");

        try {
            GenerationParam parameters = chatRequest.parameters;
            parameters.setApiKey(this.apiKey);
            parameters.setMessages(chatRequest.chatCompletionInput);
            GenerationResult result = this.generation.call(parameters);
            return ResponseEntity.of(Optional.of(result));
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }

    }

    public static DashScopeApiBuilder builder() {
        return new DashScopeApiBuilder();
    }

    public Flux<GenerationResult> chatCompletionStream(ChatCompletionRequest chatRequest) {
        Assert.notNull(chatRequest, "The request body can not be null.");
        Assert.isTrue(chatRequest.stream(), "Request must set the steam property to false.");

        AtomicBoolean isInsideTool = new AtomicBoolean(false);
        try {
            GenerationParam parameters = chatRequest.parameters;
            parameters.setApiKey(this.apiKey);
            parameters.setMessages(chatRequest.chatCompletionInput);
            Flowable<GenerationResult> resultFlowable = this.generation.streamCall(parameters);
            return RxJava2Adapter.flowableToFlux(resultFlowable);
        } catch (NoApiKeyException | InputRequiredException e) {
            throw new RuntimeException(e);
        }
    }

    public static class DashScopeApiBuilder {

        private String apiKey;

        private DashScopeApiBuilder() {
        }

        public DashScopeApiBuilder withApiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public DashscopeApi build() {
            return new DashscopeApi(apiKey);
        }
    }

    /*
     * Dashscope Chat Completion Models:
     * <a href="https://help.aliyun.com/zh/dashscope/developer-reference/api-details">Dashscope Chat API</a>
     */
    public enum ChatModel {

        /**
         * 模型支持8k tokens上下文，为了保证正常的使用和输出，API限定用户输入为6k tokens。
         */
        QWEN_PLUS("qwen-plus"),

        /**
         * 模型支持32k tokens上下文，为了保证正常的使用和输出，API限定用户输入为30k tokens。
         */
        QWEN_TURBO("qwen-turbo"),

        /**
         * 模型支持8k tokens上下文，为了保证正常的使用和输出，API限定用户输入为6k tokens。
         */
        QWEN_MAX("qwen-max"),

        /**
         * 模型支持30k tokens上下文，为了保证正常的使用和输出，API限定用户输入为28k tokens。
         */
        QWEN_MAX_LONGCONTEXT("qwen-max-longcontext");

        private final String model;

        ChatModel(String model) {
            this.model = model;
        }

        public String getModel() {
            return this.model;
        }
    }

    public enum EmbeddingModel {
        TEXT_EMBEDDING_V1("text-embedding-v1"),
        TEXT_EMBEDDING_V2("text-embedding-v2");

        private String model;

        EmbeddingModel(String model) {
            this.model = model;
        }

        public String getModel() {
            return model;
        }
    }

    public enum ChatCompletionFinishReason {
        /**
         * The model hit a natural stop point or a provided stop sequence.
         */
        @JsonProperty("stop") STOP,
        /**
         * The maximum number of tokens specified in the request was reached.
         */
        @JsonProperty("length") LENGTH,
        /**
         * The content was omitted due to a flag from our content filters.
         */
        @JsonProperty("content_filter") CONTENT_FILTER,
        /**
         * The model called a tool.
         */
        @JsonProperty("tool_calls") TOOL_CALLS,
        /**
         * (deprecated) The model called a function.
         */
        @JsonProperty("function_call") FUNCTION_CALL,
        /**
         * Only for compatibility with Mistral AI API.
         */
        @JsonProperty("tool_call") TOOL_CALL
    }

    /**
     * Creates an embedding vector representing the input text.
     *
     * @param input Input text to embed, encoded as a string or array of tokens. To embed multiple
     *              inputs in a single * request, pass an array of strings or array of token arrays. The input
     *              cannot be an empty string, and any array must be 2048 * dimensions or less.
     * @param model ID of the model to use.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingRequest(
            @JsonProperty("input") EmbeddingTextList input, @JsonProperty("model") String model) {
        /**
         * Create an embedding request with the given input, model and encoding format set to float.
         *
         * @param input Input text to embed.
         * @param model ID of the model to use.
         */
        public EmbeddingRequest(EmbeddingTextList input, String model) {
            this.input = input;
            this.model = model;
        }

        /**
         * Create an embedding request with the given input. Encoding format is set to float and user is
         * null and the model is set to 'text-embedding-v1'.
         *
         * @param input Input text to embed.
         */
        public EmbeddingRequest(EmbeddingTextList input) {
            this(input, DEFAULT_EMBEDDING_MODEL);
        }
    }

    /**
     * Embedding content to embed.
     *
     * @param texts
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EmbeddingTextList(@JsonProperty("texts") List<String> texts) {
        public EmbeddingTextList(List<String> texts) {
            this.texts = texts;
        }
    }


    /**
     * Dashscope请求返回embedding
     *
     * @param embeddings embedding列表
     */
    public record EmbeddingResponse(
            @JsonProperty("embeddings") List<DashscopeEmbedding> embeddings) {
    }

    /**
     * Dashscope请求返回embedding
     *
     * @param index     输入文本在embedding列表中的索引
     * @param embedding embedding向量
     */
    public record DashscopeEmbedding(
            @JsonProperty("text_index") Integer index,
            @JsonProperty("embedding") List<Double> embedding) {
    }

    /**
     * Represents a tool the model may call. Currently, only functions are supported as a tool.
     *
     * @param type     The type of the tool. Currently, only 'function' is supported.
     * @param function The function definition.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FunctionTool(
            @JsonProperty("type") Type type, @JsonProperty("function") Function function) {

        /**
         * Create a tool of type 'function' and the given function definition.
         *
         * @param function function definition.
         */
        @ConstructorBinding
        public FunctionTool(Function function) {
            this(Type.FUNCTION, function);
        }

        /**
         * Create a tool of type 'function' and the given function definition.
         */
        public enum Type {
            /**
             * Function tool type.
             */
            @JsonProperty("function")
            FUNCTION
        }

        /**
         * Function definition.
         *
         * @param description A description of what the function does, used by the model to choose when
         *                    and how to call the function.
         * @param name        The name of the function to be called. Must be a-z, A-Z, 0-9, or contain
         *                    underscores and dashes, with a maximum length of 64.
         * @param parameters  The parameters the functions accepts, described as a JSON Schema object. To
         *                    describe a function that accepts no parameters, provide the value {"type": "object",
         *                    "properties": {}}.
         */
        public record Function(
                @JsonProperty("description") String description,
                @JsonProperty("name") String name,
                @JsonProperty("parameters") Map<String, Object> parameters) {

            /**
             * Create tool function definition.
             *
             * @param description tool function description.
             * @param name        tool function name.
             * @param jsonSchema  tool function schema as json.
             */
            @ConstructorBinding
            public Function(String description, String name, String jsonSchema) {
                this(description, name, ModelOptionsUtils.jsonToMap(jsonSchema));
            }
        }
    }
}