package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BackReturnAction extends EndReturnAction {
	private static BackReturnAction action;
	
	public static BackReturnAction getInstance() {
		if (action == null)
			action = new BackReturnAction();
		
		return action;
	}
	
	private BackReturnAction() {}

	@Override
	public void handleAction(OperationContext context) throws ServletException, IOException {

		HttpServletRequest req = context.getRequest();
		HttpServletResponse res = context.getResponse();

		String url = context.getBackUrl();
		if (url == null || url.isEmpty())
			url = req.getContextPath() + "/";
		
		res.sendRedirect(url);
	}
	
	@Override
	public String toString() {
		return "back";
	}

}
