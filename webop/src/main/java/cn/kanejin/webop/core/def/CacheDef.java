package cn.kanejin.webop.core.def;

import java.io.Serializable;

/**
 * @author Kane Jin
 */
public class CacheDef implements Serializable {
    private final CacheExpiryDef expiryDef;

    // 生成缓存Key时要使用哪些字段
    private final String[] keyFields;

    public CacheDef(CacheExpiryDef expiryDef, String[] keyFields) {
        this.expiryDef = expiryDef;
        this.keyFields = keyFields;
    }

    public CacheExpiryDef getExpiryDef() {
        return expiryDef;
    }

    public String[] getKeyFields() {
        return keyFields;
    }
}
