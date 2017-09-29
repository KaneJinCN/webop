package cn.kanejin.webop.sample.interceptor;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;

import cn.kanejin.webop.core.Interceptor;
import cn.kanejin.webop.core.InterceptorChain;
import cn.kanejin.webop.core.OperationContext;

public class HelloInterceptor implements Interceptor {

	@Resource(name = "author")
	private String author;

	@Override
	public void init(Map<String, String> params) {

	}

	@Override
	public void intercept(OperationContext context, InterceptorChain chain)
			throws ServletException, IOException {

		System.out.println("----------------------Hello " + author + "---------------------");

		chain.intercept(context);

		System.out.println("----------------------Hello " + author + " After Operation---------------------");
	}

	@Override
	public void destroy() {
	}
}
