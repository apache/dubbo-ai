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
import org.apache.dubbo.common.config.PrefixedConfiguration;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.beans.PropertyDescriptor;

public class Configs {

    private static final String PROVIDER_FORMAT = "dubbo.ai.%s.provider";

    private static final String BASEURL_FORMAT = "dubbo.ai.%s.baseUrl";

    private static final String OPTIONS_FORMAT = "dubbo.ai.%s.options";

    private static final String SK_FORMAT = "dubbo.ai.%s.sk";

    private static final String LOAD_BALANCE_PROVIDERS = "dubbo.ai.%s.loadbalance.providers";

    private static final String AI_SERVICE_REGISTER = "dubbo.ai.service.register";

    public static AiModelProviderConfig buildFromConfigurations(String name) {
        CompositeConfiguration configuration = ApplicationModel.defaultModel().modelEnvironment().getConfiguration();
        AiModelProviderConfig aiModelProviderConfig = new AiModelProviderConfig();
        aiModelProviderConfig.setName(name);
        aiModelProviderConfig.setProviderCompany(configuration.getString(String.format(PROVIDER_FORMAT, name)));
        aiModelProviderConfig.setBaseUrl(configuration.getString(String.format(BASEURL_FORMAT, name)));
        aiModelProviderConfig.setSecretKey(configuration.getString(String.format(SK_FORMAT, name)));
        var prefix = String.format(OPTIONS_FORMAT, name);
        var prefixedConfiguration = new PrefixedConfiguration(configuration, prefix);
        List<String> properties = getProperties(Options.class);
        Options options = new Options();
        BeanWrapper wrapper = new BeanWrapperImpl(options);
        for (String propertyName : properties) {
            var value = prefixedConfiguration.getString(propertyName);
            wrapper.setPropertyValue(propertyName, value);
        }
        aiModelProviderConfig.setOptions(options);
        return aiModelProviderConfig;
    }

    public static String getAiServiceRegister() {
        CompositeConfiguration configuration = ApplicationModel.defaultModel().modelEnvironment().getConfiguration();
        return configuration.getString(AI_SERVICE_REGISTER);
    }

    public static List<String> getProperties(Class<?> clazz) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(clazz))
                .map(PropertyDescriptor::getName)
                .filter(name -> !name.equals("class"))
                .collect(Collectors.toList());
    }
}
