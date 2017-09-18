package cn.kanejin.webop.util;

import javax.servlet.http.HttpServletRequest;

import static cn.kanejin.commons.util.StringUtils.isEmpty;
import static cn.kanejin.commons.util.StringUtils.isNotEmpty;

/**
 * @author Kane Jin
 */
public class WebUtils {
    public static String parseRequestURI(HttpServletRequest req) {

        String uri = req.getRequestURI();
        if (isEmpty(uri)) {
            return "/";
        }

        String contextPath = req.getContextPath();
        if (isNotEmpty(contextPath) && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }

        uri = uri.replaceAll("//", "/");

        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        if (uri.endsWith("/") && uri.length() > 1)
            uri = uri.substring(0, uri.length() - 1);

        return uri;
    }
}
