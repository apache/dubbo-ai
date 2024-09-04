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
package org.apache.dubbo.ai.springboot.samples.service;

import org.apache.dubbo.ai.springboot.samples.pojo.UserInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class AiServiceTest {

    @DubboReference
    private SreAiService sreAiService;

    @DubboReference
    private TagAiService tagAiService;

    @DubboReference
    private UserStructAiService userStructAiService;

    @Test
    void processProblemTest() {
        String step = sreAiService.processProblem("ai-service-1-sdsds-vdfg内存使用率过高");
        Assertions.assertNotNull(step);
    }

    @Test
    void tagAiServiceTest() {
        Integer tag = tagAiService.aiTag("一起玩黑神话悟空啊");
        Assertions.assertEquals(1, tag);
        Integer tag2 = tagAiService.aiTag("交个朋友，有时间一起出来玩啊");
        Assertions.assertEquals(2, tag2);
    }

    @Test
    void structTest() {
        UserInfo userInfo = userStructAiService.aiStructMsg("我是xixingya，男,年龄23，在shanghai");
        Assertions.assertEquals(23, userInfo.getAge());
        Assertions.assertEquals("shanghai",userInfo.getAddress());
        Assertions.assertEquals("xixingya",userInfo.getName());
    }
}