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
package org.apache.dubbo.ai.core.function;

import java.util.function.Function;

public class FunctionInfo<T, I, O> {

    private final String name;

    private final String desc;

    private final Class<T> inputType;

    private final Function<I, O> function;

    public FunctionInfo(String name, String desc, Class<T> inputType, Function<I, O> function) {
        this.name = name;
        this.desc = desc;
        this.inputType = inputType;
        this.function = function;
    }


    public Function<I, O> getFunction() {
        return this.function;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Class<?> getInputType() {
        return inputType;
    }
}
