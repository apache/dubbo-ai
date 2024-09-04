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
import org.apache.dubbo.ai.core.Val;
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
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import reactor.core.publisher.Flux;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class AiServiceInterfaceImpl {

    private static final Logger logger = LoggerFactory.getLogger(AiServiceInterfaceImpl.class);

    private final Class<?> interfaceClass;

    private final Map<Class<?>, Map<String, Field>> cachedFields = new ConcurrentHashMap<>();


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

        String promptTemplate = getPromptTemplate(method, args);
        logger.debug("promptTemplate: {}", promptTemplate);
        // stream return
        if (returnType.equals(void.class)) {
            // 固定两个参数
            Parameter parameter = method.getParameters()[1];
            if (parameter.getType().equals(StreamObserver.class)) {
                StreamObserver<String> aiStreamObserver = (StreamObserver<String>) args[1];
                // String request = aiStreamObserver.getRequest();
                Flux<ChatResponse> chatResponseFlux = prompted.user(promptTemplate).stream().chatResponse();
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
                            logger.debug("Stream completed");
                            latch.countDown();
                        }
                );
                latch.await();
            }
            return null;
        }


        ChatClient.CallResponseSpec call = prompted.user(promptTemplate).call();
        if (!BeanUtils.isPrimitiveOrWrapperOrString(returnType)) {
            return call.responseEntity(returnType).entity();
        }
        String content = call.content();
        if (returnType == String.class) {
            return content;
        }
        return AiResponseParser.parse(content, returnType);

    }

    private String getPromptTemplate(Method method, Object[] args) {
        Prompt prompt = method.getAnnotation(Prompt.class);
        String promptTemplate = prompt.value();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType() == StreamObserver.class) {
                continue;
            }
            if (!BeanUtils.isPrimitiveOrWrapperOrString(parameters[i].getType())) {
                promptTemplate = dealWithComplexObject(promptTemplate, args[i]);
            }
            var name = parameters[i].getName();
            if (parameters[i].isAnnotationPresent(Val.class)) {
                name = parameters[i].getAnnotation(Val.class).value();
            }
            name = "\\{" + name + "}";
            String replaceValue = args[i].toString();
            promptTemplate = promptTemplate.replaceAll(name, replaceValue);
        }
        return promptTemplate;
    }

    private String dealWithComplexObject(String promptTemplate, Object obj) {
        BeanWrapper wrapper = new BeanWrapperImpl(obj);
        for (PropertyDescriptor propertyDescriptor : wrapper.getPropertyDescriptors()) {
            Class<?> propertyType = propertyDescriptor.getPropertyType();

            // skip obj properties
            if (propertyDescriptor.getReadMethod().getDeclaringClass() == Object.class) {
                continue;
            }
            String name = propertyDescriptor.getName();
            var propertyValue = wrapper.getPropertyValue(name);
            if (propertyValue == null) {
                continue;
            }
            if (!BeanUtils.isPrimitiveOrWrapperOrString(propertyType)) {
                promptTemplate = dealWithComplexObject(promptTemplate, wrapper.getPropertyValue(name));
            }

            name = getFieldNameFromAnnotation(obj, name);

            name = "\\{" + name + "}";
            String replaceValue = propertyValue.toString();
            promptTemplate = promptTemplate.replaceAll(name, replaceValue);
        }
        return promptTemplate;
    }

    private String getFieldNameFromAnnotation(Object obj, String name) {
        Class<?> currentClass = obj.getClass();
        Map<String, Field> fieldMap = cachedFields.computeIfAbsent(currentClass, BeanUtils::getAllFields);
        Field field = fieldMap.get(name);
        if (field == null) {
            return name;
        }
        if (field.isAnnotationPresent(Val.class)) {
            return field.getAnnotation(Val.class).value();
        }
        return name;
    }


    private void mergeOptions(Options methodOptions, DefaultChatClient.DefaultChatClientRequestSpec prompted) {
        ChatOptions chatOptions = prompted.getChatOptions();
        Options source = new Options();
        BeanUtils.copyPropertiesIgnoreNull(source, chatOptions);
        BeanUtils.copyPropertiesIgnoreNull(methodOptions, chatOptions);
        prompted.options(chatOptions);
    }
}
