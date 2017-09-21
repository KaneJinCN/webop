package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.def.OperationDef;
import cn.kanejin.webop.core.def.OperationStepDef;

import javax.servlet.ServletException;
import java.io.IOException;

public abstract class ProcessReturnAction implements ReturnAction {

    @Override
    public OperationStepDef doAction(
            OperationDef operationDef,
            OperationStepDef stepDef,
            OperationContext context) throws ServletException, IOException {

        return nextStep(operationDef, stepDef);
    }

    protected abstract OperationStepDef nextStep(
            OperationDef operationDef, OperationStepDef stepDef);
}
