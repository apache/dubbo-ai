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

import org.apache.dubbo.ai.core.DubboAiService;
import org.apache.dubbo.ai.core.Prompt;

@DubboAiService(modelProvider = "m1",configPath = "dubbo-ai.properties")
public interface MyAiService {
    
    @Prompt("""
            你是一个超高级的人工智能，请你以json的map格式回答一下问题: {userMessage}
            """)
    String chat(String userMessage);
}
