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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FunctionFactory {
    private static final Map<Class<?>, List<FunctionInfo<?, ?>>> FUNCTION_MAPS = new ConcurrentHashMap<>();

    private static final Map<Method, List<FunctionInfo<?, ?>>> CACHED_METHOD_FUNCTIONS_MAP = new ConcurrentHashMap<>();

    public static List<FunctionInfo<?, ?>> getFunctionsByClass(Class<?> clazz) {
        return FUNCTION_MAPS.computeIfAbsent(clazz, key -> {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                Object o = constructor.newInstance();
                return Collections.unmodifiableList(FunctionCreator.getAiFunctions(o));
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public static List<FunctionInfo<?, ?>> getFunctionsByMethod(Method method) {
        if (!method.isAnnotationPresent(FunctionCall.class)) {
            return Collections.emptyList();
        }
        return CACHED_METHOD_FUNCTIONS_MAP.computeIfAbsent(method, key -> {
            FunctionCall functionCall = method.getAnnotation(FunctionCall.class);
            Class<?>[] classes = functionCall.functionClasses();
            List<FunctionInfo<?, ?>> result = new ArrayList<>();
            for (Class<?> functionClass : classes) {
                result.addAll(getFunctionsByClass(functionClass));
            }
            return Collections.unmodifiableList(result);
        });
    }
}
