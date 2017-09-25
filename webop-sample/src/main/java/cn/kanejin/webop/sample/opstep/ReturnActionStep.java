package cn.kanejin.webop.sample.opstep;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.OperationStep;
import cn.kanejin.webop.core.annotation.Param;
import cn.kanejin.webop.core.annotation.StepMethod;
import org.apache.commons.fileupload.FileItem;

public class ReturnActionStep implements OperationStep {

	@StepMethod
	public int execute(OperationContext context,
					   @Param(name = "return") Integer returnValue,
					   @Param(name = "avatar") FileItem avatar,
					   @Param(name = "followers") String[] followers,
					   @Param(name = "numbers") Integer[] numbers
	) {

		if (returnValue == null)
			return 0;

		return returnValue.intValue();
	}

}
