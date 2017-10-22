package cn.kanejin.webop;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static cn.kanejin.commons.util.StringUtils.isBlank;

/**
 * @author Kane Jin
 */
public class MultipartRequestWrapper extends HttpServletRequestWrapper {
	private static final Logger log = LoggerFactory.getLogger(MultipartRequestWrapper.class);

	private Map<String, String[]> parameters;
	private Map<String, FileItem> fileItems;

	public MultipartRequestWrapper(HttpServletRequest request, String encoding) {
		this(request, encoding, System.getProperty("java.io.tmpdir"), 1024*1024);
	}

	public MultipartRequestWrapper(HttpServletRequest request, String encoding, long maxSize) {
		this(request, encoding, System.getProperty("java.io.tmpdir"), maxSize);
	}

	public MultipartRequestWrapper(HttpServletRequest request,
                                   String encoding,
                                   String repositoryPath, long maxSize) {
		super(request);
		init(request, encoding, repositoryPath, maxSize);
	}

	private void init(HttpServletRequest request, String encoding, String repositoryPath,
			long maxSize) {
		log.debug("Initializing MultipartRequest with repositoryPath : {}", repositoryPath);
	    if (!ServletFileUpload.isMultipartContent(request)) {
	        throw new RuntimeException(
	        	"ContentType is not multipart/form-data but '" + request.getContentType() + "'");
	    }
		this.parameters = new HashMap<>();
		this.fileItems = new HashMap<>();
		DiskFileItemFactory factory = new DiskFileItemFactory();

		factory.setRepository(new File(repositoryPath));

		ServletFileUpload upload = new ServletFileUpload(factory);

		String charset = encoding;
		if (isBlank(charset)) {
			charset = request.getCharacterEncoding();
		}
		if (isBlank(charset)) {
			charset = "UTF-8";
		}

		upload.setSizeMax(maxSize);
		upload.setHeaderEncoding(charset);

		List<FileItem> itemList;
		try {
			itemList = upload.parseRequest(request);
		} catch (FileUploadException e) {
			throw new RuntimeException("Upload File Error", e);
		}

		log.debug("Start Extracting Parameters");
		for (FileItem item : itemList) {
			String key = item.getFieldName();
			if (item.isFormField()) {
				String[] inStock = this.parameters.get(key);
				if (inStock == null) {
					String[] values;
					try {
						values = new String[] { item.getString(charset) };
					} catch (UnsupportedEncodingException e) {
						log.error(e.toString());
						values = new String[] { item.getString() };
					}
					this.parameters.put(key, values);

					log.debug("Extracting Parameter : [{}]=[{}]", key, values);
				} else {
					String[] values = new String[inStock.length + 1];
					System.arraycopy(inStock, 0, values, 0, inStock.length);
					try {
						values[inStock.length] = item.getString("UTF-8");
					} catch (UnsupportedEncodingException e) {
						log.error(e.toString());
						values[inStock.length] = item.getString();
					}
					this.parameters.put(key, values);
					log.debug("Extracting Parameter : [{}]=[{}]", key, values);
				}
			} else {
				this.fileItems.put(key, item);
				log.debug("Extracting File Uploaded : [{}]=[{}]", key, item.getName());
			}
		}
	}
	
	public Enumeration<String> getFileItemKeys() {
		return Collections.enumeration(this.fileItems.keySet());
	}

	public FileItem getFileItem(String key) {
		if (this.fileItems != null) {
			return this.fileItems.get(key);
		}
		return null;
	}

	@Override
	public String getParameter(String key) {
		String parameter = null;
		String[] values = this.parameters.get(key);
		if (values != null && values.length > 0) {
			parameter = values[0];
		}
		return parameter;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(this.parameters.keySet());
	}

	@Override
	public String[] getParameterValues(String key) {
		return this.parameters.get(key);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return this.parameters;
	}
}
