package cn.kanejin.webop.spring;

import cn.kanejin.webop.core.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

import static cn.kanejin.commons.util.StringUtils.isBlank;

/**
 * @author Kane Jin
 */
public class BeanResourceProvider implements ResourceProvider {
    private static final Logger log = LoggerFactory.getLogger(BeanResourceProvider.class);

    private ApplicationContext applicationContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.applicationContext =
                WebApplicationContextUtils.getWebApplicationContext(servletContext);

        if (applicationContext == null) {
            throw new IllegalStateException(
                    "No WebApplicationContext found in ServletContext");
        }
    }

    @Override
    public <T> T getResource(String name, Class<T> type) {
        return isBlank(name)
                ? applicationContext.getBean(type)
                : applicationContext.getBean(name, type);
    }
}
