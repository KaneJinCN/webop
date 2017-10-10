package cn.kanejin.webop.core;

import cn.kanejin.webop.core.cache.EhCacheManagerImpl;

import javax.servlet.ServletContext;

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

    public static void init(ServletContext servletContext) {
        WebopContext wc = get();

        wc.servletContext = servletContext;

        wc.cacheManager = new EhCacheManagerImpl();

        wc.operationMapping = new OperationMapping();
        wc.operationStepMapping = new OperationStepMapping();
        wc.interceptorMapping = new InterceptorMapping();
        wc.converterMapping = new ConverterMapping();

        wc.webopConfig = new WebopConfig();
    }

    private ServletContext servletContext;

    private WebopConfig webopConfig;

    private WebopCacheManager cacheManager;

    private OperationMapping operationMapping;

    private OperationStepMapping operationStepMapping;

    private InterceptorMapping interceptorMapping;

    private ConverterMapping converterMapping;

    public ServletContext getServletContext() {
        return servletContext;
    }

    public WebopConfig getWebopConfig() {
        return webopConfig;
    }

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
}
