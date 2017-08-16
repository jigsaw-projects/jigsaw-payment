/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.para.valid;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月16日
 */
@SuppressWarnings("serial")
public class ParamErrorexception extends RuntimeException {
	private String para;

	public ParamErrorexception(String para, String message) {
		super(message);
		this.para = para;
	}

	public String getPara() {
		return para;
	}
}
