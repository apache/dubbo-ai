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
package org.apache.dubbo.ai.core;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE,TYPE})
@Retention(RUNTIME)
@Documented
public @interface Options {

    float temperature() default DEFAULT_TEMPERATURE;
    
    String model() default DEFAULT_MODEL;

    float topP() default DEFAULT_TOP_P;

    int topK() default DEFAULT_TOP_K;

    int maxTokens() default DEFAULT_MAX_TOKENS;

    int n() default DEFAULT_N;

    String responseFormat() default DEFAULT_RESPONSE_FORMAT;

    float DEFAULT_TEMPERATURE = -1f;

    float DEFAULT_TOP_P = -1f;

    int DEFAULT_TOP_K = -1;

    int DEFAULT_MAX_TOKENS = -1;

    int DEFAULT_N = -1;

    String DEFAULT_RESPONSE_FORMAT = "";
    
    String DEFAULT_MODEL = "";
    

    class OptionsOperator {
        
        public static org.apache.dubbo.ai.core.config.Options getChangedOptions(Options options) {
            org.apache.dubbo.ai.core.config.Options res = new org.apache.dubbo.ai.core.config.Options();
            if (options.temperature() != DEFAULT_TEMPERATURE) {
                res.setTemperature(options.temperature());
            }
            if (options.topP() != DEFAULT_TOP_P) {
                res.setTopP(options.topP());
            }
            if (options.topK() != DEFAULT_TOP_K) {
                res.setTopK(options.topK());
            }
            if (options.maxTokens() != DEFAULT_MAX_TOKENS) {
                res.setMaxTokens(options.maxTokens());
            }
            if (options.n() != DEFAULT_N) {
                res.setN(options.n());
            }
            if (!options.responseFormat().equals(DEFAULT_RESPONSE_FORMAT)) {
                res.setResponseFormat(options.responseFormat());
            }
            if(!options.model().equals(DEFAULT_MODEL)){
                res.setModel(options.model());
            }
            return res;
        }
    }
}
