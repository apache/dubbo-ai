package org.apache.dubbo.ai.core.util;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author liuzhifei
 * @since 1.0
 */
public class PropertiesUtil {

    

    public static Map<String, String> getPropsByPath(String path) {
        if (path.endsWith("yml")) {
            return getPropertiesByProps(path);
        } else if (path.endsWith("properties")) {
            return getPropertiesByProps(path);
        } else {
            throw new RuntimeException("not support config format");
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
