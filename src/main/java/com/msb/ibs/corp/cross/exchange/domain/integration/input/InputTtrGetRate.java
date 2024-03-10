package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import java.math.BigDecimal;

public class InputTtrGetRate {

    private String rolloutAcctNo;
    private BigDecimal amount;
    private String currency;
    private BigDecimal rateTransaction;
    private String rateType;
    private String typeTransfer;

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
    public String getRateType() {
        return rateType;
    }
    public void setRateType(String rateType) {
        this.rateType = rateType;
    }
    public String getRolloutAcctNo() {
        return rolloutAcctNo;
    }
    public void setRolloutAcctNo(String rolloutAcctNo) {
        this.rolloutAcctNo = rolloutAcctNo;
    }
    public BigDecimal getRateTransaction() {
        return rateTransaction;
    }
    public void setRateTransaction(BigDecimal rateTransaction) {
        this.rateTransaction = rateTransaction;
    }
    public String getTypeTransfer() {
        return typeTransfer;
    }
    public void setTypeTransfer(String typeTransfer) {
        this.typeTransfer = typeTransfer;
    }
}
