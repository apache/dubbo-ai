package org.apache.dubbo.ai.openai.model;

import com.alibaba.fastjson2.JSONObject;
import org.apache.dubbo.ai.core.chat.model.ChatModel;
import org.apache.dubbo.ai.core.config.AiModelProviderConfig;
import org.apache.dubbo.ai.core.config.Configs;
import org.apache.dubbo.ai.core.model.AiModels;
import org.apache.dubbo.ai.openai.chat.model.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

import java.util.HashMap;
import java.util.Map;

public class OpenAiModels implements AiModels {

    private final Map<String, OpenAiApi> cachedApi = new HashMap<>();

    @Override
    public ChatModel getChatModel(String configModelName, JSONObject chatOptions) {
        OpenAiApi openAiApi = getOpenAiApi(configModelName);
        return new OpenAiChatModel(openAiApi, chatOptions.to(OpenAiChatOptions.class));
    }

    private OpenAiApi getOpenAiApi(String name) {
        if (!cachedApi.containsKey(name)) {
            cachedApi.put(name, buildApi(name));
        }
        return cachedApi.get(name);
    }

    private OpenAiApi buildApi(String name) {
        AiModelProviderConfig aiModelProviderConfig = Configs.buildFromConfigurations(name);
        String providerCompany = aiModelProviderConfig.getProviderCompany();
        if (!providerCompany.equals("openai")) {
            throw new RuntimeException("not support company");
        }
        OpenAiApi openAiApi;
        String baseUrl = aiModelProviderConfig.getBaseUrl();
        String secretKey = aiModelProviderConfig.getSecretKey();
        if (baseUrl != null) {
            openAiApi = new OpenAiApi(baseUrl, secretKey);
        } else {
            openAiApi = new OpenAiApi(secretKey);
        }
        return openAiApi;

    }
}
