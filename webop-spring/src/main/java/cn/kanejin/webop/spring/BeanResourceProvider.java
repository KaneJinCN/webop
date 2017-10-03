package cn.kanejin.webop.spring;

import cn.kanejin.webop.core.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

import static cn.kanejin.commons.util.StringUtils.isBlank;

/**
 * @author Kane Jin
 */
public class BeanResourceProvider implements ResourceProvider {
    private static final Logger log = LoggerFactory.getLogger(BeanResourceProvider.class);

    private ServletContext servletContext;

    @Override
    public void init(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public <T> T getResource(String name, Class<T> type) {

        ApplicationContext ac =
                WebApplicationContextUtils.getWebApplicationContext(servletContext);

        if (ac == null) {
            log.warn("No WebApplicationContext found in ServletContext");

            return null;
        }

        if (isBlank(name)) {
            return ac.getBean(type);
        }

        return ac.getBean(name, type);
    }
}
