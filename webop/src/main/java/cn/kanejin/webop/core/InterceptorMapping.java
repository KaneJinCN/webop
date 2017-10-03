package cn.kanejin.webop.core;

import cn.kanejin.webop.core.def.InterceptorDef;
import cn.kanejin.webop.core.exception.IllegalConfigException;

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
			it = instantiateInterceptor(interceptorDefMap.get(id));
			interceptors.put(id, it);
		}

		return it;
	}

	private Interceptor instantiateInterceptor(InterceptorDef itDef) {

		try {
			Interceptor it = (Interceptor) Class.forName(itDef.getClazz()).newInstance();
			ResourceInjector.getInstance().inject(it);

			it.init(itDef.getInitParams());

			return it;
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			throw new IllegalConfigException(
					"Can't instantiate interceptor[" + itDef.getId() + "]." +
							" Check for the interceptor configuration", e);
		}
	}

	public boolean exists(String itId) {
		return interceptorDefMap.containsKey(itId);
	}
}
