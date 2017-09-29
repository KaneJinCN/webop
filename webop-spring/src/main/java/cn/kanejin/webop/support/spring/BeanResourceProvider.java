package cn.kanejin.webop.support.spring;

import cn.kanejin.webop.core.ResourceProvider;
import org.springframework.web.context.ContextLoader;

/**
 * @author Kane Jin
 */
public class BeanResourceProvider implements ResourceProvider {

    @Override
    public <T> T getResource(String name, Class<T> type) {

        return ContextLoader.getCurrentWebApplicationContext().getBean(name, type);

    }
}
