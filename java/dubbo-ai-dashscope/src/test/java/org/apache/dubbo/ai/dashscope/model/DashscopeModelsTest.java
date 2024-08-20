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
package org.apache.dubbo.ai.dashscope.model;

import org.apache.dubbo.ai.core.RegisterDubboAiService;
import org.apache.dubbo.ai.dashscope.MyAiService;
import org.apache.dubbo.ai.dashscope.function.FunctionAiService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DashscopeModelsTest {


    @Test
    void testModelConfig() {
        System.setProperty("dubbo.application.serialize-check-status", "DISABLE");
        RegisterDubboAiService.registerServiceInJvm(MyAiService.class);
        MyAiService myAiService = RegisterDubboAiService.getDubboReference(MyAiService.class);
        String hi = myAiService.chat("hi");
        System.out.println(hi);
    }

    @Test
    void testFunctionCall() {
        System.setProperty("dubbo.application.serialize-check-status", "DISABLE");
        RegisterDubboAiService.registerServiceInJvm(FunctionAiService.class);
        FunctionAiService functionAiService = RegisterDubboAiService.getDubboReference(FunctionAiService.class);
        String shanghai = functionAiService.temp("please get shanghai temp");
        System.out.println(shanghai);
        Assertions.assertTrue(shanghai.contains("23"));
        String s = functionAiService.sum2Words("hello", "world");
        System.out.println(s);
        Assertions.assertTrue(s.contains("10"));
    }

}