package org.jigsaw.payment.fastpay;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * 绑卡申请
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月16日
 */
public class ContractRequest{

    private String transTime;

	@NotEmpty
    private String userId;
	//卡号
    @NotEmpty
    private String cardNum;

    //卡类型，信用卡或者借记卡
    private String cardType;

    //信用卡有效期
    private String cardValidity;

    //信用卡校验码
    private String cardCvv2;

    //预留手机号
    private String cardMobile;

    //卡主姓名
    private String cardOwnerName;

    //卡号
    private String certNum;

    //短信验证码的Key
    private String smsKey;

    //短信验证码
    private String smsCode;

    //关联平台
    private String platform;

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCardNum() {
		return cardNum;
	}

	public void setCardNum(String cardNum) {
		this.cardNum = cardNum;
	}

	public String getCardType() {
		return cardType;
	}

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	public String getCardValidity() {
		return cardValidity;
	}

	public void setCardValidity(String cardValidity) {
		this.cardValidity = cardValidity;
	}

	public String getCardCvv2() {
		return cardCvv2;
	}

	public void setCardCvv2(String cardCvv2) {
		this.cardCvv2 = cardCvv2;
	}

	public String getCardMobile() {
		return cardMobile;
	}

	public void setCardMobile(String cardMobile) {
		this.cardMobile = cardMobile;
	}

	public String getCertNum() {
		return certNum;
	}

	public void setCertNum(String certNum) {
		this.certNum = certNum;
	}

	public String getSmsKey() {
		return smsKey;
	}

	public void setSmsKey(String smsKey) {
		this.smsKey = smsKey;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getCardOwnerName() {
		return cardOwnerName;
	}

	public void setCardOwnerName(String cardOwnerName) {
		this.cardOwnerName = cardOwnerName;
	}

 
}
