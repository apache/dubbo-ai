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
package org.apache.dubbo.ai.core.function;

import org.springframework.ai.model.function.FunctionCallbackWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionFactory {
    private static final Map<Class<?>, List<FunctionCallbackWrapper<?, ?>>> FUNCTION_MAPS = new HashMap<>();

    public static List<FunctionCallbackWrapper<?, ?>> getFunctionsByClass(Class<?> clazz) {
        return FUNCTION_MAPS.computeIfAbsent(clazz, key -> {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                Object o = constructor.newInstance();
                return FunctionCreator.getAiFunctions(o);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
