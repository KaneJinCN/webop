package cn.kanejin.webop.core;

import javax.servlet.ServletContext;

/**
 * @author Kane Jin
 */
public interface ResourceProvider {
    void init(ServletContext servletContext);

    <T> T getResource(String name, Class<T> type);
}
