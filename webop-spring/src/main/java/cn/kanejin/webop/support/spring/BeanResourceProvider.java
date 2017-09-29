package cn.kanejin.webop.support.spring;

import cn.kanejin.webop.core.ResourceProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import static cn.kanejin.commons.util.StringUtils.isBlank;

/**
 * @author Kane Jin
 */
public class BeanResourceProvider implements ResourceProvider {

    @Override
    public <T> T getResource(String name, Class<T> type) {

        ApplicationContext ac = ContextLoader.getCurrentWebApplicationContext();

        if (ac == null) {
            throw new RuntimeException("No WebApplicationContext found in ContextLoader");
        }

        if (isBlank(name)) {
            return ac.getBean(type);
        }

        return ac.getBean(name, type);
    }
}
