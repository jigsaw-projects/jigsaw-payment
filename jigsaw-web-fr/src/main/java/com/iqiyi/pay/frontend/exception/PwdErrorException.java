/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.exception;


/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月22日
 */
@SuppressWarnings("serial")
public class PwdErrorException extends Exception {
	
	private int count;
	
	public PwdErrorException(int count) {
		this.count = count < 0? 0 : count;
	}

	public int getCount() {
		return count;
	}

	@Override
	public String getMessage() {
		return "支付密码错误，您还可以输入" + count + "次";
	}
	
	
	
	
}
