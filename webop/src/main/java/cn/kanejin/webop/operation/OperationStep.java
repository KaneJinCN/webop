package cn.kanejin.webop.operation;

import java.io.Serializable;
import java.util.Map;

/**
 * @version $Id: OperationStep.java 169 2017-09-17 12:26:25Z Kane $
 * @author Kane Jin
 */
public interface OperationStep extends Serializable {
	/**
	 * 初始化Step
	 * 
	 * @param params 初始化参数
	 */
	void init(Map<String, String> params);
	
	/**
	 * 执行Step，在这里实现Step的业务逻辑
	 * 
	 * @param context Operation上下文
	 * @return 执行正常时返回 &gt;= 0的整数，异常时返回&lt;0的整数(通常是-1)
	 */
	int execute(OperationContext context);
}
