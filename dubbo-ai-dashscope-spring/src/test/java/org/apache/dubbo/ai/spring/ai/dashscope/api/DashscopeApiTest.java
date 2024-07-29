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
package org.apache.dubbo.ai.spring.ai.dashscope.api;


import org.apache.dubbo.ai.spring.ai.dashscope.DashscopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

class DashscopeApiTest {


    @Test
    void testChatClients() {
        DashscopeChatModel dashscopeChatModel = new DashscopeChatModel(new DashscopeApi(""));
        ChatClient client = ChatClient.builder(dashscopeChatModel).build();
        System.out.println(client.prompt().user("hi").call().content());
    }

    @Test
    void testChatClientsStream() throws InterruptedException {
        DashscopeChatModel dashscopeChatModel = new DashscopeChatModel(new DashscopeApi(""));
        ChatClient client = ChatClient.builder(dashscopeChatModel).build();
        Flux<ChatResponse> chatResponseFlux = client.prompt().user("hi").stream().chatResponse();
        chatResponseFlux.subscribe(data -> {
            System.out.println("get data:"+data.getResult().getOutput().getContent());
        }, data -> {
            System.out.println("get data:"+data);
        }, () -> {
        });
        Thread.sleep(10000);
    }

}