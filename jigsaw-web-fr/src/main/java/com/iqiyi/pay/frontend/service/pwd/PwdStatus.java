/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.service.pwd;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月22日
 */
@SuppressWarnings("static-access")
public enum PwdStatus {
	
	VALID(1), FROZEN(2);
	
	private final int status;
	PwdStatus(int status) {
		this.status = status;
	}
	
	private static Map<Integer,PwdStatus> maps = new HashMap<Integer, PwdStatus>();
	static {
		for(PwdStatus s : PwdStatus.values()){
			maps.put(s.getStatus(), s);
		}
	}
	
	public int getStatus() {
		return status;
	}
	
	public static PwdStatus from(int val) {
		return maps.get(val);
	}

	
}
