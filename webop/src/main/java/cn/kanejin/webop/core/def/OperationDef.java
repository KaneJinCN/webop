package cn.kanejin.webop.core.def;

import java.io.Serializable;
import java.util.List;

/**
 * @author Kane Jin
 */
public class OperationDef implements Serializable {
    private final String uri;
    private final String name;
    private final List<String> interceptorRefs;
    private final List<OperationStepDef> opSteps;

    private final CacheDef cacheDef;

    public OperationDef(
            String uri, String name, CacheDef cacheDef,
            List<String> interceptorRefs, List<OperationStepDef> opSteps) {
        this.uri = uri;
        this.name = name;
        this.opSteps = opSteps;
        this.interceptorRefs = interceptorRefs;
        this.cacheDef = cacheDef;
    }

    public boolean hasInterceptors() {
        return interceptorRefs != null && !interceptorRefs.isEmpty();
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public List<String> getInterceptorRefs() {
        return interceptorRefs;
    }

    public List<OperationStepDef> getOpSteps() {
        return opSteps;
    }

    public CacheDef getCacheDef() {
        return cacheDef;
    }
}
