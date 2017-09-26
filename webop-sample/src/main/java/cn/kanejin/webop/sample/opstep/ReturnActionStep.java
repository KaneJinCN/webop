package cn.kanejin.webop.sample.opstep;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.OperationStep;
import cn.kanejin.webop.core.annotation.Param;
import cn.kanejin.webop.core.annotation.StepMethod;
import org.apache.commons.fileupload.FileItem;

public class ReturnActionStep implements OperationStep {

	@StepMethod
	public int execute(OperationContext context,
					   @Param(name = "name") String name,
					   @Param(name = "age", ifEmpty = "20") Integer age,
					   @Param(name = "avatar") FileItem avatar,
					   @Param(name = "followers") String[] followers
	) {

		System.out.println(name);
		System.out.println(age);
		System.out.println(avatar);
		System.out.println(followers);

		return 0;
	}

}
