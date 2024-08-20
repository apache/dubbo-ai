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
package org.apache.dubbo.ai.spring.boot.test.service;

import org.apache.dubbo.ai.spring.boot.test.pojo.Person;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@Service
public class AiService implements ApplicationRunner {

    @DubboReference
    private MyAiService myAiService;

    public String chat(String msg) {
        return myAiService.chat(msg);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        
        System.out.println(myAiService.chatObj("hi，你是谁"));
        System.out.println("---------------------");
        Person person = myAiService.chatTransformPerson("我是qaq，年龄23，目前就职与apache公司");
        System.out.println(person.toString());
    }
}
