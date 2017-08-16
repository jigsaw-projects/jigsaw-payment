package com.iqiyi.pay.frontend.service.passport;

import com.iqiyi.pay.sdk.UserInfo;
import com.iqiyi.pay.sdk.service.PassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by liuyanlong on 4/13/17.
 */
@Component
public class PassPortUserInfoService {

    @Autowired
    private com.iqiyi.pay.client.PassportService passportService;


    public UserInfo getUserInfoByUserId(String userId){
        PassportService.GetUserInfoRequest.Builder request = PassportService.GetUserInfoRequest.newBuilder();
        request.setUid(userId);
        PassportService.GetUserInfoResponse response =  passportService.getUserInfo(request.build());
        return response.getUserInfo();
    }
}
