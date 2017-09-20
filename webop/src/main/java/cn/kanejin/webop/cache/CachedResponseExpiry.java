package cn.kanejin.webop.cache;

import org.ehcache.ValueSupplier;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.expiry.Expiry;

import java.util.concurrent.TimeUnit;

/**
 * @author Kane Jin
 */
public class CachedResponseExpiry implements Expiry<String, CachedResponse> {
    @Override
    public Duration getExpiryForCreation(String key, CachedResponse response) {
        if (response.getTimeToLive() < 0) {
            return Duration.INFINITE;
        }

        return Duration.of(response.getTimeToLive(), TimeUnit.SECONDS);
    }

    @Override
    public Duration getExpiryForAccess(String key, ValueSupplier<? extends CachedResponse> supplier) {
        if (supplier.value().getTimeToIdle() < 0)
            return null;

        return Duration.of(supplier.value().getTimeToIdle(), TimeUnit.SECONDS);
    }

    @Override
    public Duration getExpiryForUpdate(String key, ValueSupplier<? extends CachedResponse> oldValue, CachedResponse newValue) {
        if (newValue.getTimeToLive() < 0) {
            return Duration.INFINITE;
        }

        return Duration.of(newValue.getTimeToLive(), TimeUnit.SECONDS);

    }
}
