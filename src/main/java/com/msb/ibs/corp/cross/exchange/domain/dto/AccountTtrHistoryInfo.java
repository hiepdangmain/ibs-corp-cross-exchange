package com.msb.ibs.corp.cross.exchange.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

public class AccountTtrHistoryInfo {
    private Date time;
    private Integer userId;
    private String wfProcessId;
    private String transType;
    private String tranSn;
    private BigDecimal preBalance;

    private String rmCode;
    private BigDecimal amount;
    private String currency;
    private String rolloutAcctNo;
    private String rolloutAcctName;
    private String taxCode;
    private String certCode;

    private String bnfcSwiftCode;
    private String receiverName;
    private String bnfcAcctNo;
    private String paidFeeSource;
    private String recordStatus;
    private String status;
    private String rejectContent;
    private String dcSign;
    private String amountConvert;
    private String rateType;
    private String rateDeal;
    private String nameChecker;
    private String nameMaker;
    private BigDecimal fee;
    private String amountInWord;
    private String imbSwiftNo;


    private String remark;
    private String username;
    private String bnfcName;
    private String bnfcBankName;
    private String partnerEmail;
    private String coreSn;

    private String info1;
    private String info2;
    private String debitAccountNo;
    private BigDecimal feeForeignCurrency;
    private BigDecimal rateFee;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getWfProcessId() {
        return wfProcessId;
    }

    public void setWfProcessId(String wfProcessId) {
        this.wfProcessId = wfProcessId;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTranSn() {
        return tranSn;
    }

    public void setTranSn(String tranSn) {
        this.tranSn = tranSn;
    }

    public String getRmCode() {
        return rmCode;
    }

    public void setRmCode(String rmCode) {
        this.rmCode = rmCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getRolloutAcctNo() {
        return rolloutAcctNo;
    }

    public void setRolloutAcctNo(String rolloutAcctNo) {
        this.rolloutAcctNo = rolloutAcctNo;
    }

    public String getRolloutAcctName() {
        return rolloutAcctName;
    }

    public void setRolloutAcctName(String rolloutAcctName) {
        this.rolloutAcctName = rolloutAcctName;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getCertCode() {
        return certCode;
    }

    public void setCertCode(String certCode) {
        this.certCode = certCode;
    }

    public String getBnfcSwiftCode() {
        return bnfcSwiftCode;
    }

    public void setBnfcSwiftCode(String bnfcSwiftCode) {
        this.bnfcSwiftCode = bnfcSwiftCode;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getBnfcAcctNo() {
        return bnfcAcctNo;
    }

    public void setBnfcAcctNo(String bnfcAcctNo) {
        this.bnfcAcctNo = bnfcAcctNo;
    }

    public String getPaidFeeSource() {
        return paidFeeSource;
    }

    public void setPaidFeeSource(String paidFeeSource) {
        this.paidFeeSource = paidFeeSource;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectContent() {
        return rejectContent;
    }

    public void setRejectContent(String rejectContent) {
        this.rejectContent = rejectContent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBnfcName() {
        return bnfcName;
    }

    public void setBnfcName(String bnfcName) {
        this.bnfcName = bnfcName;
    }

    public String getBnfcBankName() {
        return bnfcBankName;
    }

    public void setBnfcBankName(String bnfcBankName) {
        this.bnfcBankName = bnfcBankName;
    }

    public String getPartnerEmail() {
        return partnerEmail;
    }

    public void setPartnerEmail(String partnerEmail) {
        this.partnerEmail = partnerEmail;
    }

    public String getInfo1() {
        return info1;
    }

    public void setInfo1(String info1) {
        this.info1 = info1;
    }

    public String getInfo2() {
        return info2;
    }

    public void setInfo2(String info2) {
        this.info2 = info2;
    }

    public BigDecimal getPreBalance() {
        return preBalance;
    }

    public void setPreBalance(BigDecimal preBalance) {
        this.preBalance = preBalance;
    }

    public String getDcSign() {
        return dcSign;
    }

    public void setDcSign(String dcSign) {
        this.dcSign = dcSign;
    }

    public String getCoreSn() {
        return coreSn;
    }

    public void setCoreSn(String coreSn) {
        this.coreSn = coreSn;
    }

    public String getAmountConvert() {
        return amountConvert;
    }

    public void setAmountConvert(String amountConvert) {
        this.amountConvert = amountConvert;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getRateDeal() {
        return rateDeal;
    }

    public void setRateDeal(String rateDeal) {
        this.rateDeal = rateDeal;
    }

    public String getNameChecker() {
        return nameChecker;
    }

    public void setNameChecker(String nameChecker) {
        this.nameChecker = nameChecker;
    }

    public String getNameMaker() {
        return nameMaker;
    }

    public void setNameMaker(String nameMaker) {
        this.nameMaker = nameMaker;
    }

    public String getDebitAccountNo() {
        return debitAccountNo;
    }

    public void setDebitAccountNo(String debitAccountNo) {
        this.debitAccountNo = debitAccountNo;
    }

    public BigDecimal getFeeForeignCurrency() {
        return feeForeignCurrency;
    }

    public void setFeeForeignCurrency(BigDecimal feeForeignCurrency) {
        this.feeForeignCurrency = feeForeignCurrency;
    }

    public BigDecimal getRateFee() {
        return rateFee;
    }

    public void setRateFee(BigDecimal rateFee) {
        this.rateFee = rateFee;
    }
}
