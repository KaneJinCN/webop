package cn.kanejin.webop.operation.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.kanejin.webop.operation.OperationContext;

public class ForwardReturnAction extends EndReturnAction {
	
	public static ForwardReturnAction getInstance(String page) {
		return new ForwardReturnAction(page);
	}

	private String page;
	
	private ForwardReturnAction(String page) {
		this.page = page;
	}

	@Override
	public void doAction(OperationContext oc) throws ServletException, IOException {
		oc.getServletContext().getRequestDispatcher(page).forward(oc.getRequest(), oc.getResponse());
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
		return "forward " + page;
	}
}
