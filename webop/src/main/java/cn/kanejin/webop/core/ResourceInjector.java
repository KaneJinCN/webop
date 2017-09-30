package cn.kanejin.webop.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kane Jin
 */
class ResourceInjector {
    private static Logger log = LoggerFactory.getLogger(ResourceInjector.class);

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
        doInject(obj, obj.getClass());
    }

    private void doInject(Object obj, Class<?> clazz) {

        if (clazz.equals(Object.class)) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        if (fields != null) {
            Arrays.stream(fields).forEach(field -> {

                Resource ann = field.getDeclaredAnnotation(Resource.class);

                if (ann != null) {
                    try {
                        field.setAccessible(true);
                        field.set(obj, lookupResource(ann, field.getType()));

                        log.trace("Inject " + field.getType().getSimpleName() + " in " + clazz);
                    } catch (Throwable t) {
                        throw new OperationException("Inject resource " + field.getGenericType() + " error", t);
                    }
                }
            });
        }

        doInject(obj, clazz.getSuperclass());
    }

    private Object lookupResource(Resource ann, Class<?> type) {
        if (resourceProvider == null) {
            resourceProvider = createResourceProvider();
        }

        return resourceProvider.getResource(ann.name(), type);
    }

    private ResourceProvider createResourceProvider() {

        try {
            String providerClass = WebopContext.get().getConfig("webop.resource.provider");

            ResourceProvider provider = (ResourceProvider) Class.forName(providerClass).newInstance();

            return provider;
        } catch(Throwable t) {
            throw new OperationException("Can't get a resource provider", t);
        }
    }
}
