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

import com.alibaba.fastjson2.JSONObject;
import org.apache.dubbo.ai.core.DubboAiService;
import org.apache.dubbo.ai.core.Prompt;
import org.apache.dubbo.ai.spring.boot.test.pojo.Person;

@DubboAiService(providerConfigs = "m1",configPath = "dubbo-ai.properties")
public interface MyAiService {
    
    @Prompt("""
            你是一个超高级的人工智能，请你以json的map格式回答一下问题: {userMessage}
            """)
    String chat(String userMessage);

    @Prompt("""
            你是一个超高级的人工智能，请你以json的map格式回答一下问题: {userMessage}
            """)
    JSONObject chatObj(String userMessage);

    @Prompt("""
            你是一个超高级的人工智能，请你以json的map格式来把下面一段话转成Person的json str格式，Person包含以下字段
            name:名称，age:年龄，company:公司，返回格式为{"name":"xxx","age":1,company:"xxx"}，下面是请你提取的msg：
            {userMessage}
            """)
    Person chatTransformPerson(String userMessage);
}
