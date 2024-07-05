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

import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.config.spring.context.event.DubboConfigInitEvent;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.springframework.context.ApplicationListener;

import java.util.Map;

public class DubboAiInitializer implements ApplicationListener<DubboConfigInitEvent> {
    @Override
    public void onApplicationEvent(DubboConfigInitEvent event) {
        Map<Class<?>, Object> interfaceMap = DubboAiConfigurationRegistrar.getInterfaceMap();
        interfaceMap.forEach((beanClass, beanInterfaceImpl) -> {
            ServiceConfig<Object> serviceConfig = new ServiceConfig<>();
            serviceConfig.setInterface(beanClass);
            serviceConfig.setApplication(ApplicationModel.defaultModel().getCurrentConfig());
            serviceConfig.setRef(beanInterfaceImpl);
            serviceConfig.export();
        });
    }
}
