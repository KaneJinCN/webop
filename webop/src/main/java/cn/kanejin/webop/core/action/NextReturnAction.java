package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationException;
import cn.kanejin.webop.core.def.OperationDef;
import cn.kanejin.webop.core.def.OperationStepDef;

import java.util.List;

public class NextReturnAction extends ProcessReturnAction {
	private static NextReturnAction action;
	
	public static NextReturnAction getInstance() {
		if (action == null)
			action = new NextReturnAction();
		
		return action;
	}
	
	private NextReturnAction() {}

	@Override
	protected OperationStepDef nextStep(OperationDef operationDef, OperationStepDef stepDef) {
		List<OperationStepDef> steps = operationDef.getOpSteps();

		int nextStepIndex = steps.indexOf(stepDef) + 1;

		if (nextStepIndex > 0 && nextStepIndex < steps.size())
			return steps.get(nextStepIndex);

		throw new OperationException(
				"Operation[" + operationDef.getUri() + "] Next Step of [" + stepDef.getId() + "] not found");
	}

	@Override
	public String toString() {
		return "next";
	}
}
