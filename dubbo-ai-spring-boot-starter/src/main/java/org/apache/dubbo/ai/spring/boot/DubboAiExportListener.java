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
import org.apache.dubbo.ai.spring.boot.helper.ContextHelper;
import org.apache.dubbo.common.deploy.ApplicationDeployListener;
import org.apache.dubbo.common.lang.Prioritized;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DubboAiExportListener implements ApplicationDeployListener, Prioritized {

    private final Map<Class<?>, DubboAiServiceExporter> dubboAiServiceExporterMap = new HashMap<>();


    @Override
    public void onStarting(ApplicationModel scopeModel) {
        doScanPackages();
    }

    @Override
    public void onStopped(ApplicationModel scopeModel) {
        for (DubboAiServiceExporter dubboAiServiceExporter : dubboAiServiceExporterMap.values()) {
            dubboAiServiceExporter.unexport();
        }
    }


    @Override
    public void onInitialize(ApplicationModel scopeModel) {
        
    }


    private void doScanPackages() {
        List<String> packages = AutoConfigurationPackages.get(ContextHelper.getBeanFactory());
        for (String aPackage : packages) {
            DubboAiPackageScanner dubboAiPackageScanner = new DubboAiPackageScanner();
            try {
                List<String> interfaces = dubboAiPackageScanner.findInterfaces(aPackage);
                for (String beanClassName : interfaces) {
                    try {
                        Class<?> beanClass = ClassUtils.forName(beanClassName, this.getClass().getClassLoader());
                        if (!beanClass.isAnnotationPresent(DubboAiService.class)) {
                            continue;
                        }
                        Object beanInterfaceImpl = ProxyGenerator.createProxy(beanClass);
                        doExportService(beanClass, beanInterfaceImpl);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doExportService(Class<?> beanClass, Object beanInterfaceImpl) {
        DubboAiServiceExporter dubboAiServiceExporter = new DubboAiServiceExporter(ApplicationModel.defaultModel(), (Class<Object>) beanClass, beanInterfaceImpl);
        dubboAiServiceExporterMap.put(beanClass, dubboAiServiceExporter);
        dubboAiServiceExporter.export();
    }


    @Override
    public void onStarted(ApplicationModel scopeModel) {

    }

    @Override
    public void onStopping(ApplicationModel scopeModel) {

    }


    @Override
    public void onFailure(ApplicationModel scopeModel, Throwable cause) {

    }

    @Override
    public int getPriority() {
        return MAX_PRIORITY;
    }
}
