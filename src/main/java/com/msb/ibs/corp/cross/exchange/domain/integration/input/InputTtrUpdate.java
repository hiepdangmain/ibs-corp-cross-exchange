package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import java.math.BigDecimal;
import java.util.List;

public class InputTtrUpdate {

    private String rolloutAcctNo;
    private String tranSn;
    private String bnfcSwiftCode;
    private String bnfcAcctNo;
    private String bnfcCountry;
    private String bnfcBankName;
    private String bnfcBankAddr;
    private String bnfcName;
    private String receiverName;
    private String receiverAddr;
    private String remark;
    private BigDecimal amount;
    private String currency;
    private String amountInWord;
    private String transportType;
    private String debitAcctNo;
    private String receiverType;
    private String saveBenefit = "0";
    private Integer waitingApprove = 1;
    private String imbSwiftNo;
    private String imbBankName;
    private String imbBankAddr;
    private String imbCountry;
    private String rolloutAcctName;
    private String rolloutAddress;
    private List<String> notificationTo;
    private List<String> notificationType;
    private List<TtrDocument> documents;

    public String getTranSn() {
        return tranSn;
    }
    public void setTranSn(String tranSn) {
        this.tranSn = tranSn;
    }
    public String getBnfcSwiftCode() {
        return bnfcSwiftCode;
    }
    public void setBnfcSwiftCode(String bnfcSwiftCode) {
        this.bnfcSwiftCode = bnfcSwiftCode;
    }
    public String getBnfcAcctNo() {
        return bnfcAcctNo;
    }
    public void setBnfcAcctNo(String bnfcAcctNo) {
        this.bnfcAcctNo = bnfcAcctNo;
    }
    public String getBnfcCountry() {
        return bnfcCountry;
    }
    public void setBnfcCountry(String bnfcCountry) {
        this.bnfcCountry = bnfcCountry;
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
    public String getBnfcName() {
        return bnfcName;
    }
    public void setBnfcName(String bnfcName) {
        this.bnfcName = bnfcName;
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
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getTransportType() {
        return transportType;
    }
    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }
    public List<TtrDocument> getDocuments() {
        return documents;
    }
    public void setDocuments(List<TtrDocument> documents) {
        this.documents = documents;
    }
    public String getDebitAcctNo() {
        return debitAcctNo;
    }
    public void setDebitAcctNo(String debitAcctNo) {
        this.debitAcctNo = debitAcctNo;
    }
    public String getRolloutAcctNo() {
        return rolloutAcctNo;
    }
    public void setRolloutAcctNo(String rolloutAcctNo) {
        this.rolloutAcctNo = rolloutAcctNo;
    }
    public String getReceiverType() {
        return receiverType;
    }
    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }
    public String getSaveBenefit() {
        return saveBenefit;
    }
    public void setSaveBenefit(String saveBenefit) {
        this.saveBenefit = saveBenefit;
    }
    public Integer getWaitingApprove() {
        return waitingApprove;
    }
    public void setWaitingApprove(Integer waitingApprove) {
        this.waitingApprove = waitingApprove;
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
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    public String getAmountInWord() {
        return amountInWord;
    }
    public void setAmountInWord(String amountInWord) {
        this.amountInWord = amountInWord;
    }
    public String getImbSwiftNo() {
        return imbSwiftNo;
    }
    public void setImbSwiftNo(String imbSwiftNo) {
        this.imbSwiftNo = imbSwiftNo;
    }
    public String getImbBankName() {
        return imbBankName;
    }
    public void setImbBankName(String imbBankName) {
        this.imbBankName = imbBankName;
    }
    public String getImbBankAddr() {
        return imbBankAddr;
    }
    public void setImbBankAddr(String imbBankAddr) {
        this.imbBankAddr = imbBankAddr;
    }
    public String getImbCountry() {
        return imbCountry;
    }
    public void setImbCountry(String imbCountry) {
        this.imbCountry = imbCountry;
    }
    public String getRolloutAcctName() {
        return rolloutAcctName;
    }
    public void setRolloutAcctName(String rolloutAcctName) {
        this.rolloutAcctName = rolloutAcctName;
    }
    public String getRolloutAddress() {
        return rolloutAddress;
    }
    public void setRolloutAddress(String rolloutAddress) {
        this.rolloutAddress = rolloutAddress;
    }
}
