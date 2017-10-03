package cn.kanejin.webop.core;

import cn.kanejin.commons.util.StringUtils;
import cn.kanejin.webop.core.exception.IllegalConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;

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
                    } catch (IllegalAccessException e) {
                        log.warn("Can't inject resource '" + field.getGenericType() + "'.", e);
                    }
                }
            });
        }

        // 递归注入所有父类里的Resource
        doInject(obj, clazz.getSuperclass());
    }

    private Object lookupResource(Resource ann, Class<?> type) {
        if (resourceProvider == null) {
            resourceProvider = createResourceProvider();
        }

        return resourceProvider.getResource(ann.name(), type);
    }

    private ResourceProvider createResourceProvider() {

        String providerClass = WebopContext.get().getConfig("webop.resource.provider");

        if (StringUtils.isBlank(providerClass)) {
            throw new IllegalConfigException("Webop configuration 'webop.resource.provider' is required");
        }

        try {
            ResourceProvider provider = (ResourceProvider) Class.forName(providerClass).newInstance();
            provider.init(WebopContext.get().getServletContext());

            return provider;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalConfigException(
                    "Can't instantiate resource provider '" + providerClass + "' correctly." +
                            " Check for webop configuration 'webop.resource.provider'.", e);
        }
    }
}
