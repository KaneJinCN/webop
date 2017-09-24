package cn.kanejin.webop.sample.opstep;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.OperationStep;
import cn.kanejin.webop.core.annotation.PathVar;
import cn.kanejin.webop.core.annotation.StepMethod;

/**
 * @author Kane Jin
 */
public class PathVarStep implements OperationStep {

    @StepMethod
    public int execute(OperationContext context,
                       @PathVar(name = "count") Integer count) {

        return count;
    }
}
