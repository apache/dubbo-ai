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

import org.apache.dubbo.common.threadpool.manager.FrameworkExecutorRepository;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;

import java.util.concurrent.ExecutorService;

public class DubboAiServiceExporter {

    private final ApplicationModel applicationModel;
    private final Class<Object> interfaceClass;

    private final Object interfaceImpl;

    private volatile ServiceConfig<Object> serviceConfig;

    public DubboAiServiceExporter(ApplicationModel applicationModel, Class<Object> interfaceClass, Object interfaceImpl) {
        this.applicationModel = applicationModel;
        this.interfaceImpl = interfaceImpl;
        this.interfaceClass = interfaceClass;
    }

    public DubboAiServiceExporter export() {
        if (serviceConfig == null || !isExported()) {
            ExecutorService internalServiceExecutor = applicationModel
                    .getFrameworkModel()
                    .getBeanFactory()
                    .getBean(FrameworkExecutorRepository.class)
                    .getInternalServiceExecutor();
            this.serviceConfig = new ServiceConfig<>();
            serviceConfig.setInterface(interfaceClass);
            serviceConfig.setRef(interfaceImpl);
            serviceConfig.setTimeout(60000);
            serviceConfig.setExecutor(internalServiceExecutor);
            serviceConfig.setApplication(getApplicationConfig());
            serviceConfig.setProtocol(new ProtocolConfig("tri"));
            serviceConfig.setSerialization("fastjson2");
            serviceConfig.export();
        }
        return this;
    }

    private ApplicationConfig getApplicationConfig() {
        return applicationModel.getApplicationConfigManager().getApplication().get();
    }

    public boolean isExported() {
        return serviceConfig != null && serviceConfig.isExported() && !serviceConfig.isUnexported();
    }

    public void unexport() {
        if (isExported()) {
            serviceConfig.unexport();
        }
    }


}
