/*
 * Copyright (c) 2017 Kane Jin
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package cn.kanejin.webop.core;

import cn.kanejin.webop.core.exception.IllegalConfigException;
import cn.kanejin.webop.core.view.DefaultJspViewRenderer;
import cn.kanejin.webop.core.view.FreemarkerViewRenderer;
import cn.kanejin.webop.core.view.ViewRenderer;

import javax.servlet.ServletContext;
import java.util.Properties;

import static cn.kanejin.commons.util.StringUtils.isNotBlank;
import static cn.kanejin.commons.util.StringUtils.nullToEmpty;

/**
 * @author Kane Jin
 */
public class WebopConfig {
    private String charset;

    private String defaultViewType;

    private ViewRenderer jspViewRenderer;

    private ViewRenderer freemarkerViewRenderer;

    private ResourceProvider resourceProvider;

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        if (isNotBlank(this.charset)) {
            throw new IllegalConfigException("Configuration <charset> has been set already");
        }

        this.charset = nullTo(charset, DEFAULT_CHARSET);
    }

    public String getDefaultViewType() {
        return defaultViewType;
    }

    public void setDefaultViewType(String defaultViewType) {
        if (this.defaultViewType != null) {
            throw new IllegalConfigException("Configuration <view-type-default> has been set already");
        }

        this.defaultViewType = nullTo(defaultViewType, DEFAULT_VIEW_TYPE);
    }

    public ViewRenderer getJspViewRenderer() {
        return jspViewRenderer;
    }

    public ViewRenderer getFreemarkerViewRenderer() {
        return freemarkerViewRenderer;
    }

    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    public void setResourceProvider(String providerClass) {
        if (this.resourceProvider != null) {
            throw new IllegalConfigException("Configuration <resource-provider> has been set already");
        }

        providerClass = nullTo(providerClass, DEFAULT_RESOURCE_PROVIDER);

        try {
            resourceProvider = (ResourceProvider) Class.forName(providerClass).newInstance();
            resourceProvider.setServletContext(WebopContext.get().getServletContext());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalConfigException(
                    "Can't instantiate resource provider '" + providerClass + "' correctly." +
                            " Check for webop configuration tag '<resource-provider>'", e);
        }
    }

    public void setJspViewRenderer(ServletContext servletContext,
                                   String prefix, String suffix, String contentType) {

        if (this.jspViewRenderer != null) {
            throw new IllegalConfigException("Configuration <jsp-renderer> has been set already");
        }

        jspViewRenderer = new DefaultJspViewRenderer();
        jspViewRenderer.setServletContext(servletContext);

        jspViewRenderer.setPrefix(nullToEmpty(prefix));
        jspViewRenderer.setSuffix(nullToEmpty(suffix));
        jspViewRenderer.setContentType(nullTo(contentType, DEFAULT_CONTENT_TYPE));

    }

    public void setFreemarkerViewRenderer(ServletContext servletContext,
                                          String rendererClass,
                                          String prefix, String suffix, String contentType,
                                          String templatePath, Boolean noCache, Integer bufferSize,
                                          Boolean exceptionOnMissingTemplate,
                                          String metaInfTldSources, String classpathTlds,
                                          Properties settings) {

        if (this.freemarkerViewRenderer != null) {
            throw new IllegalConfigException("Configuration <freemarker-renderer> has been set already");
        }

        try {
            rendererClass = nullTo(rendererClass, DEFAULT_FM_RENDERER);

            FreemarkerViewRenderer renderer = (FreemarkerViewRenderer) Class.forName(rendererClass).newInstance();
            renderer.setServletContext(servletContext);

            renderer.setPrefix(nullToEmpty(prefix));
            renderer.setSuffix(nullToEmpty(suffix));
            renderer.setContentType(nullTo(contentType, DEFAULT_CONTENT_TYPE));

            renderer.setTemplatePath(nullTo(templatePath, "/"));
            renderer.setNoCache(nullTo(noCache, true));
            renderer.setBufferSize(bufferSize);
            renderer.setExceptionOnMissingTemplate(nullTo(exceptionOnMissingTemplate, true));

            renderer.setMetaInfTldSources(metaInfTldSources);
            renderer.setClasspathTlds(classpathTlds);

            renderer.setSettings(settings);

            this.freemarkerViewRenderer = renderer;

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new IllegalConfigException(
                    "Can't instantiate resource provider '" + rendererClass + "' correctly." +
                            " Check for webop configuration tag '<freemarker-renderer>'.", e);
        }
    }

    private static String nullTo(String src, String to) {
        return isNotBlank(src) ? src : to;
    }
    private static Boolean nullTo(Boolean src, Boolean to) {
        return src != null ? src : to;
    }

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String DEFAULT_VIEW_TYPE = "jsp";
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=UTF-8";
    private static final String DEFAULT_FM_RENDERER = "cn.kanejin.webop.freemarker.DefaultFreemarkerViewRenderer";
    private static final String DEFAULT_RESOURCE_PROVIDER = "cn.kanejin.webop.spring.BeanResourceProvider";

}
