package com.iqiyi.pay.web.spt.jsonp;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.iqiyi.pay.web.spt.mutable.MutableHttpServletResponse;

public class PcwebJsonpFilter extends OncePerRequestFilter {
	
	private Logger logger = LoggerFactory.getLogger(PcwebJsonpFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest)request;
		String callback = req.getParameter("callback");
		if (callback != null) {
			MutableHttpServletResponse jsonRes = (MutableHttpServletResponse) response;
			chain.doFilter(request, jsonRes);
			String content = jsonRes.getResponseData();
			//content = new StringBuilder().append(callback).append("(").append(content).append(")").toString();
			content = new StrBuilder().append("<script type=\"text/javascript\">document.domain=\"iqiyi.com\";" + callback + "(" + content+ ")</script>").toString();
			jsonRes.setResponseData(content);
			jsonRes.setContentType("text/html;charset=UTF-8");
		} else {
			chain.doFilter(request, response);
		}
		
	}
}
