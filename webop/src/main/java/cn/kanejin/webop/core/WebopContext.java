package cn.kanejin.webop.core;

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
}
