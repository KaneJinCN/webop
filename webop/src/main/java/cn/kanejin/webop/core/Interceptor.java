package cn.kanejin.webop.core;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

public interface Interceptor {

	void init(Map<String, String> params);
	
    void intercept(OperationContext context, InterceptorChain chain) throws ServletException, IOException;

	void destroy();
}
