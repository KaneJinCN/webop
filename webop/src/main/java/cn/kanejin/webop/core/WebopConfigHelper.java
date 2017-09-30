package cn.kanejin.webop.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Kane Jin
 */
class WebopConfigHelper {
    private static final Map<String, String> DEFAULT_CONFIG;
    static {
        DEFAULT_CONFIG = new HashMap<>();
        DEFAULT_CONFIG.put("webop.resource.provider", "cn.kanejin.webop.spring.BeanResourceProvider");
    }

    public static Properties getDefaultConfigs() {
        Properties p = new Properties();

        for (String key : DEFAULT_CONFIG.keySet()) {
            p.setProperty(key, DEFAULT_CONFIG.get(key));
        }

        return p;
    }

    public static boolean isSupportedConfig(String configName) {
        return DEFAULT_CONFIG.containsKey(configName);
    }
}
