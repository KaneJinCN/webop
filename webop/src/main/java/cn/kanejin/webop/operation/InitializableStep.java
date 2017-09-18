package cn.kanejin.webop.operation;

import java.util.Map;

/**
 * @author Kane Jin
 */
public interface InitializableStep extends OperationStep {
    /**
     * 初始化Step
     *
     * @param params 初始化参数
     */
    void init(Map<String, String> params);
}
