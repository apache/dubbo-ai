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
package org.apache.dubbo.ai.core.proxy;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.dubbo.ai.core.DubboAiContext;
import org.apache.dubbo.ai.core.Prompt;
import org.apache.dubbo.ai.core.config.Options;
import org.apache.dubbo.ai.core.function.FunctionFactory;
import org.apache.dubbo.ai.core.function.FunctionInfo;
import org.apache.dubbo.ai.core.model.parser.AiResponseParser;
import org.apache.dubbo.ai.core.util.BeanUtils;
import org.apache.dubbo.common.stream.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AiServiceInterfaceImpl {
    
    private static final Logger logger = LoggerFactory.getLogger(AiServiceInterfaceImpl.class);

    Class<?> interfaceClass;
    

    private final DubboAiContext dubboAiContext;

    public AiServiceInterfaceImpl(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        dubboAiContext = new DubboAiContext(interfaceClass);
    }

    @RuntimeType
    public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Exception {
        var client = dubboAiContext.getClient();
        Class<?> returnType = method.getReturnType();

        // get functions from method
        List<FunctionInfo> functionInfoList = FunctionFactory.getFunctionsByMethod(method);

        // 如果是复杂对象，则给AI一个提示词，从用户给的数据中返回一个json回来，进行序列化。

        // 非流调用
        DefaultChatClient.DefaultChatClientRequestSpec prompted = (DefaultChatClient.DefaultChatClientRequestSpec) client.prompt();
        Options methodOptions = dubboAiContext.getMethodOptions(method);
        mergeOptions(methodOptions, prompted);

        // add functions to ChatClientRequestSpec
        for (FunctionInfo functionInfo : functionInfoList) {
            prompted.function(functionInfo.getName(), functionInfo.getDesc(), functionInfo.getInputType(), functionInfo.getFunction());
        }

        // 流式返回
        if (returnType.equals(void.class)) {
            // 固定两个参数
            Parameter parameter = method.getParameters()[1];
            if (parameter.getType().equals(StreamObserver.class)) {
                StreamObserver<String> aiStreamObserver = (StreamObserver<String>) args[1];
                // String request = aiStreamObserver.getRequest();
                Flux<ChatResponse> chatResponseFlux = prompted.user(args[0].toString()).stream().chatResponse();
                CountDownLatch latch = new CountDownLatch(1);
                chatResponseFlux.subscribe(
                        chatResponse -> {
                            aiStreamObserver.onNext(chatResponse.getResult().getOutput().getContent());
                            // 这里处理每一个聊天响应
                        },
                        error -> {
                            aiStreamObserver.onError(error);
                        },
                        () -> {
                            aiStreamObserver.onCompleted();
                            // 流完成时执行
                            logger.info("Stream completed");
                            latch.countDown();
                        }
                );
                latch.await();
            }
            return null;
        }

        // 非流调用
        Prompt prompt = method.getAnnotation(Prompt.class);
        String promptTemplate = prompt.value();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            String name = "\\{" + parameters[i].getName() + "}";
            String replaceValue = args[i].toString();
            promptTemplate = promptTemplate.replaceAll(name, replaceValue);
        }
        logger.debug("promptTemplate: {}", promptTemplate);
        ChatClient.CallResponseSpec call = prompted.user(promptTemplate).call();
        String content = call.content();
        if (returnType == String.class) {
            return content;
        }
        return AiResponseParser.parse(content, returnType);

    }

    private void mergeOptions(Options methodOptions, DefaultChatClient.DefaultChatClientRequestSpec prompted) {
        ChatOptions chatOptions = prompted.getChatOptions();
        Options source = new Options();
        BeanUtils.copyPropertiesIgnoreNull(source, chatOptions);
        BeanUtils.copyPropertiesIgnoreNull(methodOptions, chatOptions);
        prompted.options(chatOptions);
    }
}
