package cn.kanejin.webop.operation.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.kanejin.webop.operation.OperationContext;

public class ErrorReturnAction extends EndReturnAction {

	private static ErrorReturnAction action;
	
	public static ErrorReturnAction getInstance() {
		if (action == null)
			action = new ErrorReturnAction();
		
		return action;
	}
	
	private ErrorReturnAction() {}
	
	@Override
	public void doAction(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		throw new ServletException(req.getRequestURI() + " error");
	}
	
	@Override
	public String toString() {
		return "error";
	}
}
