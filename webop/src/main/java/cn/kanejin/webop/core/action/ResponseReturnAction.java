package cn.kanejin.webop.core.action;

import cn.kanejin.commons.util.NumberUtils;
import cn.kanejin.webop.core.OperationContext;

import javax.servlet.ServletException;
import java.io.IOException;

public class ResponseReturnAction extends EndReturnAction {
	public static ResponseReturnAction build(String statusString) {
		return new ResponseReturnAction(NumberUtils.toInt(statusString, 500));
	}

	private final int status;
	
	private ResponseReturnAction(int status) {
		this.status = status;
	}

	@Override
	public void doActionInternal(OperationContext oc) throws ServletException, IOException {
		oc.getResponse().setStatus(status);
	}


	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "response " + status;
	}
}
