package cn.kanejin.webop;

import cn.kanejin.webop.operation.OperationContext;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

public interface Interceptor {

	void init(Map<String, String> params);
	
    void intercept(OperationContext context, InterceptorChain chain) throws ServletException, IOException;

	void destroy();
}
