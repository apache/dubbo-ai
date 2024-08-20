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
package org.apache.dubbo.ai.core.util;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesUtil {

    

    public static Map<String, String> getPropsByPath(String path) {
        if (path.endsWith("yml")) {
            return getPropertiesByProps(path);
        } else if (path.endsWith("properties")) {
            return getPropertiesByProps(path);
        } else {
            throw new UnsupportedOperationException("not support config format");
        }
    }

    public static Map<String, String> getPropertiesByYml(String ymlPath) {
        Properties properties = loadYamlIntoProperties(ymlPath);
        Map<String, String> propertiesMap = new HashMap<>();
        properties.forEach((key, value) -> propertiesMap.put(key.toString(), value.toString()));
        return propertiesMap;
    }

    public static Map<String, String> getPropertiesByProps(String propertiesPath) {
        String properties = propertiesPath.replaceAll("\\.properties", "");
        ResourceBundle resourceBundle = ResourceBundle.getBundle(properties);
        Map<String, String> propertiesMap = new HashMap<>();
        for (String key : resourceBundle.keySet()) {
            String value = resourceBundle.getString(key);
            propertiesMap.put(key, value);
        }
        return propertiesMap;
    }

    public static Properties loadYamlIntoProperties(String path) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        Resource resource = new ClassPathResource(path);
        factory.setResources(resource);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    public static String getProperty(String path, String key) {
        Properties properties = loadYamlIntoProperties(path);
        return properties.getProperty(key);
    }
}
