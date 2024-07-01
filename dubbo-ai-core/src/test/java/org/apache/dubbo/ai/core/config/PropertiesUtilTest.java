package org.apache.dubbo.ai.core.config;

import org.apache.dubbo.ai.core.util.PropertiesUtil;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author liuzhifei
 * @since 1.0
 */
class PropertiesUtilTest {

    @Test
    void testGetPropertiesByYml() {
        Map<String, String> propertiesByYml = PropertiesUtil.getPropertiesByYml("dubbo-ai.yml");
        assert propertiesByYml.size() == 1;
    }

    @Test
    void testGetProperties() {
        Map<String, String> propertiesByYml = PropertiesUtil.getPropertiesByProps("dubbo-ai.properties");
        assert propertiesByYml.size() == 1;
    }

}