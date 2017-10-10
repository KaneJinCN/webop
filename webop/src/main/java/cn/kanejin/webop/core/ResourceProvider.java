package cn.kanejin.webop.core;

import javax.servlet.ServletContext;

/**
 * @author Kane Jin
 */
public interface ResourceProvider {
    void setServletContext(ServletContext servletContext);

    <T> T getResource(String name, Class<T> type);
}
