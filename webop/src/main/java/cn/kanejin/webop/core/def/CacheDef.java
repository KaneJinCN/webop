package cn.kanejin.webop.core.def;

import java.io.Serializable;

/**
 * @author Kane Jin
 */
public class CacheDef implements Serializable {
    // 缓存生存超过此时间过期
    private final long timeToLive;

    // 缓存空闲超过此时间过期
    private final long timeToIdle;

    // 生成缓存Key时要使用哪些字段
    private final String[] keyFields;

    public CacheDef(long timeToLive, long timeToIdle, String[] keyFields) {
        this.timeToLive = timeToLive;
        this.timeToIdle = timeToIdle;
        this.keyFields = keyFields;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public long getTimeToIdle() {
        return timeToIdle;
    }

    public String[] getKeyFields() {
        return keyFields;
    }
}
