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
import org.apache.dubbo.ai.core.Options;
import org.apache.dubbo.ai.core.Prompt;
import org.apache.dubbo.ai.core.Val;
import org.apache.dubbo.ai.openai.pojo.ChatMsg;
import org.apache.dubbo.ai.openai.pojo.Person;
import org.apache.dubbo.common.stream.StreamObserver;

import java.util.List;


@DubboAiService(configPath = "dubbo-ai.properties", providerConfigs = {"m1", "m2"}, model = "deepseek-chat")
public interface MyAiService {


    @Prompt(
            """
                    请用中文回答我的这个问题:  {userMessage}
                    """)
    @Options(maxTokens = 4096)
    String chat(String userMessage);

    @Prompt("""
            你是一个超高级的人工智能，请你以json的map格式来把下面一段话转成Person的json str格式，Person包含以下字段
            name:名称，age:年龄，company:公司,city:城市，返回格式为{"name":"xxx","age":1,company:"xxx","city":"xxx"}，
            下面是请你提取的msg：
            {userMessage}
            """)
    Person chatTransform(String userMessage);


    @Prompt("""
            我们现在有以下几个标签，1.游戏 2.交友 3.引流 4.吃喝 5.其他
            其中，交友和引流的区别是，交友是单纯的一起聊天，引流是某个话题不说完整，吸引用户加好友或者私聊。
            请你根据上述标签给下面的文本打标，请你只返回标签对应的数字，只返回一个int的数字
            下面是文本：
            {userMessage}
            """)
    @Options(model = "gpt-4o")
    Integer tagMsg(String userMessage);


    @Prompt("""
            请用中文回答我的这个问题:  {msg}
            """)
    void chatStream(@Val("msg") String userMessage, StreamObserver<String> response);


    @Prompt("""
            你是一个人类,你的名字是{na}，请你根据{topic}主题,生成{count}个问题,问题为一句话，不要换行，每个问题用\n 分隔。
            """)
    @Options(model = "gpt-4o")
    List<String> complexChat(ChatMsg chatMsg);
}