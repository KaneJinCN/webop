package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.def.OperationDef;
import cn.kanejin.webop.core.def.OperationStepDef;

import javax.servlet.ServletException;
import java.io.IOException;

public interface ReturnAction {
    /**
     * 执行返回动作需要执行的业务逻辑
     *
     * @param operationDef 当前返回动作所在Operation的定义
     * @param stepDef 当前返回动作所在OperationStep的定义
     * @param context 当前返回动作的Operation上下文
     *
     * @return 执行完后应该中转到的Step，如果是终结的返回动作，则返回null
     *
     * @throws ServletException
     * @throws IOException
     */
    OperationStepDef doAction(
            OperationDef operationDef,
            OperationStepDef stepDef,
            OperationContext context) throws ServletException, IOException;
}
