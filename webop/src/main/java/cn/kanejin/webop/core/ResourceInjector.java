package cn.kanejin.webop.core;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kane Jin
 */
public class ResourceInjector {
    private static ResourceInjector injector;

    private ResourceProvider resourceProvider;

    private ResourceInjector() {
    }

    public static ResourceInjector getInstance() {
        if (injector == null)
            injector = new ResourceInjector();
        return injector;
    }

    public void inject(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();

        if (fields == null || fields.length == 0)
            return;

        Arrays.stream(fields).forEach(field -> {

            Resource ann = field.getDeclaredAnnotation(Resource.class);

            if (ann != null) {
                try {
                    field.setAccessible(true);
                    field.set(obj, lookupResource(ann, field.getType()));
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
}
