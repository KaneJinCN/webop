package cn.kanejin.webop.operation.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.kanejin.webop.operation.OperationContext;

public class RedirectReturnAction extends EndReturnAction {
	public static RedirectReturnAction getInstance(String page) {
		return new RedirectReturnAction(page);
	}

	private String page;
	
	private RedirectReturnAction(String page) {
		this.page = page;
	}

	@Override
	public void doAction(OperationContext oc) throws ServletException, IOException {
		oc.getResponse().sendRedirect(page);
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(String page) {
		this.page = page;
	}
	
	@Override
	public String toString() {
		return "redirect " + page;
	}
}
