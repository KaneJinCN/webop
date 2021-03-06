package cn.kanejin.webop.core;

import cn.kanejin.webop.core.def.OperationStepDef;

import java.util.HashMap;
import java.util.Map;

/**
 * 用来存放OperationStep。存放时键值为Step的Class + init-params
 *
 * @author Kane Jin
 */
public class OperationStepMapping {
    private final Map<String, OperationStep> steps;

    public OperationStepMapping() {
        steps = new HashMap<>();
    }

    public OperationStep get(OperationStepDef stepDef) {
        String key = generateKey(stepDef);

        OperationStep step = steps.get(key);

        if (step == null) {
            step = createStep(stepDef);
            steps.put(key, step);
        }

        return step;
    }

    public OperationStep createStep(OperationStepDef stepDef) {

        try {
            OperationStep step =
                    (OperationStep) Class.forName(stepDef.getClazz()).newInstance();

            if (step instanceof InitializableStep) {
                ((InitializableStep) step).init(stepDef.getInitParams());
            }

            ResourceInjector.getInstance().inject(step);

            return step;
        } catch (Exception e) {
            throw new OperationException("Instant step class[" + stepDef.getClazz() + "] error", e);
        }
    }

    private String generateKey(OperationStepDef stepDef) {
        String className = stepDef.getClazz();

        Map<String, String> params = stepDef.getInitParams();

        if (params == null)
            return className;

        StringBuilder sb = new StringBuilder(className);
        for (String key : params.keySet()) {
            sb.append('_');
            sb.append(key);
            sb.append('-');
            sb.append(params.get(key));
        }

        return sb.toString();
    }
}
