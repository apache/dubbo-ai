package org.apache.dubbo.ai.openai.chat.model;

import org.apache.dubbo.ai.core.chat.model.ChatModel;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author liuzhifei
 * @since 1.0
 */
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
