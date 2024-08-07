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
package org.apache.dubbo.ai.core.config;

import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.image.ImageOptions;

import java.util.Objects;

public class Options implements ChatOptions, ImageOptions {

    private Float temperature;

    private Float topP;

    private Integer topK;

    private Integer n;

    private String model;

    private Integer width;

    private Integer height;

    private String responseFormat;
    
    private Integer maxTokens;

    private String ext;

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public void setTopP(Float topP) {
        this.topP = topP;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public void setN(Integer n) {
        this.n = n;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public void setResponseFormat(String responseFormat) {
        this.responseFormat = responseFormat;
    }
    
    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public Integer getMaxTokens() {
        return this.maxTokens;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getExt() {
        return ext;
    }

    @Override
    public Float getTemperature() {
        return this.temperature;
    }

    @Override
    public Float getTopP() {
        return this.topP;
    }

    @Override
    public Integer getTopK() {
        return this.topK;
    }

    @Override
    public ChatOptions copy() {
        return null;
    }

    @Override
    public Integer getN() {
        return this.n;
    }

    @Override
    public String getModel() {
        return this.model;
    }

    @Override
    public Integer getWidth() {
        return this.width;
    }

    @Override
    public Integer getHeight() {
        return this.height;
    }

    @Override
    public String getResponseFormat() {
        return this.responseFormat;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Options options)) return false;
        return Objects.equals(getTemperature(), options.getTemperature()) && Objects.equals(getTopP(), options.getTopP()) && Objects.equals(getTopK(), options.getTopK()) && Objects.equals(getN(), options.getN()) && Objects.equals(getModel(), options.getModel()) && Objects.equals(getWidth(), options.getWidth()) && Objects.equals(getHeight(), options.getHeight()) && Objects.equals(getResponseFormat(), options.getResponseFormat()) && Objects.equals(getMaxTokens(), options.getMaxTokens()) && Objects.equals(getExt(), options.getExt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTemperature(), getTopP(), getTopK(), getN(), getModel(), getWidth(), getHeight(), getResponseFormat(), getMaxTokens(), getExt());
    }
}
