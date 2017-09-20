package cn.kanejin.webop.core;

import java.util.HashMap;
import java.util.Map;

public class InterceptorMapping {
	private static InterceptorMapping im;
	
	private Map<String, Interceptor> interceptors;
	
	private InterceptorMapping() {
		interceptors = new HashMap<String, Interceptor>();
	}
	
	public static InterceptorMapping getInstance() {
		if (im == null)
			im = new InterceptorMapping();
		return im;
	}
	
	public Interceptor get(String id) {
		return interceptors.get(id);
	}
	
	public void put(String id, Interceptor interceptor) {
		interceptors.put(id, interceptor);
	}
}
