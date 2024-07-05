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
package org.apache.dubbo.ai.spring.boot;

import org.apache.dubbo.ai.core.DubboAiService;
import org.apache.dubbo.ai.core.proxy.ProxyGenerator;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DubboAiConfigurationRegistrar implements ImportBeanDefinitionRegistrar, ApplicationListener<ContextRefreshedEvent>, PriorityOrdered {
    
    private static final Map<Class<?>, Object> interfaceMap = new HashMap<>();


    public static Map<Class<?>, Object> getInterfaceMap() {
        return interfaceMap;
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        // ensureDubboAutoConfigurationProcessed();
        String annotationName = EnableDubboAiConfiguration.class.getName();
        if (metadata.hasAnnotation(annotationName)) {
            Map<String, Object> attributes = metadata.getAnnotationAttributes(annotationName);
            String basePackage = attributes.get("scanBasePackage").toString();
            DubboAiPackageScanner dubboAiPackageScanner = new DubboAiPackageScanner();
            try {
                List<String> interfaces = dubboAiPackageScanner.findInterfaces(basePackage);
                for (String beanClassName : interfaces) {
                    try {
                        Class<?> beanClass = ClassUtils.forName(beanClassName, this.getClass().getClassLoader());
                        if (!beanClass.isAnnotationPresent(DubboAiService.class)) {
                            continue;
                        }

                        Object beanInterfaceImpl = ProxyGenerator.createProxy(beanClass);
                        interfaceMap.put(beanClass, beanInterfaceImpl);
//                        ServiceConfig<Object> serviceConfig = new ServiceConfig<>();
//                        serviceConfig.setInterface(beanClass);
//                        serviceConfig.setApplication(ApplicationModel.defaultModel().getCurrentConfig());
//                        serviceConfig.setRef(beanInterfaceImpl);
//                        serviceConfig.export();
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }


    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        interfaceMap.forEach((beanClass, beanInterfaceImpl) -> {
            ServiceConfig<Object> serviceConfig = new ServiceConfig<>();
            serviceConfig.setInterface(beanClass);
            serviceConfig.setApplication(ApplicationModel.defaultModel().getCurrentConfig());
            serviceConfig.setRef(beanInterfaceImpl);
            serviceConfig.export();
        });
    }

//    private void ensureDubboAutoConfigurationProcessed() {
//        // 这里我们尝试获取一个 Dubbo 相关的 bean，如果获取不到，说明 DubboAutoConfiguration 还没有被处理
//        try {
//            beanFactory.getBean("applicationConfig");
//        } catch (NoSuchBeanDefinitionException e) {
//            throw new IllegalStateException("DubboAutoConfiguration must be processed before DubboAiConfigurationRegistrar", e);
//        }
//    }
}
