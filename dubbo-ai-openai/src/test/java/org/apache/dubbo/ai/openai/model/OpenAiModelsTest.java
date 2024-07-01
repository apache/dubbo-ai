package org.apache.dubbo.ai.openai.model;


import org.apache.dubbo.ai.core.RegisterDubboAiService;
import org.apache.dubbo.ai.openai.MyAiService;
import org.junit.jupiter.api.Test;

/**
 * @author liuzhifei
 * @since 1.0
 */
class OpenAiModelsTest {

    @Test
    void testOpenAiModelConfig() {
        System.setProperty("dubbo.application.serialize-check-status", "DISABLE");
        RegisterDubboAiService.registerServiceInJvm(MyAiService.class);
        MyAiService myAiService = RegisterDubboAiService.getDubboReference(MyAiService.class);
        String hi = myAiService.chat("hi");
        System.out.println(hi);
    }

}