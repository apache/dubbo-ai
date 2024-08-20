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
package org.apache.dubbo.ai.spring.ai.dashscope.metadata;

import com.alibaba.dashscope.aigc.generation.GenerationUsage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;

public class DashscopeChatResponseMetadata extends ChatResponseMetadata {

    protected static final String AI_METADATA_STRING = "{ @type: %1$s, id: %2$s, usage: %3$s }";

    private final DashscopeUsage usage;

    private final String requestId;

    public DashscopeChatResponseMetadata(GenerationUsage usage, String requestId) {
        this.usage = DashscopeUsage.from(usage);
        this.requestId = requestId;
    }

    public static DashscopeChatResponseMetadata from(GenerationUsage usage, String requestId) {
        return new DashscopeChatResponseMetadata(usage, requestId);
    }

    public String getRequestId() {
        return this.requestId;
    }

    public Usage getTokenUsage() {
        return usage != null ? usage : new EmptyUsage();
    }

    @Override
    public Usage getUsage() {
        return getTokenUsage();
    }

    @Override
    public String toString() {
        return AI_METADATA_STRING.formatted(getClass().getName(), getRequestId(), getTokenUsage());
    }

}
