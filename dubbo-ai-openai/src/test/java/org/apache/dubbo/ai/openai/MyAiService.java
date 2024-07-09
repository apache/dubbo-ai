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
package org.apache.dubbo.ai.openai;

import org.apache.dubbo.ai.core.DubboAiService;
import org.apache.dubbo.ai.core.Prompt;
import org.apache.dubbo.ai.openai.pojo.Person;
import org.apache.dubbo.common.stream.StreamObserver;


@DubboAiService(configPath = "dubbo-ai.properties", providerConfigs = {"m1", "m2"},model = "gpt-4-all")
public interface MyAiService {


    @Prompt(
            """
            请用中文回答我的这个问题:  {userMessage}
            """)
    String chat(String userMessage);

    @Prompt("""
            你是一个超高级的人工智能，请你以json的map格式来把下面一段话转成Person的json str格式，Person包含以下字段
            name:名称，age:年龄，company:公司,city:城市，返回格式为{"name":"xxx","age":1,company:"xxx","city":"xxx"}，
            下面是请你提取的msg：
            {userMessage}
            """)
    Person chatTransform(String userMessage);


    


    @Prompt("""
            请用中文回答我的这个问题:  {userMessage}
            """)
    void chat(String userMessage, StreamObserver<String> response);
}