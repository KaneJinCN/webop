package cn.kanejin.webop.core;

import cn.kanejin.webop.core.cache.CachedResponse;
import org.ehcache.Cache;

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
