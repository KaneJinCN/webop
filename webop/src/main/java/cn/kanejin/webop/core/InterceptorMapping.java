package cn.kanejin.webop.core;

import cn.kanejin.webop.core.def.InterceptorDef;

import java.util.HashMap;
import java.util.Map;

public class InterceptorMapping {
	private Map<String, InterceptorDef> interceptorDefMap;

	private Map<String, Interceptor> interceptors;
	
	public InterceptorMapping() {
		interceptorDefMap = new HashMap<>();
		interceptors = new HashMap<>();
	}
	
	public void put(String id, InterceptorDef def) {
		interceptorDefMap.put(id, def);
	}
	
	public Interceptor get(String id) {
		Interceptor it = interceptors.get(id);

		if (it == null) {
			it = instantInterceptor(interceptorDefMap.get(id));
			interceptors.put(id, it);
		}

		return it;
	}

	private Interceptor instantInterceptor(InterceptorDef itDef) {

		try {
			Interceptor it = (Interceptor) Class.forName(itDef.getClazz()).newInstance();
			it.init(itDef.getInitParams());

			ResourceInjector.getInstance().inject(it);

			return it;
		} catch (Throwable t) {
			throw new OperationException("Instant interceptor [" + itDef.getId() + "] error", t);
		}
	}

	public boolean exists(String itId) {
		return interceptorDefMap.containsKey(itId);
	}
}
