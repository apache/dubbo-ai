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
package org.apache.dubbo.ai.core.proxy;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class ProxyGenerator {

    public static <T> T createProxy(Class<T> interfaceClass) {
        try {
            // 使用 ByteBuddy 创建代理类
            Class<?> dynamicType = new ByteBuddy()
                    .subclass(Object.class)
                    .implement(interfaceClass)
                    .method(ElementMatchers.isDeclaredBy(interfaceClass))
                    .intercept(MethodDelegation.to(new AiServiceInterfaceImpl(interfaceClass)))
                    .make()
                    .load(interfaceClass.getClassLoader())
                    .getLoaded();

            return (T) dynamicType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create proxy instance", e);
        }
    }
}
