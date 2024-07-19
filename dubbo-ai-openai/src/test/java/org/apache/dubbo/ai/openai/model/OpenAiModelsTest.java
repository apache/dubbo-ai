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
package org.apache.dubbo.ai.openai.model;


import org.apache.dubbo.ai.core.RegisterDubboAiService;
import org.apache.dubbo.ai.openai.MyAiService;
import org.apache.dubbo.ai.openai.function.FunctionAiService;
import org.apache.dubbo.ai.openai.pojo.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OpenAiModelsTest {

    @Test
    void testOpenAiModelConfig() {
        System.setProperty("dubbo.application.serialize-check-status", "DISABLE");
        RegisterDubboAiService.registerServiceInJvm(MyAiService.class);
        MyAiService myAiService = RegisterDubboAiService.getDubboReference(MyAiService.class);
        String hi = myAiService.chat("hi");
        System.out.println(hi);
    }

    @Test
    void testOpenAiObjTransform() {
        System.setProperty("dubbo.application.serialize-check-status", "DISABLE");
        RegisterDubboAiService.registerServiceInJvm(MyAiService.class);
        MyAiService myAiService = RegisterDubboAiService.getDubboReference(MyAiService.class);
        Person person = myAiService.chatTransform("我是xixingya,来自上海,年龄23,公司是apache");
        Assertions.assertEquals(23, person.getAge());
        Assertions.assertEquals("xixingya", person.getName());
        Assertions.assertEquals("apache", person.getCompany());
        Assertions.assertEquals("上海", person.getCity());
    }

    @Test
    void testOpenAiTag() {
        System.setProperty("dubbo.application.serialize-check-status", "DISABLE");
        RegisterDubboAiService.registerServiceInJvm(MyAiService.class);
        MyAiService myAiService = RegisterDubboAiService.getDubboReference(MyAiService.class);
        Integer tag = myAiService.tagMsg("一起玩游戏呀");
        Assertions.assertEquals(1, tag);
        Integer tag2 = myAiService.tagMsg("或许你想来一局王者荣耀");
        Assertions.assertEquals(1, tag2);
        Integer tag3 = myAiService.tagMsg("认识一下吗");
        Assertions.assertEquals(2, tag3);
        Integer tag4 = myAiService.tagMsg("如果你想了解更多详情，可以加个联系方式");
        Assertions.assertEquals(3, tag4);

        Integer tag5 = myAiService.tagMsg("大闸蟹超好吃的！");
        Assertions.assertEquals(4, tag5);
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