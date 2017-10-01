package cn.kanejin.webop.core;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class InterceptorChainImpl implements InterceptorChain {
	private List<Interceptor> interceptors;

	private Integer cursor = 0;

	private CompleteHandler completeHandler;

	public InterceptorChainImpl(CompleteHandler completeHandler) {
		this(null, completeHandler);
	}

	public InterceptorChainImpl(List<Interceptor> interceptors, CompleteHandler completeHandler) {
		this.completeHandler = completeHandler;
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

		if (completeHandler != null) {
			completeHandler.complete(context);
		}
	}
}
