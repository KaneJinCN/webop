package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;

public class ResponseReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(ResponseReturnAction.class);

	public static ResponseReturnAction getInstance(String statusString) {
		try {
			return new ResponseReturnAction(Integer.parseInt(statusString));
		} catch (Exception e) {
		}
		return new ResponseReturnAction(500);
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
