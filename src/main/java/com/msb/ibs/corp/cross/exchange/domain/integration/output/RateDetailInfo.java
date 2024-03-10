package com.msb.ibs.corp.cross.exchange.domain.integration.output;

public class RateDetailInfo {
    private String rateId;
    private String spotRate;
    private String currency;
    private String amount;
    private String amountDeal;
    private String orgNo;
    private String rateMsb;

    public String getRateId() {
        return rateId;
    }
    public void setRateId(String rateId) {
        this.rateId = rateId;
    }
    public String getSpotRate() {
        return spotRate;
    }
    public void setSpotRate(String spotRate) {
        this.spotRate = spotRate;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmountDeal() {
        return amountDeal;
    }
    public void setAmountDeal(String amountDeal) {
        this.amountDeal = amountDeal;
    }

    public String getOrgNo() {
        return orgNo;
    }
    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }
    public String getRateMsb() {
        return rateMsb;
    }
    public void setRateMsb(String rateMsb) {
        this.rateMsb = rateMsb;
    }

    public RateDetailInfo(String rateId, String spotRate, String currency) {
        super();
        this.rateId = rateId;
        this.spotRate = spotRate;
        this.currency = currency;
    }
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public RateDetailInfo(String rateId, String spotRate, String currency, String amount) {
        super();
        this.rateId = rateId;
        this.spotRate = spotRate;
        this.currency = currency;
        this.amount = amount;
    }
    public RateDetailInfo(String rateId, String spotRate, String currency, String amount, String chiNhanh) {
        super();
        this.rateId = rateId;
        this.spotRate = spotRate;
        this.currency = currency;
        this.amount = amount;
        this.orgNo = chiNhanh;
    }
}
