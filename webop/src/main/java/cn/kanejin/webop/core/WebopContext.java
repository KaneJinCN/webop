package cn.kanejin.webop.core;

/**
 * @author Kane Jin
 */
public class WebopContext {
    private static WebopContext webopContext;

    public static WebopContext get() {
        if (webopContext == null)
            webopContext = new WebopContext();

        return webopContext;
    }

    private WebopContext() {}

    private WebopCacheManager cacheManager;

    private OperationMapping operationMapping;

    private OperationStepMapping operationStepMapping;

    private InterceptorMapping interceptorMapping;

    private ConverterMapping converterMapping;

    public WebopCacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(WebopCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public OperationMapping getOperationMapping() {
        return operationMapping;
    }

    public void setOperationMapping(OperationMapping operationMapping) {
        this.operationMapping = operationMapping;
    }

    public OperationStepMapping getOperationStepMapping() {
        return operationStepMapping;
    }

    public void setOperationStepMapping(OperationStepMapping operationStepMapping) {
        this.operationStepMapping = operationStepMapping;
    }

    public InterceptorMapping getInterceptorMapping() {
        return interceptorMapping;
    }

    public void setInterceptorMapping(InterceptorMapping interceptorMapping) {
        this.interceptorMapping = interceptorMapping;
    }

    public ConverterMapping getConverterMapping() {
        return converterMapping;
    }

    public void setConverterMapping(ConverterMapping converterMapping) {
        this.converterMapping = converterMapping;
    }
}
