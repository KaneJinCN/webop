package cn.kanejin.webop.core;

import cn.kanejin.webop.cache.CachedResponse;
import cn.kanejin.webop.cache.CachedResponseExpiry;
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
public interface WebopCacheManager {

    /**
     * @return 保存URI中有变量的Operation的缓存
     */
    Cache<String, PatternOperation> getPatternOperationCache();

    /**
     * @return 保存Http响应内容的缓存
     */
    Cache<String, CachedResponse> getHttpResponseCache();

    void close();
}
