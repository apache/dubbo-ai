package org.apache.dubbo.ai.core;

import org.apache.dubbo.ai.core.proxy.ProxyGenerator;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

/**
 * @author liuzhifei
 * @since 1.0
 * 通过Dubbo Ai Service的注解注入之后，构造一个Service层走InJvm模式。
 */
public class RegisterDubboAiService {
    public static void registerServiceInJvm(Class c) {
        if (c.isAnnotationPresent(DubboAiService.class)) {

            // 解析注解里面的配置内容
            // 写入到dubbo的配置中
            // 创建ApplicationConfig
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName("manual-injvm-service-provider");

            // 创建ProtocolConfig
            ProtocolConfig protocolConfig = new ProtocolConfig();
            protocolConfig.setName(CommonConstants.TRIPLE);
            protocolConfig.setSerialization("fastjson2");

            // 创建ServiceConfig
            ServiceConfig<Object> serviceConfig = new ServiceConfig<>();
            // serviceConfig.setApplication(applicationConfig);
            serviceConfig.setProtocol(protocolConfig);
            serviceConfig.setInterface(c);
            serviceConfig.setRef(ProxyGenerator.createProxy(c));
            serviceConfig.setVersion("1.0.0");
            // 暴露服务
           //  serviceConfig.export();

            DubboBootstrap bootstrap = DubboBootstrap.getInstance();
            bootstrap
                    .application(applicationConfig)
                    .protocol(protocolConfig)
                    .service(serviceConfig)
                    .start();
        }
    }

    public static <T> T getDubboReference(Class<T> interfaceClass) {
        // 创建ApplicationConfig
        // ApplicationConfig applicationConfig = ApplicationModel.defaultModel().getApplicationConfigManager().getApplication().get();
        // 创建ReferenceConfig
        ReferenceConfig<?> referenceConfig = new ReferenceConfig<>();
        // referenceConfig.setApplication(applicationConfig);
        referenceConfig.setProtocol(CommonConstants.TRIPLE);
        referenceConfig.setInterface(interfaceClass);
        referenceConfig.setVersion("1.0.0");

        // 获取服务代理
        return (T) referenceConfig.get();
    }

}
