package cn.kanejin.webop.operation.action;

import java.io.IOException;

import javax.servlet.ServletException;

import cn.kanejin.webop.operation.OperationContext;

public abstract class EndReturnAction extends ReturnAction {
	public abstract void doAction(OperationContext oc) throws ServletException, IOException;
}
