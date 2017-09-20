package cn.kanejin.webop.core;

/**
 * @version $Id: OperationException.java 115 2016-03-15 06:34:36Z Kane $
 * @author Kane Jin
 */
public class OperationException extends RuntimeException {

	private static final long serialVersionUID = -8055615505048097467L;

	public OperationException(String message, Throwable cause) {
		super(message, cause);
	}

	public OperationException(String message) {
		super(message);
	}

}
