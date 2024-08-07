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
package org.apache.dubbo.ai.core;

import com.alibaba.fastjson2.JSONObject;
import org.apache.dubbo.ai.core.chat.model.LoadBalanceChatModel;
import org.apache.dubbo.ai.core.config.AiModelProviderConfig;
import org.apache.dubbo.ai.core.config.Options;
import org.apache.dubbo.ai.core.model.ModelFactory;
import org.apache.dubbo.ai.core.type.ClassAiMetadata;
import org.apache.dubbo.ai.core.type.MethodAiMetadata;
import org.apache.dubbo.ai.core.util.BeanUtils;
import org.apache.dubbo.ai.core.util.PropertiesUtil;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClient;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * one class  one Ai Context;
 */
public class DubboAiContext {


    private DefaultChatClient client;

    private Class<?> aiInterfaceClass;

    private ClassAiMetadata classAiMetadata;

    private Map<Method, MethodAiMetadata> methodMetadataMap = new ConcurrentHashMap<>();


    public DubboAiContext(Class<?> aiInterfaceClass) {
        this.aiInterfaceClass = aiInterfaceClass;
        classAiMetadata = new ClassAiMetadata(aiInterfaceClass);
        checkOptions();
        constructChatClients();
    }

    /**
     * check options is same
     */
    private void checkOptions() {
        List<AiModelProviderConfig> providerConfigs = classAiMetadata.getProviderConfigs();
        if (providerConfigs.size() == 1) {
            return;
        }
        AiModelProviderConfig pre = providerConfigs.get(0);
        for (int i = 1; i < providerConfigs.size(); i++) {
            Options preOptions = pre.getOptions();
            AiModelProviderConfig current = providerConfigs.get(i);
            Options now = current.getOptions();
            if (!preOptions.equals(now)) {
                throw new IllegalArgumentException("you config at @DubboAiService modelConfig Options must same,please check " + pre.getName() + " and " + current.getName());
            }
        }
    }

    private void constructAiConfig(DubboAiService dubboAiService) {
        String path = dubboAiService.configPath();
        if (path.isBlank()) {
            return;
        }
        Map<String, String> props = PropertiesUtil.getPropsByPath(path);
        ApplicationModel.defaultModel().modelEnvironment().updateAppConfigMap(props);
    }


    /**
     * merge method options to class options
     */
    public Options getMethodOptions(Method method) {
        MethodAiMetadata methodAiMetadata = methodMetadataMap.computeIfAbsent(method, key -> new MethodAiMetadata(method));
        Options classOptions = classAiMetadata.getOptions();
        Options methodOptions = methodAiMetadata.getOptions();
        Options options = new Options();
        BeanUtils.copyPropertiesIgnoreNull(classOptions, options);
        BeanUtils.copyPropertiesIgnoreNull(methodOptions, options);
        return options;
    }

    private void constructChatClients() {
        DubboAiService dubboAiService = aiInterfaceClass.getAnnotation(DubboAiService.class);
        String[] providerConfigs = dubboAiService.providerConfigs();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", dubboAiService.model());
        constructAiConfig(dubboAiService);
        LoadBalanceChatModel loadBalanceChatModel = ModelFactory.getLoadBalanceChatModel(Arrays.stream(providerConfigs).toList(), jsonObject);
        this.client = (DefaultChatClient) ChatClient.builder(loadBalanceChatModel).build();
    }

    public DefaultChatClient getClient() {
        return client;
    }


}
