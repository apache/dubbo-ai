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

import java.util.HashSet;
import java.util.List;
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
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                ignoreSet.add(pd.getName());
            }
        }

        String[] result = new String[ignoreSet.size()];
        return ignoreSet.toArray(result);
    }

}
