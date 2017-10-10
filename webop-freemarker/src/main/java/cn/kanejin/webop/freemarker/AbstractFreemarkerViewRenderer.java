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

/*
 * This code is partially copied from FreemarkerServlet in freemarker.jar
 *
 * Copyright 2014 Attila Szegedi, Daniel Dekany, Jonathan Revusky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.kanejin.webop.freemarker;

import cn.kanejin.webop.core.exception.IllegalConfigException;
import cn.kanejin.webop.core.view.AbstractViewRenderer;
import cn.kanejin.webop.core.view.FreemarkerViewRenderer;
import freemarker.core.Configurable;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.jsp.TaglibFactory.MetaInfTldSource;
import freemarker.template.*;
import freemarker.template.utility.SecurityUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import static cn.kanejin.commons.util.StringUtils.*;
import static freemarker.ext.servlet.FreemarkerServlet.*;

/**
 * @author Kane Jin
 */
public abstract class AbstractFreemarkerViewRenderer extends AbstractViewRenderer implements FreemarkerViewRenderer {

    private static final Logger log = LoggerFactory.getLogger(AbstractFreemarkerViewRenderer.class);

    protected boolean contiansCharsetInContentType = false;

    protected String templatePath;

    protected boolean noCache;

    protected Integer bufferSize;

    protected boolean exceptionOnMissingTemplate;

    protected List<MetaInfTldSource> metaInfTldSources;

    protected List<String> classpathTlds;

    @Override
    public void setSettings(Properties settings) {
        createConfiguration(settings);
    }


    @Override
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    @Override
    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }

    @Override
    public void setBufferSize(Integer bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void setContentType(String contentType) {
        super.setContentType(contentType);

        contiansCharsetInContentType = Pattern.matches(".*;\\s*charset=.*", contentType.toLowerCase());
    }

    @Override
    public void setExceptionOnMissingTemplate(boolean exceptionOnMissingTemplate) {
        this.exceptionOnMissingTemplate = exceptionOnMissingTemplate;
    }

    @Override
    public void setMetaInfTldSources(String metaInfTldSourcesValue) {
        try {
            this.metaInfTldSources = isBlank(metaInfTldSourcesValue)
                    ? createDefaultMetaInfTldSources()
                    : parseAsMetaInfTldLocations(metaInfTldSourcesValue);
        } catch (ParseException e) {
            throw new IllegalConfigException(
                    "Freemarker config <meta-info-tld-sources> error", e);
        }
    }

    @Override
    public void setClasspathTlds(String classpathTldsValue) {
        this.classpathTlds = isBlank(classpathTldsValue)
                ? createDefaultClassPathTlds()
                : parseCommaSeparatedList(classpathTldsValue);
    }

    protected Configuration config;

    private void createConfiguration(Properties settings) {
        config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

        // Only override what's coming from the config if it was explicitly specified:
        final String iciInitParamValue = settings.getProperty(Configuration.INCOMPATIBLE_IMPROVEMENTS_KEY);
        if (iciInitParamValue != null) {
            try {
                config.setSetting(Configuration.INCOMPATIBLE_IMPROVEMENTS_KEY, iciInitParamValue);
            } catch (TemplateException e) {
                throw new IllegalConfigException(
                        "Freemarker settings '" + Configuration.INCOMPATIBLE_IMPROVEMENTS_KEY + "' error", e);
            }
        }

        // Set FreemarkerServlet-specific defaults, except where createConfiguration() has already set them:
        if (!config.isTemplateExceptionHandlerExplicitlySet()) {
            config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        }
        if (!config.isLogTemplateExceptionsExplicitlySet()) {
            config.setLogTemplateExceptions(false);
        }

        if (isNotBlank(templatePath)) {
            config.setServletContextForTemplateLoading(servletContext, templatePath);
        }

        // Process object_wrapper setting out of order:
        setObjectWrapper(settings.getProperty(Configurable.OBJECT_WRAPPER_KEY));

        // Process all other settings:
        Enumeration settingNames = settings.keys();
        while (settingNames.hasMoreElements()) {
            final String name = (String) settingNames.nextElement();
            final String value = settings.getProperty(name);
            if (isBlank(value)) {
                throw new IllegalConfigException(
                        "Freemarker settings '" + name + "' error");
            }

            try {
                if (name.equals(Configurable.OBJECT_WRAPPER_KEY)
                        || name.equals(Configuration.INCOMPATIBLE_IMPROVEMENTS_KEY)) {
                    // ignore: we have already processed these
                } else {
                        config.setSetting(name, value);
                }
            } catch (TemplateException e) {
                throw new IllegalConfigException(
                        "Freemarker settings '" + name + "' error", e);
            }
        } // while settingNames
    }

    private void setObjectWrapper(String wrapper) {
        if (isBlank(wrapper)) {
            if (!config.isObjectWrapperExplicitlySet()) {
                config.setObjectWrapper(createDefaultObjectWrapper());
            }
        } else {
            try {
                config.setSetting(Configurable.OBJECT_WRAPPER_KEY, wrapper);
            } catch (TemplateException e) {
                throw new IllegalConfigException(
                        "Freemarker settings '" + Configurable.OBJECT_WRAPPER_KEY + "' error", e);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Using object wrapper: " + config.getObjectWrapper());
        }
    }

    private ObjectWrapper createDefaultObjectWrapper() {
        return Configuration.getDefaultObjectWrapper(config.getIncompatibleImprovements());
    }

    private List<MetaInfTldSource> createDefaultMetaInfTldSources() {
        return TaglibFactory.DEFAULT_META_INF_TLD_SOURCES;
    }

    private List<MetaInfTldSource> parseAsMetaInfTldLocations(String value) throws ParseException {
        List<MetaInfTldSource> metaInfTldSources = null;

        List<String> values = parseCommaSeparatedList(value);
        for (Iterator it = values.iterator(); it.hasNext();) {
            final String itemStr = (String) it.next();
            final MetaInfTldSource metaInfTldSource;
            if (itemStr.equals(META_INF_TLD_LOCATION_WEB_INF_PER_LIB_JARS)) {
                metaInfTldSource = TaglibFactory.WebInfPerLibJarMetaInfTldSource.INSTANCE;
            } else if (itemStr.startsWith(META_INF_TLD_LOCATION_CLASSPATH)) {
                String itemRightSide = itemStr.substring(META_INF_TLD_LOCATION_CLASSPATH.length()).trim();
                if (itemRightSide.length() == 0) {
                    metaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(".*", Pattern.DOTALL));
                } else if (itemRightSide.startsWith(":")) {
                    final String regexpStr = itemRightSide.substring(1).trim();
                    if (regexpStr.length() == 0) {
                        throw new ParseException("Empty regular expression after \""
                                + META_INF_TLD_LOCATION_CLASSPATH + ":\"", -1);
                    }
                    metaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(regexpStr));
                } else {
                    throw new ParseException("Invalid \"" + META_INF_TLD_LOCATION_CLASSPATH
                            + "\" value syntax: " + value, -1);
                }
            } else if (itemStr.startsWith(META_INF_TLD_LOCATION_CLEAR)) {
                metaInfTldSource = TaglibFactory.ClearMetaInfTldSource.INSTANCE;
            } else {
                throw new ParseException("Item has no recognized source type prefix: " + itemStr, -1);
            }
            if (metaInfTldSources == null) {
                metaInfTldSources = new ArrayList();
            }
            metaInfTldSources.add(metaInfTldSource);
        }

        return metaInfTldSources;
    }

    private List<String> createDefaultClassPathTlds() {
        return TaglibFactory.DEFAULT_CLASSPATH_TLDS;
    }

    protected TaglibFactory createTaglibFactory(ServletContext servletContext,
                                                ObjectWrapper objectWrapper) {
        TaglibFactory taglibFactory = new TaglibFactory(servletContext);

        taglibFactory.setObjectWrapper(objectWrapper);

        {
            List<MetaInfTldSource> mergedMetaInfTldSources = new ArrayList<>();

            if (metaInfTldSources != null) {
                mergedMetaInfTldSources.addAll(metaInfTldSources);
            }

            String sysPropVal = SecurityUtilities.getSystemProperty(SYSTEM_PROPERTY_META_INF_TLD_SOURCES, null);
            if (sysPropVal != null) {
                try {
                    List metaInfTldSourcesSysProp = parseAsMetaInfTldLocations(sysPropVal);
                    if (metaInfTldSourcesSysProp != null) {
                        mergedMetaInfTldSources.addAll(metaInfTldSourcesSysProp);
                    }
                } catch (java.text.ParseException e) {
                    throw new IllegalConfigException("Failed to parse system property \""
                            + SYSTEM_PROPERTY_META_INF_TLD_SOURCES + "\"", e);
                }
            }

//            List/*<Pattern>*/ jettyTaglibJarPatterns = null;
//            try {
//                final String attrVal = (String) servletContext.getAttribute(ATTR_JETTY_CP_TAGLIB_JAR_PATTERNS);
//                jettyTaglibJarPatterns = attrVal != null ? InitParamParser.parseCommaSeparatedPatterns(attrVal) : null;
//            } catch (Exception e) {
//                LOG.error("Failed to parse application context attribute \""
//                        + ATTR_JETTY_CP_TAGLIB_JAR_PATTERNS + "\" - it will be ignored", e);
//            }
//            if (jettyTaglibJarPatterns != null) {
//                for (Iterator/*<Pattern>*/ it = jettyTaglibJarPatterns.iterator(); it.hasNext();) {
//                    Pattern pattern = (Pattern) it.next();
//                    mergedMetaInfTldSources.add(new TaglibFactory.ClasspathMetaInfTldSource(pattern));
//                }
//            }

            taglibFactory.setMetaInfTldSources(mergedMetaInfTldSources);
        }

        {
            List<String> mergedClassPathTlds = new ArrayList<>();
            if (classpathTlds != null) {
                mergedClassPathTlds.addAll(classpathTlds);
            }

            String sysPropVal = SecurityUtilities.getSystemProperty(SYSTEM_PROPERTY_CLASSPATH_TLDS, null);
            if (sysPropVal != null) {
                List<String> classpathTldsSysProp = parseCommaSeparatedList(sysPropVal);
                if (classpathTldsSysProp != null) {
                    mergedClassPathTlds.addAll(classpathTldsSysProp);
                }
            }

            taglibFactory.setClasspathTlds(mergedClassPathTlds);
        }

        return taglibFactory;
    }

    private List<String> parseCommaSeparatedList(String value) {
        if (isBlank(value))
            return new ArrayList<>(0);

        List<String> valuesList = new ArrayList<>();

        String[] values = value.trim().split("(\\s*,\\s*)|(\\s+)");
        for (int i = 0; i < values.length; i++) {
            final String s = values[i].trim();
            if (isNotEmpty(s)) {
                valuesList.add(s);
            }
        }
        return valuesList;
    }

    private static final String EXPIRATION_DATE;

    static {
        // Generate expiration date that is one year from now in the past
        GregorianCalendar expiration = new GregorianCalendar();
        expiration.roll(Calendar.YEAR, -1);
        SimpleDateFormat httpDate =
                new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss z",
                        java.util.Locale.US);
        EXPIRATION_DATE = httpDate.format(expiration.getTime());
    }

    protected void setBrowserCachingPolicy(HttpServletResponse res)
    {
        if (noCache)
        {
            // HTTP/1.1 + IE extensions
            res.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, "
                    + "post-check=0, pre-check=0");
            // HTTP/1.0
            res.setHeader("Pragma", "no-cache");
            // Last resort for those that ignore all of the above
            res.setHeader("Expires", EXPIRATION_DATE);
        }
    }

}
