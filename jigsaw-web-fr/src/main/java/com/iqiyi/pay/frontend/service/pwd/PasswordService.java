/*
 * *****************************************************
 * Copyright (C) 2016 iQIYI.COM - All Rights Reserved
 * This file is part of iQiyi Pay project.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 * ****************************************************
 */
package com.iqiyi.pay.frontend.service.pwd;

import com.iqiyi.pay.client.EntityService;
import com.iqiyi.pay.frontend.exception.PwdErrorException;
import com.iqiyi.pay.frontend.exception.PwdFrozenException;
import com.iqiyi.pay.frontend.exception.PwdNotExistException;
import com.iqiyi.pay.sdk.UserPwd;
import com.iqiyi.pay.sdk.service.EntityService.UserPwdCheckRequest;
import com.iqiyi.pay.sdk.service.EntityService.UserPwdCheckResponse;
import com.iqiyi.pay.sdk.service.EntityService.UserPwdQueryByUidRequest;
import com.iqiyi.pay.sdk.service.EntityService.UserPwdQueryByUidResponse;
import com.iqiyi.security.whitebox.CryptoToolbox;
import ocx.AESWithJCE;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zhu jinxian <zhujinxian@qiyi.com>
 *
 * @date 2016年7月19日
 */
@Service
public class PasswordService {
	
	private Logger logger = LoggerFactory.getLogger(PasswordService.class);
	
	@Autowired
	private EntityService service; 
	
	@Value("${max.pwd.err.count:3}")
	private int maxPWdErrCount;

	@Value("${pwd.decrypt.key}")
	private String key;

	@Value("${secret.pc.platform}")
	private String platform;

	@Value("${secret.redis.pre}")
	private String pre;

	@Value("${secret.redis.expiretime}")
	private String expiretime;

	@Resource(name = "stringRedisTemplate")
	RedisTemplate stringRedisTemplate;

	@Value("${pwd.notExist.tip}")
	private String pwdNotExitsTip;

	public void checkPassword(long userId, String pwd, String platform) throws PwdFrozenException, PwdErrorException, PwdNotExistException {
		String clearPwd = pwd;

		if ("PCW".equals(platform)){
			clearPwd = decodePwd(pwd);
		}

		if (this.platform.equals(platform)){
			clearPwd = decodePwd(userId,pwd);
		}


		UserPwd.Builder userPwd = UserPwd.newBuilder();
		userPwd.setUserId(userId);
		userPwd.setPassword(clearPwd);
		UserPwdCheckRequest.Builder request = UserPwdCheckRequest.newBuilder();
		request.setUser(userPwd);
		UserPwdCheckResponse response = service.checkUserPwd(request.build());
		if (!response.hasUser()) {
			throw new PwdNotExistException(pwdNotExitsTip);
		}
		UserPwd user = response.getUser();
		PwdStatus status = PwdStatus.from(user.getStatus());
		if (status == PwdStatus.VALID) {
			if (response.getRet()) {
				return ;
			} 
			throw new PwdErrorException(maxPWdErrCount-user.getErrorCount());
		} else if (status == PwdStatus.FROZEN) {
			throw new PwdFrozenException(user.getEndFrozenTime());
		}
	}
	
	
	public boolean isPasswordSet(long userId) {
		UserPwdQueryByUidRequest.Builder request = UserPwdQueryByUidRequest.newBuilder();
		request.setUid(userId);
		UserPwdQueryByUidResponse response = service.queryUserPwdByUid(request.build());
		
		return response.hasUser();
	
	}


	private String decodePwd(Long userId,String content){
		content = content.substring(1, content.length()-1);
		String value = (String) stringRedisTemplate.opsForValue().get(pre+userId);
			if(StringUtils.isBlank(value)){
			logger.warn("redis get key is null :{}",pre+userId);
		}
		content = AESWithJCE.getResult(value,content);
		return content;
	}

	private String decodePwd(String content){
		content = content.substring(1, content.length()-1);
		String[] chars = content.split(",");
		StringBuffer sb = new StringBuffer();
		for (String c : chars) {
			c = CryptoToolbox.aesDecryptData(c, key);
			sb.append(c.split("#")[0]);
		}
		return sb.toString();
	}
}
