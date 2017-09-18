package cn.kanejin.webop.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import cn.kanejin.webop.Interceptor;
import cn.kanejin.webop.InterceptorChain;
import cn.kanejin.webop.operation.Operation;
import cn.kanejin.webop.operation.OperationContext;

public class InterceptorChainImpl implements InterceptorChain {
	private List<Interceptor> interceptors;
	
	private Integer cursor = 0;
	
	private Operation operation;
	
	public InterceptorChainImpl(Operation operation) {
		this.operation = operation;
	}
	
	public InterceptorChainImpl(Operation operation, List<Interceptor> interceptors) {
		this.operation = operation;
		this.interceptors = interceptors;
	}

	public void addInterceptor(Interceptor interceptor) {
		if (interceptors == null)
			interceptors = new ArrayList<Interceptor>();
		
		interceptors.add(interceptor);
	}
	
	@Override
	public void intercept(OperationContext context) throws ServletException, IOException {
		if (interceptors != null) {
			if (cursor < interceptors.size()) {
				interceptors.get(cursor++).intercept(context, this);
				
				return ;
			}
		}
		
		operation.execute(context);
	}
}
