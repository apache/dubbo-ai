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
package org.apache.dubbo.ai.openai.chat.model;

import org.apache.dubbo.ai.core.chat.model.ChatModel;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.retry.support.RetryTemplate;

public class OpenAiChatModel extends org.springframework.ai.openai.OpenAiChatModel implements ChatModel {
    public OpenAiChatModel(OpenAiApi openAiApi) {
        super(openAiApi);
    }

    public OpenAiChatModel(OpenAiApi openAiApi, OpenAiChatOptions options) {
        super(openAiApi, options);
    }

    public OpenAiChatModel(OpenAiApi openAiApi, OpenAiChatOptions options, FunctionCallbackContext functionCallbackContext, RetryTemplate retryTemplate) {
        super(openAiApi, options, functionCallbackContext, retryTemplate);
    }
}
