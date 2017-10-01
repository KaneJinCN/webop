package cn.kanejin.webop.core.cache;

import cn.kanejin.webop.core.PatternOperation;
import cn.kanejin.webop.core.WebopCacheManager;
import org.ehcache.Cache;
import org.ehcache.CacheManager;

import java.util.concurrent.TimeUnit;

import static org.ehcache.config.builders.CacheConfigurationBuilder.newCacheConfigurationBuilder;
import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManagerBuilder;
import static org.ehcache.config.builders.ResourcePoolsBuilder.heap;
import static org.ehcache.expiry.Duration.of;
import static org.ehcache.expiry.Expirations.timeToIdleExpiration;

/**
 * @author Kane Jin
 */
public class EhCacheManagerImpl implements WebopCacheManager {

    private final CacheManager cacheManager;

    private static final String CACHE_HTTP_RESPONSE = "_webop_httpResponseCache";

    private static final String CACHE_PATTERN_OPERATION = "_webop_patternOperationCache";

    public EhCacheManagerImpl() {
        cacheManager = newCacheManagerBuilder()
                .withCache(CACHE_PATTERN_OPERATION,
                        newCacheConfigurationBuilder(String.class, PatternOperation.class, heap(1000))
                                .withExpiry(timeToIdleExpiration(of(10L, TimeUnit.MINUTES))))
                .withCache(CACHE_HTTP_RESPONSE,
                        newCacheConfigurationBuilder(String.class, CachedResponse.class, heap(1000))
                                .withExpiry(new CachedResponseExpiry()))
                .build(true);
    }

    @Override
    public Cache<String, PatternOperation> getPatternOperationCache() {
        return cacheManager.getCache(CACHE_PATTERN_OPERATION, String.class, PatternOperation.class);
    }

    @Override
    public Cache<String, CachedResponse> getHttpResponseCache() {
        return cacheManager.getCache(CACHE_HTTP_RESPONSE, String.class, CachedResponse.class);
    }

    @Override
    public void close() {
        if (cacheManager != null)
            cacheManager.close();
    }
}
