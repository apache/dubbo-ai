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
import org.apache.dubbo.config.spring.ServiceBean;
import org.apache.dubbo.config.spring.context.annotation.DubboClassPathBeanDefinitionScanner;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class DubboAiConfigurationRegistrar implements ImportBeanDefinitionRegistrar, BeanFactoryAware, PriorityOrdered {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }


    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        // ensureDubboAutoConfigurationProcessed();
        String annotationName = EnableDubboAiConfiguration.class.getName();
        if (metadata.hasAnnotation(annotationName)) {
            Map<String, Object> attributes = metadata.getAnnotationAttributes(annotationName);
            String basePackage = attributes.get("scanBasePackage").toString();
            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
            scanner.addIncludeFilter(new AnnotationTypeFilter(DubboAiService.class));
            scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
                ClassMetadata classMetadata = metadataReader.getClassMetadata();
                return classMetadata.isInterface() && metadataReader.getAnnotationMetadata().hasAnnotation(DubboAiService.class.getName());
            });
            Set<BeanDefinition> definitions = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition definition : definitions) {
                String beanClassName = definition.getBeanClassName();
                try {
                    Class<?> beanClass = ClassUtils.forName(beanClassName, this.getClass().getClassLoader());
                    Object beanInterfaceImpl = ProxyGenerator.createProxy(beanClass);
                    ServiceConfig<Object> serviceConfig = new ServiceConfig<>();
                    // serviceConfig.setApplication(ApplicationModel.defaultModel().getCurrentConfig());
                    serviceConfig.setInterface(beanClass);
                    serviceConfig.setRef(beanInterfaceImpl);
                    serviceConfig.export();
//                    RootBeanDefinition serviceBeanDefinition = new RootBeanDefinition();
//                    serviceBeanDefinition.setBeanClass(ServiceBean.class);
//                    serviceBeanDefinition.setLazyInit(false);
//                    serviceBeanDefinition.getPropertyValues().add("interface", beanClass.getName());
//                    serviceBeanDefinition.getPropertyValues().add("ref", beanInterfaceImpl);
//                    serviceBeanDefinition.getPropertyValues().add("application", ApplicationModel.defaultModel().getCurrentConfig());
//                    registry.registerBeanDefinition("aaa",serviceBeanDefinition);
                    // definition.getPropertyValues().add("protocol", beanFactory.getBean("protocolConfig"));
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
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
