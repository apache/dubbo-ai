package org.apache.dubbo.ai.core.chat.model;

import java.util.List;


public class DefaultLoadBalanceChatModel extends LoadBalanceChatModel {

    public List<ChatModel> chatModelList;

    
    public DefaultLoadBalanceChatModel(List<ChatModel> chatModelList) {
        this.chatModelList = chatModelList;
    }


    @Override
    public List<ChatModel> getChatModels() {
        return chatModelList;
    }
}
