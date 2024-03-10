package com.msb.ibs.corp.cross.exchange.domain.integration.output;

public class OutputTtrDeal {
    private String rateDeal;
    private String amount;
    private String amountConvert;
    private String amountInWord;
    private String fee;
    public String getRateDeal() {
        return rateDeal;
    }
    public void setRateDeal(String rateDeal) {
        this.rateDeal = rateDeal;
    }
    public String getAmountConvert() {
        return amountConvert;
    }
    public void setAmountConvert(String amountConvert) {
        this.amountConvert = amountConvert;
    }
    public String getAmountInWord() {
        return amountInWord;
    }
    public void setAmountInWord(String amountInWord) {
        this.amountInWord = amountInWord;
    }
    public String getFee() {
        return fee;
    }
    public void setFee(String fee) {
        this.fee = fee;
    }
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

}
