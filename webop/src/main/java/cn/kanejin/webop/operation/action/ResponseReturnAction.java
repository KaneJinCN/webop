package cn.kanejin.webop.operation.action;

import java.io.IOException;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.kanejin.webop.operation.OperationContext;

public class ResponseReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(ResponseReturnAction.class);

	public static ResponseReturnAction getInstance(String statusString) {
		try {
			return new ResponseReturnAction(Integer.parseInt(statusString));
		} catch (Exception e) {
		}
		return new ResponseReturnAction(500);
	}

	private int status;
	
	private ResponseReturnAction(int status) {
		this.status = status;
	}

	@Override
	public void doAction(OperationContext oc) throws ServletException, IOException {
		oc.getResponse().setStatus(status);
	}


	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "response " + status;
	}
}
