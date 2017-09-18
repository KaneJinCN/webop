package cn.kanejin.webop.operation.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.kanejin.webop.operation.OperationContext;

public class BackReturnAction extends EndReturnAction {
	private static BackReturnAction action;
	
	public static BackReturnAction getInstance() {
		if (action == null)
			action = new BackReturnAction();
		
		return action;
	}
	
	private BackReturnAction() {}

	@Override
	public void doAction(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		String url = oc.getBackUrl();
		if (url == null || url.isEmpty())
			url = req.getContextPath() + "/";
		
		res.sendRedirect(url);
	}
	
	@Override
	public String toString() {
		return "back";
	}
}
