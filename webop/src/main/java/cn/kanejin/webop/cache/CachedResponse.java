package cn.kanejin.webop.cache;

import cn.kanejin.webop.core.def.CacheExpiryDef;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @author Kane Jin
 */
public class CachedResponse implements Serializable {
    private final CacheExpiryDef expiryDef;

    private int statusCode;
    private String contentType;
    private byte[] content;

    public CachedResponse(CacheExpiryDef expiryDef) {
        this.expiryDef = expiryDef;
    }

    public void setResponse(int statusCode, String contentType, byte[] content) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.content = content;

    }

    public CacheExpiryDef getExpiryDef() {
        return expiryDef;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public boolean isOk() {
        return statusCode == HttpServletResponse.SC_OK;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
