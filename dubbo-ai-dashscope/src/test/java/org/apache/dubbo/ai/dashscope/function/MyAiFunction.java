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
package org.apache.dubbo.ai.dashscope.function;

import org.apache.dubbo.ai.core.function.AiFunction;

public class MyAiFunction {

    public record A(String city) {

    }

    public record TempResponse(int minTemp,int maxTemp,int avgTemp){

    }

    public record WordsDto(String word1, String word2) {

    }

    @AiFunction("get the city temp")
    public TempResponse temp(A a) {
        System.out.println("city:" + a.city + " temp = " + 23);
        return new TempResponse(23,30,25);
    }

    @AiFunction("sum 2 words length")
    public int sum2Words(WordsDto wordsDto) {
        int len = wordsDto.word1.length() + wordsDto.word2.length();
        System.out.println("words len=0" + len);
        return len;
    }
}
