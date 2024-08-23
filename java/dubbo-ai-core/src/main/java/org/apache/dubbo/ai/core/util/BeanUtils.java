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
package org.apache.dubbo.ai.core.util;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanUtils {


    public static void copyPropertiesIgnoreNull(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source, ignoreProperties));
    }

    public static String[] getNullPropertyNames(Object source, String... ignoreProperties) {
        Set<String> ignoreSet = new HashSet<>();
        if (ignoreProperties != null) {
            ignoreSet.addAll(List.of(ignoreProperties));
        }

        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        for (java.beans.PropertyDescriptor pd : pds) {
            if (ignoreSet.contains(pd.getName())) {
                continue;
            }
            try {
                Object srcValue = src.getPropertyValue(pd.getName());
                if (srcValue == null) {
                    ignoreSet.add(pd.getName());
                }
            } catch (Exception e) {
                // if the property is not readable or throws exception, ignore it
                ignoreSet.add(pd.getName());
            }
        }

        String[] result = new String[ignoreSet.size()];
        return ignoreSet.toArray(result);
    }

    public static boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() ||
                clazz == Boolean.class ||
                clazz == Character.class ||
                clazz == Byte.class ||
                clazz == Short.class ||
                clazz == Integer.class ||
                clazz == Long.class ||
                clazz == Float.class ||
                clazz == Double.class;
    }
    
    public static boolean isPrimitiveOrWrapperOrString(Class<?> clazz) {
        return isPrimitiveOrWrapper(clazz)|| clazz == String.class;
    }
    
    public static Map<String,Field> getAllFields(Class<?> clazz) {
        Map<String,Field> fieldMap = new HashMap<>();
        while (clazz != null && clazz != Object.class) {
            for (Field f : clazz.getDeclaredFields()) {
                if (!f.isSynthetic()) {
                    fieldMap.putIfAbsent(f.getName(), f);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fieldMap;
    }

}
