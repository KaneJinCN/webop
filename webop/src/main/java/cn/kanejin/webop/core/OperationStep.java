package cn.kanejin.webop.core;

import java.io.Serializable;

/**
 * @version $Id: OperationStep.java 169 2017-09-17 12:26:25Z Kane $
 * @author Kane Jin
 */
public interface OperationStep extends Serializable {
	/**
	 * 执行Step，在这里实现Step的业务逻辑
	 * 
	 * @param context Operation上下文
	 * @return 执行正常时返回 &gt;= 0的整数，异常时返回&lt;0的整数(通常是-1)
	 */
	int execute(OperationContext context);
}
