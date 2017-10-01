package cn.kanejin.webop.core.cache;

import cn.kanejin.webop.core.def.CacheExpiryDef;
import org.ehcache.ValueSupplier;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expiry;

/**
 * @author Kane Jin
 */
public class CachedResponseExpiry implements Expiry<String, CachedResponse> {
    @Override
    public Duration getExpiryForCreation(String key, CachedResponse response) {
        CacheExpiryDef expiryDef = response.getExpiryDef();

        if (expiryDef.getType().equals("eternal")) {
            return Duration.INFINITE;
        } else if (expiryDef.getType().equals("ttl")) {
            return Duration.of(expiryDef.getTime(), expiryDef.getUnit());
        } else if (expiryDef.getType().equals("tti")) {
            return Duration.of(expiryDef.getTime(), expiryDef.getUnit());
        } else {
            throw new RuntimeException("CacheDef of CachedResponse is invalid");
        }
    }

    @Override
    public Duration getExpiryForAccess(String key, ValueSupplier<? extends CachedResponse> supplier) {
        CacheExpiryDef expiryDef = supplier.value().getExpiryDef();

        if (expiryDef.getType().equals("tti")) {
            return Duration.of(expiryDef.getTime(), expiryDef.getUnit());
        } else {
            return null;
        }
    }

    @Override
    public Duration getExpiryForUpdate(String key, ValueSupplier<? extends CachedResponse> oldValue, CachedResponse newValue) {
        return getExpiryForCreation(key, newValue);
    }
}
