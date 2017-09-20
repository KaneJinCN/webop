package cn.kanejin.webop.cache;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

/**
 * @author Kane Jin
 */
public class CachedResponse implements Serializable {
    private long timeToLive;
    private long timeToIdle;

    private int statusCode;
    private String contentType;
    private byte[] content;

    public void setExpiry(long timeToLive, long timeToIdle) {
        this.timeToLive = timeToLive;
        this.timeToIdle = timeToIdle;
    }

    public void setResponse(int statusCode, String contentType, byte[] content) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.content = content;

    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public long getTimeToIdle() {
        return timeToIdle;
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
