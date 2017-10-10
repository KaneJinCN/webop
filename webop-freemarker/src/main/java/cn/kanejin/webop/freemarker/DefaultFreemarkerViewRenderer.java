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

import freemarker.core.ParseException;
import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.*;
import freemarker.template.*;
import freemarker.template.utility.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

import static freemarker.ext.servlet.FreemarkerServlet.*;

/**
 * @author Kane Jin
 */
public class DefaultFreemarkerViewRenderer extends AbstractFreemarkerViewRenderer {
    private static final Logger log = LoggerFactory.getLogger(DefaultFreemarkerViewRenderer.class);

    @Override
    public void render(String templateName,
                       HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (bufferSize != null && !response.isCommitted()) {
            try {
                response.setBufferSize(bufferSize);
            } catch (IllegalStateException e) {
                log.debug("Can't set buffer size any more,", e);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Requested template " + StringUtil.jQuoteNoXSS(templateName));
        }

        Template template = null;
        try {
            template = config.getTemplate(templateName);
        } catch (TemplateNotFoundException e) {
            e.printStackTrace();
            if (exceptionOnMissingTemplate) {
                throw newServletExceptionWithFreeMarkerLogging(
                        "Template not found for name " + StringUtil.jQuoteNoXSS(templatePath) + ".", e);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Responding HTTP 404 \"Not found\" for missing template "
                            + StringUtil.jQuoteNoXSS(templatePath) + ".", e);
                }
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page template not found");
                return;
            }
        } catch (ParseException e) {
            throw newServletExceptionWithFreeMarkerLogging(
                    "Parsing error with template " + StringUtil.jQuoteNoXSS(templatePath) + ".", e);
        } catch (Exception e) {
            throw newServletExceptionWithFreeMarkerLogging(
                    "Unexpected error when loading template " + StringUtil.jQuoteNoXSS(templatePath) + ".", e);
        }

        Object attrContentType = template.getCustomAttribute("content_type");
        if(attrContentType != null) {
            response.setContentType(attrContentType.toString());
        } else {
            if (contiansCharsetInContentType) {
                response.setContentType(contentType);
            } else {
                response.setContentType(contentType + ";charset=" + template.getEncoding());
            }
        }

        setBrowserCachingPolicy(response);

        try {
            TemplateModel model = createModel(config.getObjectWrapper(), servletContext, request, response);

            template.process(model, response.getWriter());
        } catch (TemplateException e) {
            final TemplateExceptionHandler teh = config.getTemplateExceptionHandler();
            // Ensure that debug handler responses aren't rolled back:
            if (teh == TemplateExceptionHandler.HTML_DEBUG_HANDLER || teh == TemplateExceptionHandler.DEBUG_HANDLER
                    || teh.getClass().getName().indexOf("Debug") != -1) {
                response.flushBuffer();
            }
            throw newServletExceptionWithFreeMarkerLogging("Error executing FreeMarker template", e);
        }
    }

    protected TemplateModel createModel(ObjectWrapper objectWrapper,
                                        ServletContext servletContext,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response) throws TemplateModelException {
        AllHttpScopesHashModel model = new AllHttpScopesHashModel(objectWrapper, servletContext, request);

        // Create hash model wrapper for servlet context (the application)
        if (this.servletContextModel == null) {
            buildServletContextModel(servletContext, objectWrapper);
        }

        model.putUnlistedModel(KEY_APPLICATION, servletContextModel);
        model.putUnlistedModel(KEY_JSP_TAGLIBS, taglibFactory);
        model.putUnlistedModel(KEY_SESSION, buildHttpSessionHashModel(request, response, objectWrapper));
        model.putUnlistedModel(KEY_REQUEST, new HttpRequestHashModel(request, response, objectWrapper));
        model.putUnlistedModel(KEY_INCLUDE, new IncludePage(request, response));
        model.putUnlistedModel(KEY_REQUEST_PARAMETERS, new HttpRequestParametersHashModel(request));

        return model;
    }

    private ServletContextHashModel servletContextModel;
    private TaglibFactory taglibFactory;

    private synchronized void buildServletContextModel(ServletContext servletContext, ObjectWrapper objectWrapper) {
        if (this.servletContextModel != null)
            return ;

        GenericServlet servlet = new GenericServlet() {
            @Override
            public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {}
        };

        try {
            servlet.init(new ServletConfig() {
                @Override
                public String getServletName() {
                    return "FreemarkerViewRenderer";
                }

                @Override
                public ServletContext getServletContext() {
                    return DefaultFreemarkerViewRenderer.this.servletContext;
                }

                @Override
                public String getInitParameter(String name) {
                    return null;
                }

                @Override
                public Enumeration getInitParameterNames() {
                    return null;
                }
            });
        } catch (ServletException ex) {}


        this.servletContextModel = new ServletContextHashModel(servlet, objectWrapper);
        this.taglibFactory = createTaglibFactory(servletContext, objectWrapper);
    }

    private HttpSessionHashModel buildHttpSessionHashModel(final HttpServletRequest request,
                                                           final HttpServletResponse response,
                                                           ObjectWrapper objectWrapper ) {
        // Create hash model wrapper for session
        HttpSession session = request.getSession(false);
        if (session != null) {
            return new HttpSessionHashModel(session, objectWrapper);
        }
        else {
            return new HttpSessionHashModel(null, request, response, objectWrapper);
        }
    }


    private ServletException newServletExceptionWithFreeMarkerLogging(String message, Throwable cause) throws ServletException {
        log.error(message, cause);

        ServletException e = new ServletException(message, cause);
        try {
            // Prior to Servlet 2.5, the cause exception wasn't set by the above constructor.
            // If we are on 2.5+ then this will throw an exception as the cause was already set.
            e.initCause(cause);
        } catch (Exception ex) {
            // Ignored; see above
        }
        throw e;
    }
}
