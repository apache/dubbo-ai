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
package org.apache.dubbo.ai.spring.ai.dashscope;

import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.tools.FunctionDefinition;
import com.alibaba.dashscope.tools.ToolBase;
import com.alibaba.dashscope.tools.ToolFunction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonParser;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashscopeChatOptions implements FunctionCallingOptions, ChatOptions {

    // @formatter:off
    /** ID of the model to use. */
    @JsonProperty("model")
    private String model;

    @JsonIgnore private Boolean stream;

    /**
     * 用于控制随机性和多样性的程度。具体来说，temperature值控制了生成文本时对每个候选词的概率分布进行平滑的程度。较高的temperature值会降低概率分布的峰值，使得更多的低概率词被选择，生成结果更加多样化；而较低的temperature值则会增强概率分布的峰值，使得高概率词更容易被选择，生成结果更加确定。
     * 取值范围：[0, 2)，系统默认值0.85。不建议取值为0，无意义。
     */
    private @JsonProperty("temperature") Float temperature;

    /**
     * 生成时，核采样方法的概率阈值。例如，取值为0.8时，仅保留累计概率之和大于等于0.8的概率分布中的token，作为随机采样的候选集。取值范围为（0,1.0)，取值越大，生成的随机性越高；取值越低，生成的随机性越低。默认值为0.8。注意，取值不要大于等于1
     */
    private @JsonProperty("top_p") Float topP;

    /**
     * 生成时，采样候选集的大小。例如，取值为50时，仅将单次生成中得分最高的50个token组成随机采样的候选集。取值越大，生成的随机性越高；取值越小，生成的确定性越高。注意：如果top_k参数为空或者top_k的值大于100，表示不启用top_k策略，此时仅有top_p策略生效，默认是空。
     */
    private @JsonProperty("top_k") Integer topK;
    
    private @JsonProperty("max_tokens") Integer maxTokens;

    /**
     * "text"表示旧版本的text
     *
     * <p>"message"表示兼容openai的message
     */
    private @JsonProperty("result_format") String resultFormat = "message";

    /**
     *
     *
     * <ul>
     *   <li>stop参数用于实现内容生成过程的精确控制，在生成内容即将包含指定的字符串或token_ids时自动停止，生成内容不包含指定的内容。
     *       <p>例如，如果指定stop为"你好"，表示将要生成"你好"时停止；如果指定stop为[37763, 367]，表示将要生成"Observation"时停止。
     *   <li>stop参数支持以list方式传入字符串数组或者token_ids数组，支持使用多个stop的场景。
     * </ul>
     *
     * <q>说明 list模式下不支持字符串和token_ids混用，list模式下元素类型要相同。</q>
     */
    private @JsonProperty("stop") List<String> stop;

    /**
     * 模型内置了互联网搜索服务，该参数控制模型在生成文本时是否参考使用互联网搜索结果。取值如下：
     *
     * <ul>
     *   <li>true：启用互联网搜索，模型会将搜索结果作为文本生成过程中的参考信息，但模型会基于其内部逻辑“自行判断”是否使用互联网搜索结果。
     *   <li>false（默认）：关闭互联网搜索。
     * </ul>
     */
    private @JsonProperty("enable_search") Boolean enableSearch = false;

    /** 用于控制模型生成时的重复度。提高repetition_penalty时可以降低模型生成的重复度。1.0表示不做惩罚。默认为1.1。 */
    private @JsonProperty("repetition_penalty") Float repetitionPenalty;

    /** 模型可选调用的工具列表。目前仅支持function，并且即使输入多个function，模型仅会选择其中一个生成结果。模型根据tools参数内容可以生产函数调用的参数 */
    private @JsonProperty("tools") List<ToolBase> tools;

    /**
     * OpenAI Tool Function Callbacks to register with the ChatClient. For Prompt Options the
     * functionCallbacks are automatically enabled for the duration of the prompt execution. For
     * Default Options the functionCallbacks are registered but disabled by default. Use the
     * enableFunctions to set the functions from the registry to be used by the ChatClient chat
     * completion requests.
     */
    @NestedConfigurationProperty @JsonIgnore
    private List<FunctionCallback> functionCallbacks = new ArrayList<>();

    /**
     * List of functions, identified by their names, to configure for function calling in the chat
     * completion requests. Functions with those names must exist in the functionCallbacks registry.
     * The {@link #functionCallbacks} from the PromptOptions are automatically enabled for the
     * duration of the prompt execution.
     *
     * <p>Note that function enabled with the default options are enabled for all chat completion
     * requests. This could impact the token count and the billing. If the functions is set in a
     * prompt options, then the enabled functions are only active for the duration of this prompt
     * execution.
     */
    @NestedConfigurationProperty @JsonIgnore private Set<String> functions = new HashSet<>();

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }

    @Override
    public Float getTemperature() {
        return this.temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    @Override
    public Float getTopP() {
        return this.topP;
    }

    public void setTopP(Float topP) {
        this.topP = topP;
    }

    @Override
    public Integer getTopK() {
        return this.topK;
    }

    @Override 
    public ChatOptions copy() {
        return fromOptions(this);
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public List<String> getStop() {
        return stop==null?new ArrayList<>():stop;
    }

    public void setStop(List<String> stop) {
        this.stop = stop;
    }

    public Boolean getEnableSearch() {
        return enableSearch;
    }

    public void setEnableSearch(Boolean enableSearch) {
        this.enableSearch = enableSearch;
    }
    
    public Integer getMaxTokens() {
        return maxTokens;
    }
    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
    public Float getRepetitionPenalty() {
        return repetitionPenalty;
    }

    public void setRepetitionPenalty(Float repetitionPenalty) {
        this.repetitionPenalty = repetitionPenalty;
    }

    public List<ToolBase> getTools() {
        return transformFunction();
    }

    public void setTools(List<ToolBase> tools) {
        this.tools = tools;
    }

    @Override
    public List<FunctionCallback> getFunctionCallbacks() {
        return this.functionCallbacks;
    }

    @Override
    public void setFunctionCallbacks(List<FunctionCallback> functionCallbacks) {
        this.functionCallbacks = functionCallbacks;
    }

    @Override
    public Set<String> getFunctions() {
        return this.functions;
    }

    @Override
    public void setFunctions(Set<String> functions) {
        this.functions = functions;
    }

    public String getResultFormat() {
        return resultFormat;
    }

    public void setResultFormat(String resultFormat) {
        this.resultFormat = resultFormat;
    }
    public static DashscopeChatOptionsBuilder builder() {
        return new DashscopeChatOptionsBuilder();
    }

    public static class DashscopeChatOptionsBuilder {
        private DashscopeChatOptions options;

        public DashscopeChatOptionsBuilder() {
            this.options = new DashscopeChatOptions();
        }

        public DashscopeChatOptionsBuilder withModel(String model) {
            this.options.model = model;
            return this;
        }

        public DashscopeChatOptionsBuilder withTemperature(Float temperature) {
            this.options.temperature = temperature;
            return this;
        }

        public DashscopeChatOptionsBuilder withTopP(Float topP) {
            this.options.topP = topP;
            return this;
        }

        public DashscopeChatOptionsBuilder withTopK(Integer topK) {
            this.options.topK = topK;
            return this;
        }

        public DashscopeChatOptionsBuilder withStop(List<String> stop) {
            this.options.stop = stop;
            return this;
        }

        public DashscopeChatOptionsBuilder withEnableSearch(Boolean enableSearch) {
            this.options.enableSearch = enableSearch;
            return this;
        }

        public DashscopeChatOptionsBuilder withRepetitionPenalty(Float repetitionPenalty) {
            this.options.repetitionPenalty = repetitionPenalty;
            return this;
        }

        public DashscopeChatOptionsBuilder withTools(List<ToolBase> tools) {
            this.options.tools = tools;
            return this;
        }

        public DashscopeChatOptionsBuilder withStream(Boolean stream) {
            this.options.stream = stream;
            return this;
        }

        public DashscopeChatOptionsBuilder withResultFormat(String resultFormat) {
            this.options.resultFormat = resultFormat;
            return this;
        }

        public DashscopeChatOptionsBuilder withFunctionCallbacks(
                List<FunctionCallback> functionCallbacks) {
            this.options.functionCallbacks = functionCallbacks;
            return this;
        }

        public DashscopeChatOptionsBuilder withFunction(String functionName) {
            Assert.hasText(functionName, "Function name must not be empty");
            this.options.functions.add(functionName);
            return this;
        }

        public DashscopeChatOptionsBuilder withFunctions(Set<String> functionNames) {
            Assert.notNull(functionNames, "Function names must not be null");
            this.options.functions = functionNames;
            return this;
        }

        public DashscopeChatOptions build() {
            return this.options;
        }
    }

    public static DashscopeChatOptions fromOptions(DashscopeChatOptions fromOptions) {
        return DashscopeChatOptions.builder()
                .withModel(fromOptions.getModel())
                .withTemperature(fromOptions.getTemperature())
                .withTopP(fromOptions.getTopP())
                .withTopK(fromOptions.getTopK())
                .withStop(fromOptions.getStop())
                .withStream(fromOptions.getStream())
                .withEnableSearch(fromOptions.enableSearch)
                .withResultFormat(fromOptions.getResultFormat())
                .withFunctionCallbacks(fromOptions.getFunctionCallbacks())
                .withFunctions(fromOptions.getFunctions())
                .withRepetitionPenalty(fromOptions.getRepetitionPenalty())
                .withTools(fromOptions.getTools())
                .build();
    }
    
    private List<ToolBase> transformFunction() {
        List<ToolBase> toolBaseList = new ArrayList<>();
        for (FunctionCallback functionCallback : getFunctionCallbacks()) {
            var builder = ToolFunction.builder();
            var functionDefinitionBuilder = FunctionDefinition.builder();
            functionDefinitionBuilder.name(functionCallback.getName());
            functionDefinitionBuilder.description(functionCallback.getDescription());
            functionDefinitionBuilder.parameters(JsonParser.parseString(functionCallback.getInputTypeSchema()).getAsJsonObject());
            builder.function(functionDefinitionBuilder.build());
            toolBaseList.add(builder.build());
        }
        return toolBaseList;
    }

    public GenerationParam toGenerationParam() {
        GenerationParam.GenerationParamBuilder<?,?> builder = GenerationParam.builder();
        builder.model(this.model)
                .temperature(this.temperature)
                .topP(this.topP==null?null:this.topP.doubleValue())
                .topK(this.topK)
                .maxTokens(this.maxTokens)
                .tools(getTools())
                .stopStrings(this.getStop())
                .enableSearch(this.enableSearch)
                .resultFormat(this.resultFormat)
                .repetitionPenalty(this.repetitionPenalty);
        return builder.build();
    }
}
