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

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author Kane Jin
 */
public class LogHelper {

    public static void logOperationParameters(Logger log, Operation op, OperationContext context) {
        if (log.isDebugEnabled()) {

            Enumeration<String> paramEnum = context.getRequest().getParameterNames();

            if (paramEnum.hasMoreElements())
                log.debug("%%%% Parameters in Context of Operation[{}]:", op.getUri());

            while (paramEnum.hasMoreElements()) {
                String name = paramEnum.nextElement();
                if (name.endsWith("[]")) {
                    String[] value = context.getRequest().getParameterValues(name);
                    log.debug("%%%% [{}] = {}", name.substring(0, name.length() - 2), Arrays.toString(value));
                } else {
                    String value = context.getRequest().getParameter(name);
                    log.debug("%%%% [{}] = [{}]", name, value);
                }
            }

            Map<String, String> pathVars =
                    (Map<String, String>) context.getRequest().getAttribute(Constants.PATH_VAR);

            if (pathVars != null) {

                log.debug("%%%% Path Variables in Context of Operation[{}]:", op.getUri());

                for (String key : pathVars.keySet())
                    log.debug("%%%% [{}] = [{}]", key, pathVars.get(key));
            }


            if (context.isMultipart()) {
                Enumeration<String> fileEnum = context.getFileItemKeys();

                if (fileEnum.hasMoreElements())
                    log.debug("%%%% FileItems in Context of Operation[{}]:", op.getUri());

                while (fileEnum.hasMoreElements()) {
                    String key = fileEnum.nextElement();
                    FileItem item = context.getFileItem(key);
                    log.debug("%%%% [{}] = [{}], size=[{}], contentType=[{}]",
                            new Object[]{key, item.getName(), item.getSize(), item.getContentType()});
                }
            }
        }
    }
}
