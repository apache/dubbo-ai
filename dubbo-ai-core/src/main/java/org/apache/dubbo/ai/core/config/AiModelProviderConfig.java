package org.apache.dubbo.ai.core.config;

/**
 * @author liuzhifei
 * @since 1.0
 */
public class AiModelProviderConfig {
    
    
    private String providerCompany;
    private String secretKey;
    private String baseUrl;
    
    private String name;

    public String getProviderCompany() {
        return providerCompany;
    }

    public void setProviderCompany(String providerCompany) {
        this.providerCompany = providerCompany;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
