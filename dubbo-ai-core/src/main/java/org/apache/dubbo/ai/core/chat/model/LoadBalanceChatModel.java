package org.apache.dubbo.ai.core.chat.model;

import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuzhifei
 * @since 1.0
 */
public abstract class LoadBalanceChatModel implements ChatModel {


    private AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     * 目前就实现轮询就行，后续再改造
     */
    public ChatModel getChatModel() {
        List<ChatModel> chatModels = getChatModels();
        int size = chatModels.size();
        if (size == 1) {
            return chatModels.get(0);
        }
        if (currentIndex.get() > size - 1) {
            currentIndex.set(0);
        }
        return chatModels.get(currentIndex.getAndIncrement());
    }


    @Override
    public ChatResponse call(Prompt prompt) {
        return getChatModel().call(prompt);
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return getChatModel().getDefaultOptions();
    }


    public abstract List<ChatModel> getChatModels();
}
