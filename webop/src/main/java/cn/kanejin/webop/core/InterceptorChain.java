package cn.kanejin.webop.core;

import javax.servlet.ServletException;
import java.io.IOException;

public interface InterceptorChain {
	void intercept(OperationContext context) throws ServletException, IOException;
}
