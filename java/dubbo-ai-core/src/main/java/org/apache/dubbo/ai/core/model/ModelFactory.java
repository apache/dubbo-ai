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
package org.apache.dubbo.ai.core.model;

import org.apache.dubbo.ai.core.DubboAiContext;
import org.apache.dubbo.ai.core.chat.model.ChatModel;
import org.apache.dubbo.ai.core.chat.model.DefaultLoadBalanceChatModel;
import org.apache.dubbo.ai.core.chat.model.LoadBalanceChatModel;
import org.apache.dubbo.ai.core.config.AiModelProviderConfig;
import org.apache.dubbo.ai.core.config.Options;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.ArrayList;
import java.util.List;

public class ModelFactory {

    public static LoadBalanceChatModel getLoadBalanceChatModel(DubboAiContext dubboAiContext) {
        List<AiModelProviderConfig> aiModelProviderConfigs = dubboAiContext.getAiModelProviderConfigs();
        Options options = dubboAiContext.getOptions();
        List<ChatModel> res = new ArrayList<>();
        for (AiModelProviderConfig aiModelProviderConfig : aiModelProviderConfigs) {
            String providerCompany = aiModelProviderConfig.getProviderCompany();
            AiModels models = ApplicationModel.defaultModel().getExtension(AiModels.class, providerCompany);
            ChatModel chatModel = models.getChatModel(aiModelProviderConfig, options);
            res.add(chatModel);
        }
        return new DefaultLoadBalanceChatModel(res);
    }
}
