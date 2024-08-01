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

import org.apache.dubbo.ai.core.Options;
import org.apache.dubbo.ai.core.Prompt;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodAiMetadata implements AiMetadata {


    private Options optionsAnnotation;

    private Prompt prompt;

    private org.apache.dubbo.ai.core.config.Options options;

    public MethodAiMetadata(Method method) {
        if (method.isAnnotationPresent(Options.class)) {
            optionsAnnotation = method.getAnnotation(Options.class);
        }
        createOptions();

    }

    private void createOptions() {
        if (optionsAnnotation == null) {
            options = new org.apache.dubbo.ai.core.config.Options();
        }
        Class<? extends Options.OptionsProvider> changedOptions = optionsAnnotation.getChangedOptions();
        try {
            Options.OptionsProvider optionsProvider = changedOptions.getDeclaredConstructor().newInstance();
            options = optionsProvider.getChangedOptions(optionsAnnotation);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public org.apache.dubbo.ai.core.config.Options getOptions() {
        return options;
    }
}
