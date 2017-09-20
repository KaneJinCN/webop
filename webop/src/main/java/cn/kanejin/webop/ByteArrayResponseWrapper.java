package cn.kanejin.webop;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * @author Kane Jin
 */
public class ByteArrayResponseWrapper extends HttpServletResponseWrapper implements Serializable {
    private int statusCode = SC_OK;

    private ByteArrayServletOutputStream servletOutputStream;
    private PrintWriter writer;

    public ByteArrayResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        this.servletOutputStream = new ByteArrayServletOutputStream();
    }

    public byte[] toByteArray() {
        return servletOutputStream.toByteArray();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return servletOutputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null)
            writer = new PrintWriter(
                    new OutputStreamWriter(servletOutputStream, getCharacterEncoding()), true);

        return writer;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public void setStatus(int sc) {
        this.statusCode = sc;

        super.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        this.statusCode = sc;
        super.setStatus(sc, sm);
    }

    public void flush() throws IOException {
        if (writer != null) {
            writer.flush();
        }

        servletOutputStream.flush();
    }

    private static class ByteArrayServletOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream outputStream;

        public ByteArrayServletOutputStream() {
            this.outputStream = new ByteArrayOutputStream();
        }

        public byte[] toByteArray() {
            return this.outputStream.toByteArray();
        }

        @Override
        public void write(final int b) throws IOException {
            outputStream.write(b);
        }

        @Override
        public void flush() throws IOException {
            super.flush();

            outputStream.flush();
        }
    }
}
