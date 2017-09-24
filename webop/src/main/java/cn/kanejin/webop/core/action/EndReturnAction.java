package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.def.OperationDef;
import cn.kanejin.webop.core.def.OperationStepDef;

import javax.servlet.ServletException;
import java.io.IOException;

public abstract class EndReturnAction implements ReturnAction {
	@Override
	public OperationStepDef doAction(
			OperationDef operationDef,
			OperationStepDef stepDef,
			OperationContext context) throws ServletException, IOException {

		doActionInternal(context);

		return null;
	}

	protected abstract void doActionInternal(OperationContext context) throws ServletException, IOException;
}