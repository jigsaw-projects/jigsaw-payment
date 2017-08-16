package com.iqiyi.pay.frontend.service.bank;

import com.iqiyi.pay.frontend.aspect.Trace;
import com.iqiyi.pay.frontend.service.id.IdService;
import com.iqiyi.pay.frontend.service.order.PayTradeService;
import com.iqiyi.pay.sdk.BankCardCertification;
import com.iqiyi.pay.sdk.CardBankMap;
import com.iqiyi.pay.sdk.PayUserCard;
import com.iqiyi.pay.sdk.service.AccountService.AddBankCardCertificationRequest;
import com.iqiyi.pay.sdk.service.AccountService.AddBankCardCertificationResponse;
import com.iqiyi.pay.sdk.service.RouteService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by leishengbao on 8/26/16.
 */
@Component
public class CardBindManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(CardBindManager.class);

    @Autowired
    com.iqiyi.pay.client.RouteService routeService;
    @Autowired
    com.iqiyi.pay.client.AccountService accountService;
    @Autowired
    com.iqiyi.pay.client.ConfigService configService;
    @Autowired
    BankCardInfoService bankCardInfoService;
    @Autowired
    IdService idService;
    @Autowired
    PayTradeService payTradeService;

    /**
     * 保存四要素，以及卡的签约关系
     * @param orderCode
     * @param contractId
     * @param userId
     * @param userName
     * @param cardNum
     * @param cardMobile
     * @param certType
     * @param certNum
     * @param serviceCode
     * @return
     */
    @Trace
    public Long  saveCardInfoAndContract(String orderCode,
                                           String contractId,
                                           String userId,
                                           String userName,
                                           String cardNum,
                                           String cardMobile,
                                           String certType,
                                           String certNum,
                                           String serviceCode){
        long start = System.currentTimeMillis();
        LOGGER.debug("[orderCode:{}][userId:{}][userName:{}]", orderCode, userId, userName);

        BankCardCertification bankCard = bankCardInfoService.queryBankCertifycation(userId, cardNum);
        if (bankCard != null){
            updatePayCardByCardId(orderCode, cardNum, userId, contractId, serviceCode);
            payTradeService.updatePayTradeExtend(bankCard.getId(), orderCode, userId);
            //保存实名信息
            idService.writeCertification(Long.parseLong(userId), userName, certNum);
            return bankCard.getId();
        }


        CardBankMap cardBankMap = bankCardInfoService.queryBankInfo(cardNum);
        BankCardCertification bc = BankCardCertification.newBuilder()
                .setBankCode(cardBankMap.getBankCode())
                .setBankName(cardBankMap.getBankName())
                .setBindPhone(cardMobile)
                .setCardNumber(cardNum)
                .setCardType(cardBankMap.getCardType())
                .setOwnerName(userName)
                .setOwnerIdNum(certNum)
                .setStatus(1)
                .setUserId(Long.parseLong(userId))
                .setCreateTime(System.currentTimeMillis()).build();
        AddBankCardCertificationRequest request = AddBankCardCertificationRequest.newBuilder().setCertification(bc).build();
        AddBankCardCertificationResponse response = accountService.addBankCardCertification(request);


        long cardId = response.getId();
        PayUserCard.Builder payUserCard = PayUserCard.newBuilder()
                .setCardNumLast(cardNum.substring(cardNum.length() - 4))
                .setCardType(cardBankMap.getCardType())
                .setPayType(serviceCode)
                .setUserId(Long.parseLong(userId))
                .setBankCode(cardBankMap.getBankCode())
                .setCreateTime(System.currentTimeMillis())
                .setId(cardId)
                .setMobile(cardMobile)
                .setToken(contractId)
                .setUpdateTime(System.currentTimeMillis()).setStatus(1).setVersion(0);
        if (orderCode != null){
            payUserCard.setOrderCode(orderCode);
        }
        RouteService.CreatePayCardRequest createPayCardRequest = RouteService.CreatePayCardRequest.newBuilder().setPayUserCard(payUserCard).build();
        routeService.createPayCard(createPayCardRequest);
        payTradeService.updatePayTradeExtend(response.getId(), orderCode, userId);
        //保存实名信息
        idService.writeCertification(Long.parseLong(userId), userName, certNum);
        LOGGER.debug("[userId:{}][time:{}]", userId, (System.currentTimeMillis() - start));
        return response.getId();
    }


    public String queryUserContractId(String uid, String cardNum, String orderCode, String serviceCode){
        String contractId = bankCardInfoService.queryCardTokenByCardNum(cardNum, serviceCode);
        return contractId;
    }


    /**
     * 删除签约关系
     * 1/先更新status = 2
     * 2/再查询是否还有该卡的签约关系
     * @param userId
     * @param cardNum
     * @param paytype
     * @return 有签约：false，没签约 : true
     */
    @Trace
    public boolean  removePayCardContract(long cardId, String userId, String cardNum, String paytype){
        PayUserCard payUserCard = PayUserCard.newBuilder().setStatus(2).build();
        RouteService.UpdatePayCardByUserIdAndCardNumRequest request = RouteService.UpdatePayCardByUserIdAndCardNumRequest.newBuilder()
                .setCardId(cardId).setUserId(Long.parseLong(userId)).setPayCard(payUserCard).build();
        RouteService.UpdatePayCardByUserIdAndCardNumResponse response = routeService.updatePayCardByUserIdAndCardNum(request);
        String token = bankCardInfoService.queryCardTokenByCardNum(cardNum, paytype);
        LOGGER.debug("[cardId:{}][update:{}][token:{}]", cardId, response.hasEffect(), token);
        return StringUtils.isBlank(token);
    }



    public boolean removePayCardByCardId(long cardId, String paytype){
        BankCardCertification bcc = bankCardInfoService.queryBankCertificationByCardId(cardId);
        long userId = bcc.getUserId();
        String cardNum = bcc.getCardNumber();
        String token = bankCardInfoService.queryCardTokenByCardNum(cardNum, paytype);
        return StringUtils.isBlank(token);
    }

    /**
     * 更新卡的签约号
     * @param orderCode
     * @param cardNum
     * @param userId
     * @param contractId
     * @param serviceCode
     */
    private void updatePayCardByCardId(String orderCode, String cardNum, String userId, String contractId, String serviceCode){
        PayUserCard payUserCard = PayUserCard.newBuilder()
                .setStatus(1)
                .setOrderCode(orderCode)
                .setPayType(serviceCode)
                .setToken(contractId).build();
        RouteService.UpdatePayCardByUserIdAndCardNumRequest request = RouteService.UpdatePayCardByUserIdAndCardNumRequest.newBuilder()
                .setCardNum(cardNum).setUserId(Long.parseLong(userId)).setPayCard(payUserCard).build();
        routeService.updatePayCardByUserIdAndCardNum(request);
    }



/*    public void saveBankCardContract(BankCardContract bankCardContract){
        SaveBankCardContractRequest request = SaveBankCardContractRequest.newBuilder()
                .setBankCardContract(bankCardContract).build();

//        accountService

    }*/

}
