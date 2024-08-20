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
package org.apache.dubbo.ai.openai.model;

import org.apache.dubbo.ai.core.chat.model.ChatModel;
import org.apache.dubbo.ai.core.config.AiModelProviderConfig;
import org.apache.dubbo.ai.core.config.Options;
import org.apache.dubbo.ai.core.model.AiModels;
import org.apache.dubbo.ai.core.util.BeanUtils;
import org.apache.dubbo.ai.openai.chat.model.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.HashMap;
import java.util.Map;

public class OpenAiModels implements AiModels {

    private final Map<String, OpenAiApi> cachedConfig = new HashMap<>();

    @Override
    public ChatModel getChatModel(AiModelProviderConfig aiModelProviderConfig, Options chatOptions) {
        OpenAiChatOptions target = new OpenAiChatOptions();
        BeanUtils.copyPropertiesIgnoreNull(chatOptions, target, "topK");
        return new OpenAiChatModel(getOpenAiConfig(aiModelProviderConfig), target);
    }

    private OpenAiApi getOpenAiConfig(AiModelProviderConfig aiModelProviderConfig) {
        String name = aiModelProviderConfig.getName();
        if (!cachedConfig.containsKey(name)) {
            cachedConfig.put(name, buildOpenAiApi(aiModelProviderConfig));
        }
        return cachedConfig.get(name);
    }

    private OpenAiApi buildOpenAiApi(AiModelProviderConfig aiModelProviderConfig) {
        String providerCompany = aiModelProviderConfig.getProviderCompany();
        if (!providerCompany.equals("openai")) {
            throw new RuntimeException("not support company");
        }
        String baseUrl = aiModelProviderConfig.getBaseUrl();
        String secretKey = aiModelProviderConfig.getSecretKey();
        if (baseUrl != null) {
            return new OpenAiApi(baseUrl, secretKey);
        } else {
            return new OpenAiApi(secretKey);
        }
    }

}
