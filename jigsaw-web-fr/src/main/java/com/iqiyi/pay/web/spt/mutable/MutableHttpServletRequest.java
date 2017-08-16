/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.web.spt.mutable;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月8日
 */
public class MutableHttpServletRequest extends HttpServletRequestWrapper {
	
	private Map<String,String[]> parameters = new HashMap<String,String[]>();

	public MutableHttpServletRequest(HttpServletRequest request) {
		super(request);
		this.parameters.putAll(request.getParameterMap());
	}
	

	public void setParameter(String name, String value) {
		parameters.put(name, new String[] {value});
	}

	@Override
	public String getParameter(String name) {
		if (parameters.get(name) != null) {
			return parameters.get(name)[0];
		}
		return null;
	}

	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> result = new HashMap<String, String[]>();
		result.putAll(parameters);
		return Collections.<String, String[]>unmodifiableMap(result);
	}

	public Enumeration<String> getParameterNames() {
		Set<String> result = new HashSet<String>();
		result.addAll(parameters.keySet());
		return new Vector<String>(result).elements();
	}

	public String[] getParameterValues(String name) {
		if (parameters.get(name) != null) {
			return parameters.get(name);
		}
		return null;
	}
	
	public void deleteParameter(String key) {
		this.parameters.remove(key);
	}

}
