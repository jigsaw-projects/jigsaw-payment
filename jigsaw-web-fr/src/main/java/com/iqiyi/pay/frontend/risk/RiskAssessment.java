/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.risk;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月3日
 */
public class RiskAssessment {
	private int level;
	private String reason;
	private String requestId;
	private List<Object> firedRules;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public List<Object> getFiredRules() {
		return firedRules;
	}

	public void setFiredRules(List<Object> firedRules) {
		this.firedRules = firedRules;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("level", level)
				.append("reason", reason).append("requestId", requestId)
				.append("firedRules", firedRules).toString();
	}
}