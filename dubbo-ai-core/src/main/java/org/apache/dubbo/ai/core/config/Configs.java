package org.apache.dubbo.ai.core.config;

import org.apache.dubbo.common.config.CompositeConfiguration;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.List;

/**
 * @author liuzhifei
 * @since 1.0
 */
public class Configs {

    private static final String PROVIDER_FORMAT = "dubbo.ai.%s.provider";

    private static final String BASEURL_FORMAT = "dubbo.ai.%s.baseUrl";

    private static final String SK_FORMAT = "dubbo.ai.%s.sk";

    private static final String LOAD_BALANCE_PROVIDERS = "dubbo.ai.%s.loadbalance.providers";

    public static AiModelProviderConfig buildFromConfigurations(String name) {
        CompositeConfiguration configuration = ApplicationModel.defaultModel().modelEnvironment().getConfiguration();
        AiModelProviderConfig aiModelProviderConfig = new AiModelProviderConfig();
        aiModelProviderConfig.setName(name);
        aiModelProviderConfig.setProviderCompany(configuration.getString(String.format(PROVIDER_FORMAT, name)));
        aiModelProviderConfig.setBaseUrl(configuration.getString(String.format(BASEURL_FORMAT, name)));
        aiModelProviderConfig.setSecretKey(configuration.getString(String.format(SK_FORMAT, name)));
        return aiModelProviderConfig;
    }
}
