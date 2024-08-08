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
package org.apache.dubbo.ai.core.type;

import org.apache.dubbo.ai.core.DubboAiService;
import org.apache.dubbo.ai.core.config.AiModelProviderConfig;
import org.apache.dubbo.ai.core.config.Configs;
import org.apache.dubbo.ai.core.config.Options;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassAiMetadata implements AiMetadata {

    private final Class<?> clazz;

    private DubboAiService dubboAiService;

    private Options options;

    List<AiModelProviderConfig> aiModelProviderConfigs;

    public ClassAiMetadata(Class<?> targetClass) {
        dubboAiService = targetClass.getAnnotation(DubboAiService.class);
        this.clazz = targetClass;
        buildAiModelProviderConfigs();
    }

    @Override
    public Options getOptions() {
        return this.options;
    }

    private void buildAiModelProviderConfigs() {
        List<String> configModelNames = Arrays.stream(dubboAiService.providerConfigs()).toList();
        List<AiModelProviderConfig> aiModelProviderConfigs = new ArrayList<>();
        for (String configModelName : configModelNames) {
            AiModelProviderConfig aiModelProviderConfig = Configs.buildFromConfigurations(configModelName);
            aiModelProviderConfig.getOptions().setModel(dubboAiService.model());
            compareOptions(aiModelProviderConfig.getOptions());
            aiModelProviderConfigs.add(aiModelProviderConfig);
        }
        this.aiModelProviderConfigs = aiModelProviderConfigs;
    }

    private void compareOptions(Options targetOptions) {
        if (this.options == null) {
            this.options = targetOptions;
            return;
        }
        if (options.equals(targetOptions)) {
            return;
        }
        throw new IllegalArgumentException("you config at @DubboAiService modelConfig Options must same,please check  class "+clazz.getName());
    }

    public List<AiModelProviderConfig> getProviderConfigs() {
        return aiModelProviderConfigs;
    }
}
