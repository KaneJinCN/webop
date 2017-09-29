package cn.kanejin.webop.core;

/**
 * @author Kane Jin
 */
public interface ResourceProvider {
    <T> T getResource(String name, Class<T> type);
}
