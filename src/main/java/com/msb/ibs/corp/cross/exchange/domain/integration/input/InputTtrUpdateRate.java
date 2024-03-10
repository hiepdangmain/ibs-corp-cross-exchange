package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import java.util.List;

public class InputTtrUpdateRate {
    private String tranSn;
    private String rateType;
    private String rateDealId;
    private String rateDeal;
    private String paymentFrom;
    private String amountConvert;
    private String bnfcSwiftCode;
    private String bnfcBankName;
    private String bnfcBankAddr;
    private String receiverName;
    private String receiverAddr;
    private String bnfcAcctNo;
    private String amountInWord;
    private String receiverType;
    private String transportType;
    private String rolloutAcctNo;
    private Integer waitingApprove = 1;
    private String bnfcName;
    private String remark;
    private String saveBenefit = "0";
    private List<TtrDocument> documents;
    private List<String> notificationTo;
    private List<String> notificationType;


    public String getPaymentFrom() {
        return paymentFrom;
    }
    public void setPaymentFrom(String paymentFrom) {
        this.paymentFrom = paymentFrom;
    }
    public String getRateType() {
        return rateType;
    }
    public void setRateType(String rateType) {
        this.rateType = rateType;
    }
    public String getRateDeal() {
        return rateDeal;
    }
    public void setRateDeal(String rateDeal) {
        this.rateDeal = rateDeal;
    }
    public String getRateDealId() {
        return rateDealId;
    }
    public void setRateDealId(String rateDealId) {
        this.rateDealId = rateDealId;
    }
    public String getAmountConvert() {
        return amountConvert;
    }
    public void setAmountConvert(String amountConvert) {
        this.amountConvert = amountConvert;
    }
    public String getTranSn() {
        return tranSn;
    }
    public void setTranSn(String tranSn) {
        this.tranSn = tranSn;
    }
    public Integer getWaitingApprove() {
        return waitingApprove;
    }
    public void setWaitingApprove(Integer waitingApprove) {
        this.waitingApprove = waitingApprove;
    }
    public String getRolloutAcctNo() {
        return rolloutAcctNo;
    }
    public void setRolloutAcctNo(String rolloutAcctNo) {
        this.rolloutAcctNo = rolloutAcctNo;
    }
    public String getBnfcName() {
        return bnfcName;
    }
    public void setBnfcName(String bnfcName) {
        this.bnfcName = bnfcName;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getSaveBenefit() {
        return saveBenefit;
    }
    public void setSaveBenefit(String saveBenefit) {
        this.saveBenefit = saveBenefit;
    }
    public List<TtrDocument> getDocuments() {
        return documents;
    }
    public void setDocuments(List<TtrDocument> documents) {
        this.documents = documents;
    }
    public List<String> getNotificationTo() {
        return notificationTo;
    }
    public void setNotificationTo(List<String> notificationTo) {
        this.notificationTo = notificationTo;
    }
    public List<String> getNotificationType() {
        return notificationType;
    }
    public void setNotificationType(List<String> notificationType) {
        this.notificationType = notificationType;
    }
    public String getBnfcSwiftCode() {
        return bnfcSwiftCode;
    }
    public void setBnfcSwiftCode(String bnfcSwiftCode) {
        this.bnfcSwiftCode = bnfcSwiftCode;
    }
    public String getBnfcBankName() {
        return bnfcBankName;
    }
    public void setBnfcBankName(String bnfcBankName) {
        this.bnfcBankName = bnfcBankName;
    }
    public String getBnfcBankAddr() {
        return bnfcBankAddr;
    }
    public void setBnfcBankAddr(String bnfcBankAddr) {
        this.bnfcBankAddr = bnfcBankAddr;
    }
    public String getReceiverName() {
        return receiverName;
    }
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
    public String getReceiverAddr() {
        return receiverAddr;
    }
    public void setReceiverAddr(String receiverAddr) {
        this.receiverAddr = receiverAddr;
    }
    public String getBnfcAcctNo() {
        return bnfcAcctNo;
    }
    public void setBnfcAcctNo(String bnfcAcctNo) {
        this.bnfcAcctNo = bnfcAcctNo;
    }
    public String getReceiverType() {
        return receiverType;
    }
    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }
    public String getTransportType() {
        return transportType;
    }
    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
    public String getAmountInWord() {
        return amountInWord;
    }
    public void setAmountInWord(String amountInWord) {
        this.amountInWord = amountInWord;
    }
}
