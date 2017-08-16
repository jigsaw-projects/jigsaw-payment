/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.risk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年8月3日
 */
public class RiskAssessmentHandler {
	 
    public static final String CODE = "code";
    public static final String RESULT = "result";
    public static final String REQUEST_ID = "request_id";
    public static final String RESULT_PARAMS = "result_params";
    public static final String RISK_ASSESSMENT = "risk_assessment";
    public static final String LEVEL = "level";
    public static final String REASON = "reason";
    public static final String FIRED_RULES = "fired_rules";
 
    public static RiskAssessment handle(String result) {
        RiskAssessment riskAssessment = new RiskAssessment();
        JSONObject object = JSON.parseObject(result);
 
        Integer code = object.getInteger(CODE);
        if (!code.equals(0)) {
            return riskAssessment;
        }
 
        JSONObject jsonResult = object.getJSONObject(RESULT);
        if (jsonResult == null) {
            return riskAssessment;
        }
        riskAssessment.setRequestId(jsonResult.getString(REQUEST_ID));
        riskAssessment.setFiredRules(jsonResult.getJSONArray(FIRED_RULES));
 
        JSONObject resultParams = jsonResult.getJSONObject(RESULT_PARAMS);
        JSONObject assessment = resultParams.getJSONObject(RISK_ASSESSMENT);
 
        Integer level = assessment.getInteger(LEVEL);
        riskAssessment.setLevel(level == null ? 0 : level);
        riskAssessment.setReason(assessment.getString(REASON));
        return riskAssessment;
    }
}
