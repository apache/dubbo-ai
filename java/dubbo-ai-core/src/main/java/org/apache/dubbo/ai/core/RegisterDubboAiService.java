/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.ai.core;

import org.apache.dubbo.ai.core.proxy.ProxyGenerator;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;

/**
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
        ReferenceConfig<?> referenceConfig = new ReferenceConfig<>();
        // referenceConfig.setApplication(applicationConfig);
        referenceConfig.setProtocol(CommonConstants.TRIPLE);
        referenceConfig.setInterface(interfaceClass);
        referenceConfig.setVersion("1.0.0");

        // 获取服务代理
        return (T) referenceConfig.get();
    }

}
