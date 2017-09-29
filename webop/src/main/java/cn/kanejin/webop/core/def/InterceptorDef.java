package cn.kanejin.webop.core.def;

import java.util.Map;

/**
 * @author Kane Jin
 */
public class InterceptorDef {
    private final String id;

    private final String clazz;

    private final Map<String, String> initParams;

    public InterceptorDef(String id, String clazz, Map<String, String> initParams) {
        this.id = id;
        this.clazz = clazz;
        this.initParams = initParams;
    }


    public String getId() {
        return id;
    }

    public String getClazz() {
        return clazz;
    }

    public Map<String, String> getInitParams() {
        return initParams;
    }
}
