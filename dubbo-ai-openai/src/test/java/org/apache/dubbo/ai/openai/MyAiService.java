package org.apache.dubbo.ai.openai;

import org.apache.dubbo.ai.core.DubboAiService;
import org.apache.dubbo.ai.core.Prompt;
import org.apache.dubbo.common.stream.StreamObserver;

/**
 * @author liuzhifei
 * @since 1.0
 */
@DubboAiService(configPath = "dubbo-ai.properties", modelProvider = {"m1", "m2"},model = "gpt-4-all")
public interface MyAiService {


    @Prompt(
            """
            请用中文回答我的这个问题:  {userMessage}
            """)
    String chat(String userMessage);


    @Prompt("""
            请用中文回答我的这个问题:  {userMessage}
            """)
    void chat(String userMessage, StreamObserver<String> response);
}