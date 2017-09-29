package cn.kanejin.webop.core;

import cn.kanejin.webop.core.def.OperationStepDef;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kane Jin
 */
public class OperationStepContainer {
    private static OperationStepContainer container;

    private Map<String, OperationStep> steps = new HashMap<>();
    private ResourceProvider resourceProvider;

    private OperationStepContainer() {
    }

    public static OperationStepContainer getInstance() {
        if (container == null)
            container = new OperationStepContainer();
        return container;
    }

    public static OperationStep get(OperationStepDef stepDef) {
        return getInstance().getStep(stepDef);
    }

    public OperationStep getStep(OperationStepDef stepDef) {
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

            injectResource(step);

            return step;
        } catch (Exception e) {
            throw new OperationException("Instant step class[" +
                    stepDef.getClazz() + "] error", e);
        }
    }

    private void injectResource(OperationStep step) {
        Field[] fields = step.getClass().getDeclaredFields();

        if (fields == null || fields.length == 0)
            return;


        Arrays.stream(fields).forEach(field -> {

            Resource ann = field.getDeclaredAnnotation(Resource.class);

            if (ann != null) {
                try {
                    field.setAccessible(true);
                    field.set(step, lookupResource(ann, field.getType()));
                } catch (Throwable t) {
                    throw new OperationException("Inject resource " + field.getGenericType() + "error");
                }
            }
        });
    }

    private Object lookupResource(Resource ann, Class<?> type) {
        if (resourceProvider == null) {
            resourceProvider = createResourceProvider();
        }

        return resourceProvider.getResource(ann.name(), type);
    }

    private ResourceProvider createResourceProvider() {

        try {
            ResourceProvider provider = (ResourceProvider)
                    Class.forName("cn.kanejin.webop.support.spring.BeanResourceProvider")
                            .newInstance();

            return provider;
        } catch(Throwable t) {
            throw new OperationException("Can't get a resource provider", t);
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
