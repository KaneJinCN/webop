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

package cn.kanejin.webop.core.view;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kane Jin
 */
public interface ViewRenderer {
    /**
     * 设置ServletContext
     * @param servletContext
     */
    void setServletContext(ServletContext servletContext);

    /**
     *
     * @param prefix
     */
    void setPrefix(String prefix);

    /**
     *
     * @param suffix
     */
    void setSuffix(String suffix);

    /**
     *
     * @param contentType
     */
    void setContentType(String contentType);

    /**
     *
     * @param pageName
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    void render(String pageName,
                HttpServletRequest request,
                HttpServletResponse response)
            throws ServletException, IOException;
}
