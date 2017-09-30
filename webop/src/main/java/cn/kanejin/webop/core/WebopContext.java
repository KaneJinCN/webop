package cn.kanejin.webop.core;

import java.util.Properties;

import static cn.kanejin.commons.util.StringUtils.isEmpty;

/**
 * @author Kane Jin
 */
public class WebopContext {
    private static WebopContext context;

    public static WebopContext get() {
        if (context == null)
            context = new WebopContext();

        return context;
    }

    private WebopContext() {

        operationMapping = new OperationMapping();

        operationStepMapping = new OperationStepMapping();
        interceptorMapping = new InterceptorMapping();
        converterMapping = new ConverterMapping();

        configs = WebopConfigHelper.getDefaultConfigs();
    }

    private WebopCacheManager cacheManager;

    private final OperationMapping operationMapping;

    private final OperationStepMapping operationStepMapping;

    private final InterceptorMapping interceptorMapping;

    private final ConverterMapping converterMapping;

    public WebopCacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(WebopCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public OperationMapping getOperationMapping() {
        return operationMapping;
    }

    public OperationStepMapping getOperationStepMapping() {
        return operationStepMapping;
    }

    public InterceptorMapping getInterceptorMapping() {
        return interceptorMapping;
    }

    public ConverterMapping getConverterMapping() {
        return converterMapping;
    }

    private final Properties configs;

    public void setConfig(String configName, String configValue) {
        if (WebopConfigHelper.isSupportedConfig(configName))
            configs.setProperty(configName, configValue);
    }

    public String getConfig(String configName) {

        String config = configs.getProperty(configName);

        if (isEmpty(config)) {
            config = System.getProperty(configName);
        }

        return config;
    }
}
