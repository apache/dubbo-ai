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

import com.alibaba.fastjson2.JSONObject;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.apache.dubbo.ai.core.DubboAiService;
import org.apache.dubbo.ai.core.Prompt;
import org.apache.dubbo.ai.core.chat.model.LoadBalanceChatModel;
import org.apache.dubbo.ai.core.model.ModelFactory;
import org.apache.dubbo.ai.core.util.PropertiesUtil;
import org.apache.dubbo.common.stream.StreamObserver;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class AiServiceInterfaceImpl {


    private static final Logger logger = LoggerFactory.getLogger(AiServiceInterfaceImpl.class);

    Class<?> interfaceClass;

    ChatClient client;

    public AiServiceInterfaceImpl(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        constructChatClients();
    }

    private void constructChatClients() {
        DubboAiService dubboAiService = interfaceClass.getAnnotation(DubboAiService.class);
        String[] providerConfigs = dubboAiService.providerConfigs();
        constructAiConfig(dubboAiService);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model",dubboAiService.model());
        LoadBalanceChatModel loadBalanceChatModel = ModelFactory.getLoadBalanceChatModel(Arrays.stream(providerConfigs).toList(), jsonObject);
        this.client = ChatClient.builder(loadBalanceChatModel).build();
    }

    private void constructAiConfig(DubboAiService dubboAiService) {
        String path = dubboAiService.configPath();
        Map<String, String> props = PropertiesUtil.getPropsByPath(path);
        ApplicationModel.defaultModel().modelEnvironment().updateAppConfigMap(props);
    }

    @RuntimeType
    public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Exception {
        Class<?> returnType = method.getReturnType();
        // 非流调用
        if (returnType.equals(String.class)) {
            Prompt prompt = method.getAnnotation(Prompt.class);
            String promptTemplate = prompt.value();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                String name = "\\{" + parameters[i].getName() + "}";
                String replaceValue = args[i].toString();
                promptTemplate = promptTemplate.replaceAll(name, replaceValue);
            }
            logger.info("promptTemplate: {}",promptTemplate);
            ChatClient.CallResponseSpec call = client.prompt().user(promptTemplate).call();
            // 非流调用并返回
            return call.chatResponse().getResult().getOutput().getContent();
        }
        // 如果是复杂对象，则给AI一个提示词，从用户给的数据中返回一个json回来，进行序列化。
        // 非流调用

        // 流式返回
        if (returnType.equals(void.class)) {
            // 固定两个参数
            Parameter parameter = method.getParameters()[1];
            if (parameter.getType().equals(StreamObserver.class)) {
                StreamObserver<String> aiStreamObserver = (StreamObserver<String>) args[1];
                // String request = aiStreamObserver.getRequest();
                Flux<ChatResponse> chatResponseFlux = client.prompt().user(args[0].toString()).stream().chatResponse();
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

        throw new RuntimeException("not support ai return type");
    }
}
