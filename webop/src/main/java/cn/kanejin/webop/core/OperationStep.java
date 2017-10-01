package cn.kanejin.webop.core;

import java.io.Serializable;

/**
 * 所有的Step类都应该实现OperationStep接口。
 * Step类的具体的业务方法请使用@StepMethod进行注解。
 *
 * @author Kane Jin
 */
public interface OperationStep extends Serializable { }
