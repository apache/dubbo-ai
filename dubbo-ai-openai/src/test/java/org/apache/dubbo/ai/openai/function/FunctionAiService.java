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
package org.apache.dubbo.ai.openai.function;

import org.apache.dubbo.ai.core.DubboAiService;
import org.apache.dubbo.ai.core.Prompt;
import org.apache.dubbo.ai.core.function.FunctionCall;

@DubboAiService(configPath = "dubbo-ai.properties", providerConfigs = {"m1", "m2"}, model = "gpt-3.5-turbo")
public interface FunctionAiService {

    @Prompt(
            """
                    {value}
                    """)
    @FunctionCall(functionClasses = MyAiFunction.class)
    String temp(String value);

    @Prompt(
            """
                    calculate is the 2 words len
                    word1={word1} word2={word2}
                    """)
    @FunctionCall(functionClasses = MyAiFunction.class)
    String sum2Words(String word1,String word2);
}
