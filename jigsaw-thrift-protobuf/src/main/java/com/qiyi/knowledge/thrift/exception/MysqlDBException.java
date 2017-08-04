package com.qiyi.knowledge.thrift.exception;

/**
 * mysql data base exception
 * @author Hu Xia<xiahu@qiyi.com>
 *
 */
public class MysqlDBException extends Exception {

	private static final long serialVersionUID = -3849019615644280930L;

	public MysqlDBException(String msg) {
		super(msg);
	}
}
