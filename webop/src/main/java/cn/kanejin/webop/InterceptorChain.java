package cn.kanejin.webop;

import cn.kanejin.webop.operation.OperationContext;

import javax.servlet.ServletException;
import java.io.IOException;


public interface InterceptorChain {
	void intercept(OperationContext context) throws ServletException, IOException;
}
