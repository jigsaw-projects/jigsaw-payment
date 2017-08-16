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
public class PwdFrozenException extends Exception {

	private long time;

	public PwdFrozenException(long time) {
		this.time = time/1000;
	}

	public long getTime() {
		return time;
	}

	@Override
	public String getMessage() {
		return "支付密码已被冻结，请" + getPwdFrozenTime() + "后重试";
	}

	/**
	 * 获取密码的冻结时间
	 * 
	 * @param
	 * @return
	 */
	public String getPwdFrozenTime() {
		Long hour = time / 3600;
		Long minute = (time % 3600) / 60;
		return hour + "小时" + minute + "分";
	}
}
