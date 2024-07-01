package org.apache.dubbo.ai.core.model;


import com.alibaba.fastjson2.JSONObject;
import org.apache.dubbo.ai.core.chat.model.ChatModel;
import org.apache.dubbo.common.extension.SPI;
import org.springframework.ai.chat.prompt.ChatOptions;

import java.util.List;

/**
 * @author liuzhifei
 * @since 1.0
 */
@SPI
public interface AiModels {


    /**
     * get AiModels by config name
     * @param configModelNames name
     * @return the config name provider
     */
    ChatModel getChatModel(List<String> configModelNames, JSONObject chatOptions);
}
