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
package org.apache.dubbo.ai.core.chat.model;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuzhifei
 * @since 1.0
 */
public abstract class LoadBalanceChatModel implements ChatModel {


    private AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     * 目前就实现轮询就行，后续再改造
     */
    public ChatModel getChatModel() {
        List<ChatModel> chatModels = getChatModels();
        int size = chatModels.size();
        if (size == 1) {
            return chatModels.get(0);
        }
        if (currentIndex.get() > size - 1) {
            currentIndex.set(0);
        }
        return chatModels.get(currentIndex.get());
    }


    @Override
    public ChatResponse call(Prompt prompt) {
        ChatModel chatModel = getChatModel();
        currentIndex.incrementAndGet();
        return chatModel.call(prompt);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return getChatModel().getDefaultOptions();
    }


    public abstract List<ChatModel> getChatModels();
}
