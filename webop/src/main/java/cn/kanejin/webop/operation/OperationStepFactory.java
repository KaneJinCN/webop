package cn.kanejin.webop.operation;

import cn.kanejin.webop.cache.WebopCacheManager;
import org.ehcache.Cache;

import java.util.Map;

/**
 * @version $Id: OperationStepFactory.java 169 2017-09-17 12:26:25Z Kane $
 * @author Kane Jin
 */
public class OperationStepFactory {
	private static OperationStepFactory factory;
	
	private OperationStepFactory() {}

	public static OperationStepFactory getInstance() {
		if (factory == null)
			factory = new OperationStepFactory();
		return factory;
	}
	
	public OperationStep create(OperationStepSpec stepDef) {
		try {
			Cache<String, OperationStep> cache =
					WebopCacheManager.getInstance().getStepCache();

			String cacheKey = generateCacheKey(stepDef);
			
			OperationStep step = cache.get(cacheKey);

			// 如果Cache中存在STEP
			if (step != null) {
				return step;
			}

			// Cache中不存在时，创建新的STEP对象，并把对象放在Cache中
			step = (OperationStep) Class.forName(stepDef.getClazz()).newInstance();
			
			step.init(stepDef.getInitParams());
			
			cache.put(cacheKey, step);

			return step;
		} catch (Exception e) {
			throw new OperationException("Instant step class[" + 
										stepDef.getClazz() + "] error", e);
		}
	}
	
	private String generateCacheKey(OperationStepSpec stepDef) {
		String className = stepDef.getClazz();
		
		Map<String, String> params = stepDef.getInitParams();
		
		if (params == null)
			return className;
		
		StringBuilder sb = new StringBuilder(className);
		for (String key : params.keySet()) {
			sb.append('_');
			sb.append(key);
			sb.append('-');
			sb.append(params.get(key));
		}
		
		return sb.toString();
	}
}
