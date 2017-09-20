package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;

import javax.servlet.ServletException;
import java.io.IOException;

public class RedirectReturnAction extends EndReturnAction {
	public static RedirectReturnAction getInstance(String page) {
		return new RedirectReturnAction(page);
	}

	private final String page;
	
	private RedirectReturnAction(String page) {
		this.page = page;
	}

	@Override
	public void handleAction(OperationContext oc) throws ServletException, IOException {
		oc.getResponse().sendRedirect(page);
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}

	@Override
	public String toString() {
		return "redirect " + page;
	}
}
