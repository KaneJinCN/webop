package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;

import javax.servlet.ServletException;
import java.io.IOException;

public class ErrorReturnAction extends EndReturnAction {

	private static ErrorReturnAction action;
	
	public static ErrorReturnAction build() {
		if (action == null)
			action = new ErrorReturnAction();
		
		return action;
	}
	
	private ErrorReturnAction() {}

	@Override
	public void doActionInternal(OperationContext context) throws ServletException, IOException {
		throw new ServletException(context.getRequest().getRequestURI() + " error");
	}
	
	@Override
	public String toString() {
		return "error";
	}
}
