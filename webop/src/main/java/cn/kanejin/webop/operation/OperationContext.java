package cn.kanejin.webop.operation;

import cn.kanejin.webop.operation.Message.Level;
import org.apache.commons.fileupload.FileItem;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @version $Id: OperationContext.java 166 2017-09-13 08:54:09Z Kane $
 * @author Kane Jin
 */
public interface OperationContext {

	/**
	 * 获取当前Operation的Http Request
	 * 
	 * @return 当前Operation的Http Request
	 */
	HttpServletRequest getRequest();

	/**
	 * 获取当前Operation的Http Response
	 * 
	 * @return 当前Operation的Http Response
	 */
	HttpServletResponse getResponse();
	
	/**
	 * 获取当前的ServletContext
	 * 
	 * @return 当前的ServletContext
	 */	
	ServletContext getServletContext();

	/**
	 * 获取当前Operation的Http Session
	 * 
	 * @return 当前Operation的Http Session
	 */
	HttpSession getSession();
	
	/**
	 * 从Operation的上下文中获取String类型的属性值
	 * <p>
	 * getParameter是{@link OperationContext#getAttribute getAttribute}的一个特例，即下列两个方法是同义的：
	 * <pre>
	 * String name = getParameter("name");
	 * String name = getAttribute("name", String.class);
	 * </pre>
	 * 
	 * <em>
	 * getParameter跟request.getParameter是相同的，如果parameter是一个数组的话，那两者取到的都是该数组的第一个元素。
	 * 所以当parameter是数组时，请使用{@link #getRequest()}取到request，然后再调用request.getParameterValues()取该数组。
	 * </em>
	 * 
	 * @param paramKey 属性键
	 * @return String类型的属性值
	 */
	String getParameter(String paramKey);
	
	String getParameter(String paramKey, String defaultValue);
	
	/**
	 * 判断当前请求是否包含上传文件
	 * <p>
	 * 即判断当前HttpRequest的ContentType是否是multipart/form-data
	 * 
	 * @return 当前请求是否包含上传文件
	 */
	boolean isMultipart();

	/**
	 * 获取上传的文件元素
	 * <p>
	 * 如果当前请求包含上传文件的话，根据文件元素的键获取上传的文件
	 * 
	 * @param fileItemKey 文件元素的键
	 * @return 上传的文件元素
	 */
	FileItem getFileItem(String fileItemKey);

	/**
	 * 获取上下文中的属性值
	 * <p>
	 * 
	 * 根据属性键从上下文中取得属性值，取出的属性值是Object类型，使用时<em>需要</em>进行转换，例如：
	 * <pre>
	 * Long userId = (Long) context.getAttribute("userId");
	 * </pre>
	 * 
	 * @param attKey 属性键
	 * @return 属性值
	 */
	Object getAttribute(String attKey);

	/**
	 * 获取上下文中的属性值
	 * <p>
	 * 
	 * 根据属性键从上下文中取得属性值，属性值的类型作为第二个参数传给getAttribute，使用时<em>不需要</em>进行转换，例如：
	 * <pre>
	 * Long userId = context.getAttribute("userId", Long.class);
	 * </pre>
	 * 
	 * @param attKey 属性键
	 * @param clazz 属性类型
	 * @return 属性值
	 */
	<T> T getAttribute(String attKey, Class<T> clazz);

	/**
	 * 设置属性值到上下文中
	 * 
	 * @param attKey 属性键
	 * @param obj 属性值
	 */
	void setAttribute(String attKey, Object obj);
	
	/**
	 * 从上下文中删除属性值
	 * 
	 * @param attKey 属性键
	 */
	void removeAttribute(String attKey);

	/**
	 * 设置提示信息到上下文
	 * 
	 * @param level 信息级别：SUCCESS（成功）、INFO（信息）、ERROR（错误）
	 * @param text 信息的内容
	 */
	void setMessage(Level level, String text);

	/**
	 * 获取上下文中的提示信息
	 * 
	 * @return
	 */
	Message getMessage();
	
	/**
	 * 获取请求的IP地址
	 * 
	 * @return
	 */
	String getIp();

	/**
	 * 设置返回链接到上下文中
	 * 
	 * @param url
	 */
	void setBackUrl(String url);
	
	/**
	 * 获取返回链接
	 * 
	 * @return
	 */
	String getBackUrl();
	
	/**
	 * 创建一个新的token，并把这个token放在上下文中
	 * 
	 * @return
	 */
	String newToken();
	
	/**
	 * 获取当前上下文中的token
	 * <p>
	 * 如果上下文中没有token，则创建一个新的
	 * 
	 * @return
	 */
	String getToken();

	/**
	 * 删除当前上下文中的token
	 * 
	 */
	void cleanToken();

	/**
	 * 对比请求中的token和上下文中的是否一致
	 * 
	 * @param requestToken
	 * @return
	 */
	boolean checkToken(String requestToken);
	
	/**
	 * 获取当前请求URL
	 * 
	 * @param withQueryString 是否带上参数串
	 * 
	 * @return
	 */
	String getCurrentUrl(boolean withQueryString);
}
