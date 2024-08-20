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
package org.apache.dubbo.ai.core.model.parser;

import com.alibaba.fastjson2.JSON;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class AiResponseParser {


    public static Object parse(String content, Class<?> returnType) {

        if (PrimitiveParserFactory.isPrimitiveOrWrapper(returnType)) {
            return PrimitiveParserFactory.getParser(returnType).parse(content);
        }
        if (returnType == List.class) {
            return asList(content.split("\n"));
        }

        if (returnType == Set.class) {
            return new HashSet<>(asList(content.split("\n")));
        }
        String s = clearMarkdown(content);
        return JSON.parseObject(s, returnType);
    }

    public static String clearMarkdown(String content) {
        if (content.contains("```json")) {
            int start = content.indexOf("```json") + 7;
            int end = content.indexOf("```", start);
            if (end != -1) {
                return content.substring(start, end).trim();
            }
        }
        return content;
    }
}
