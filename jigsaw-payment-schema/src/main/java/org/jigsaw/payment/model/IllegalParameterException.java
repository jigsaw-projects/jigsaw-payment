/**
 * 
 */
package org.jigsaw.payment.model;

/**
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月10日
 */
public class IllegalParameterException extends RuntimeException{

	private static final long serialVersionUID = 1522222813099906369L;

	private long code;

	public IllegalParameterException(long code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public IllegalParameterException(long code, String message) {
		super(message);
		this.code = code;
	}
	
	public long getCode() {
		return this.code;
	}

}
