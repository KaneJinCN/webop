package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;

import javax.servlet.ServletException;
import java.io.IOException;

public class ForwardReturnAction extends EndReturnAction {
	
	public static ForwardReturnAction getInstance(String page) {
		return new ForwardReturnAction(page);
	}

	private final String page;
	
	private ForwardReturnAction(String page) {
		this.page = page;
	}

	@Override
	public void handleAction(OperationContext oc) throws ServletException, IOException {
		oc.getServletContext().getRequestDispatcher(page).forward(oc.getRequest(), oc.getResponse());
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}

	@Override
	public String toString() {
		return "forward " + page;
	}
}
