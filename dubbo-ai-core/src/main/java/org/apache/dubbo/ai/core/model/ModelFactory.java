package org.apache.dubbo.ai.core.model;

import com.alibaba.fastjson2.JSONObject;
import org.apache.dubbo.ai.core.chat.model.ChatModel;
import org.apache.dubbo.ai.core.chat.model.DefaultLoadBalanceChatModel;
import org.apache.dubbo.ai.core.chat.model.LoadBalanceChatModel;
import org.apache.dubbo.ai.core.config.AiModelProviderConfig;
import org.apache.dubbo.ai.core.config.Configs;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liuzhifei
 * @since 1.0
 */
public class ModelFactory {

    public static LoadBalanceChatModel getLoadBalanceChatModel(List<String> configModelNames, JSONObject chatOptions) {
        List<ChatModel> res = new ArrayList<>();
        for (String configModelName : configModelNames) {
            AiModelProviderConfig aiModelProviderConfig = Configs.buildFromConfigurations(configModelName);
            String providerCompany = aiModelProviderConfig.getProviderCompany();
            AiModels models = ApplicationModel.defaultModel().getExtension(AiModels.class, providerCompany);
            ChatModel chatModel = models.getChatModel(configModelName, chatOptions);
            res.add(chatModel);
        }
        return new DefaultLoadBalanceChatModel(res);
    }
}
