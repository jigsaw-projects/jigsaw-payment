package com.qiyi.knowledge.thrift.exception;

/**
 * exception throws in invoke method
 * @author HuXia<xiahu@qiyi.com>
 *
 */
public class InvokeException extends RuntimeException {

	private static final long serialVersionUID = 1184782180199131737L;
	
	public InvokeException(String msg) {
		super(msg);
	}
	
	public InvokeException(Throwable cause) {
		super(cause);
	}
}
