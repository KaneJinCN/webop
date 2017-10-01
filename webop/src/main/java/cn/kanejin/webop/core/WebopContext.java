package cn.kanejin.webop.core;

import cn.kanejin.webop.core.cache.EhCacheManagerImpl;

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

    public static void init() {
        WebopContext wc = get();

        wc.cacheManager = new EhCacheManagerImpl();

        wc.operationMapping = new OperationMapping();
        wc.operationStepMapping = new OperationStepMapping();
        wc.interceptorMapping = new InterceptorMapping();
        wc.converterMapping = new ConverterMapping();

        wc.configs = WebopConfigHelper.getDefaultConfigs();
    }

    private WebopCacheManager cacheManager;

    private OperationMapping operationMapping;

    private OperationStepMapping operationStepMapping;

    private InterceptorMapping interceptorMapping;

    private ConverterMapping converterMapping;

    public WebopCacheManager getCacheManager() {
        return cacheManager;
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

    private Properties configs;

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
