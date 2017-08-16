package com.iqiyi.pay.frontend.service.contract;

import com.iqiyi.pay.frontend.service.bank.CardBindManager;
import com.iqiyi.pay.frontend.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by leishengbao on 12/22/16.
 * 处理银行卡多次签约的逻辑
 */
@Component
public class MultiChannelContractService {

    @Value("${pay.channel.url}")
    private String bankChannelUrl;

    @Autowired
    private CardBindManager cardBindManager;



    public void cardBindPost(long cardId, String channelCode, Map<String, String> cardInfo){
        if (channelCode.equals(Constants.BAIDU_API_CODE)){
            unionContract(cardId, cardInfo);
        }
    }



    public void unionContract(long cardId, Map<String, String> cardInfo){
        String unionCode = "unionpay";
        String unionUrl = bankChannelUrl+"/"+unionCode;




/*        String contractId = "";
        String userId = cardInfo.get("user_id");
        BankCardContract cardContract = BankCardContract.newBuilder()
                .setCardId(cardId)
                .setUserId(Long.parseLong(userId))
                .setChannelCode(unionCode)
                .setToken(contractId)
                .setStatus(1)
                .build();
        cardBindManager.saveBankCardContract(cardContract);*/
    }




}
