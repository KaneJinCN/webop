package cn.kanejin.webop.cache;

import cn.kanejin.webop.converter.Converter;
import cn.kanejin.webop.operation.OperationStep;
import cn.kanejin.webop.operation.PatternOperation;
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
public class WebopCacheManager {

    private CacheManager cacheManager;

    private static final String CACHE_PATTERN_OPERATION = "_webop_patternOperationCache";
    private static final String CACHE_STEP = "_webop_stepCache";
    private static final String CACHE_CONVERTER = "_webop_converterCache";

    private WebopCacheManager() {}

    private static WebopCacheManager managerInstance;
    public static WebopCacheManager getInstance() {
        if (managerInstance == null) {
            managerInstance = new WebopCacheManager();
        }

        return managerInstance;
    }

    public void init() {
        if (cacheManager == null) {
            cacheManager = newCacheManagerBuilder()
                    .withCache(CACHE_PATTERN_OPERATION,
                            newCacheConfigurationBuilder(String.class, PatternOperation.class, heap(1000))
                                    .withExpiry(timeToIdleExpiration(of(30L, TimeUnit.MINUTES))))
                    .withCache(CACHE_STEP,
                            newCacheConfigurationBuilder(String.class, OperationStep.class, heap(1000))
                                    .withExpiry(timeToIdleExpiration(of(30L, TimeUnit.MINUTES))))
                    .withCache(CACHE_CONVERTER,
                            newCacheConfigurationBuilder(String.class, Converter.class, heap(1000))
                                    .withExpiry(timeToIdleExpiration(of(30L, TimeUnit.MINUTES))))
                    .build(true);
        }
    }

    /**
     * @return 保存URI中有变量的Operation的缓存
     */
    public Cache<String, PatternOperation> getPatternOperationCache() {
        return cacheManager.getCache(CACHE_PATTERN_OPERATION, String.class, PatternOperation.class);
    }

    /**
     * @return 保存Operation Step的缓存
     */
    public Cache<String, OperationStep> getStepCache() {
        return cacheManager.getCache(CACHE_STEP, String.class, OperationStep.class);
    }

    /**
     * @return 保存Converter的缓存
     */
    public Cache<String, Converter> getConverterCache() {
        return cacheManager.getCache(CACHE_CONVERTER, String.class, Converter.class);
    }

    public void close() {
        if (cacheManager != null)
            cacheManager.close();
    }
}
