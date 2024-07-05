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
package org.apache.dubbo.ai.core.config;

import org.apache.dubbo.common.config.CompositeConfiguration;
import org.apache.dubbo.rpc.model.ApplicationModel;

public class Configs {

    private static final String PROVIDER_FORMAT = "dubbo.ai.%s.provider";

    private static final String BASEURL_FORMAT = "dubbo.ai.%s.baseUrl";

    private static final String SK_FORMAT = "dubbo.ai.%s.sk";

    private static final String LOAD_BALANCE_PROVIDERS = "dubbo.ai.%s.loadbalance.providers";

    public static AiModelProviderConfig buildFromConfigurations(String name) {
        CompositeConfiguration configuration = ApplicationModel.defaultModel().modelEnvironment().getConfiguration();
        AiModelProviderConfig aiModelProviderConfig = new AiModelProviderConfig();
        aiModelProviderConfig.setName(name);
        aiModelProviderConfig.setProviderCompany(configuration.getString(String.format(PROVIDER_FORMAT, name)));
        aiModelProviderConfig.setBaseUrl(configuration.getString(String.format(BASEURL_FORMAT, name)));
        aiModelProviderConfig.setSecretKey(configuration.getString(String.format(SK_FORMAT, name)));
        return aiModelProviderConfig;
    }
}
