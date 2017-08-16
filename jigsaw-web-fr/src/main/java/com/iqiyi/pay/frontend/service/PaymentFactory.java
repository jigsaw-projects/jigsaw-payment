package com.iqiyi.pay.frontend.service;

import com.iqiyi.pay.frontend.param.PayFrontendParam;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by leishengbao on 7/19/16.
 */
@Component
public class PaymentFactory implements ApplicationContextAware{

    private ApplicationContext ac;

    public static final String[] JSON_PAY_TYPES = new String[]{"CARDPAY", "ALIPAYGLOBAL", "CARDDUTPAY", "PAY2GO"};


    public static final List<String> THIRD_CHANNEL = Arrays.asList("ALIPAYGLOBAL", "PAY2GO", "UNIONPAYSDK");
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }


    public Payment getPayment(String paymentCode){
        if (paymentCode.equals("CARDPAY")){
            return ac.getBean(CardPayPayment.class);
        }else if (THIRD_CHANNEL.contains(paymentCode)){
            return ac.getBean(ThirdChannelPayment.class);
        }else if (paymentCode.equals("CARDDUTPAY")){
            return ac.getBean(CardDutPayment.class);
        }else if (paymentCode.equals("CHANNELQUERY")){
            return ac.getBean(ChannelQueryPayment.class);
        }else if (paymentCode.equals("INNERREFUND")){
            return ac.getBean(InnerRefundPayment.class);
        }
        return ac.getBean(BankChannelPayment.class);
    }


    public Payment refundPayment(String paymentCode){
        if (THIRD_CHANNEL.contains(paymentCode)){
            return ac.getBean(ThirdChannelPayment.class);
        }
        return ac.getBean(BankChannelPayment.class);
    }

    public boolean returnJson(PayFrontendParam payFrontendParam){
        return payFrontendParam.getPay_type() == null || Arrays.asList(JSON_PAY_TYPES).contains(payFrontendParam.getPay_type());
    }
}
