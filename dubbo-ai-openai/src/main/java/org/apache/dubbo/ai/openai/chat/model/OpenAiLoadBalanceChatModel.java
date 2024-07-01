package org.apache.dubbo.ai.openai.chat.model;

import org.apache.dubbo.ai.core.chat.model.ChatModel;
import org.apache.dubbo.ai.core.chat.model.LoadBalanceChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuzhifei
 * @since 1.0
 */
public class OpenAiLoadBalanceChatModel extends LoadBalanceChatModel {
    
    public List<ChatModel> openAiChatModelList;
    
    
    public OpenAiLoadBalanceChatModel(List<ChatModel> openAiChatModelList){
        this.openAiChatModelList = openAiChatModelList;
    }
    

    @Override
    public List<ChatModel> getChatModels() {
        return openAiChatModelList;
    }
}
