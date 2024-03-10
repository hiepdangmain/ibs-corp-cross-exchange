package com.msb.ibs.corp.cross.exchange.domain.integration.output;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbTtrHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BkSwiftInfo;
import com.msb.ibs.corp.cross.exchange.domain.integration.input.TtrDocument;

import java.math.BigDecimal;
import java.util.List;

public class OutputTtrHistory extends BbTtrHistory {

    private List<TtrDocument> documents;
    private String certCode;
    private String taxCode;
    private String itemDisplay;
    private String tradeFeeDisplay;
    private String purposesPaymentDisplay;
    private String purposesPaymentEngDisplay;
    private List<OutputBkSysParam> paymentFroms;
    private List<RateInfo> rates;
    private List<BkSwiftInfo> swifts;
    private List<OutputBkSysParam> transportTypes;
    private List<Integer> listWaitingApprove;
    private String paidFeeSourceDisplay;
    private String transactionTypeDisplay;
    private String paymentFromDisplay;
    private BigDecimal ivnFee;
    private List<String> notificationTo;
    private String maker;
    private String checker;
    private BigDecimal feeForeignCurrency;
    private String currencyAccountDebit;

    public List<TtrDocument> getDocuments() {
        return documents;
    }


    public void setDocuments(List<TtrDocument> documents) {
        this.documents = documents;
    }


    public String getCertCode() {
        return certCode;
    }

    public String getItemDisplay() {
        return itemDisplay;
    }


    public void setItemDisplay(String itemDisplay) {
        this.itemDisplay = itemDisplay;
    }


    public String getTradeFeeDisplay() {
        return tradeFeeDisplay;
    }


    public void setTradeFeeDisplay(String tradeFeeDisplay) {
        this.tradeFeeDisplay = tradeFeeDisplay;
    }


    public void setCertCode(String certCode) {
        this.certCode = certCode;
    }


    public String getTaxCode() {
        return taxCode;
    }


    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }


    public String getPurposesPaymentDisplay() {
        return purposesPaymentDisplay;
    }


    public void setPurposesPaymentDisplay(String purposesPaymentDisplay) {
        this.purposesPaymentDisplay = purposesPaymentDisplay;
    }


    public List<OutputBkSysParam> getPaymentFroms() {
        return paymentFroms;
    }


    public void setPaymentFroms(List<OutputBkSysParam> paymentFroms) {
        this.paymentFroms = paymentFroms;
    }


    public List<RateInfo> getRates() {
        return rates;
    }


    public void setRates(List<RateInfo> rates) {
        this.rates = rates;
    }


    public List<BkSwiftInfo> getSwifts() {
        return swifts;
    }


    public void setSwifts(List<BkSwiftInfo> swifts) {
        this.swifts = swifts;
    }


    public List<OutputBkSysParam> getTransportTypes() {
        return transportTypes;
    }


    public void setTransportTypes(List<OutputBkSysParam> transportTypes) {
        this.transportTypes = transportTypes;
    }


    public List<Integer> getListWaitingApprove() {
        return listWaitingApprove;
    }


    public void setListWaitingApprove(List<Integer> listWaitingApprove) {
        this.listWaitingApprove = listWaitingApprove;
    }


    public String getPaidFeeSourceDisplay() {
        return paidFeeSourceDisplay;
    }


    public void setPaidFeeSourceDisplay(String paidFeeSourceDisplay) {
        this.paidFeeSourceDisplay = paidFeeSourceDisplay;
    }


    public String getTransactionTypeDisplay() {
        return transactionTypeDisplay;
    }


    public void setTransactionTypeDisplay(String transactionTypeDisplay) {
        this.transactionTypeDisplay = transactionTypeDisplay;
    }


    public String getPaymentFromDisplay() {
        return paymentFromDisplay;
    }


    public void setPaymentFromDisplay(String paymentFromDisplay) {
        this.paymentFromDisplay = paymentFromDisplay;
    }


    public BigDecimal getIvnFee() {
        return ivnFee;
    }


    public void setIvnFee(BigDecimal ivnFee) {
        this.ivnFee = ivnFee;
    }


    public List<String> getNotificationTo() {
        return notificationTo;
    }


    public void setNotificationTo(List<String> notificationTo) {
        this.notificationTo = notificationTo;
    }


    public String getMaker() {
        return maker;
    }


    public void setMaker(String maker) {
        this.maker = maker;
    }


    public String getChecker() {
        return checker;
    }


    public void setChecker(String checker) {
        this.checker = checker;
    }


    public BigDecimal getFeeForeignCurrency() {
        return feeForeignCurrency;
    }


    public void setFeeForeignCurrency(BigDecimal feeForeignCurrency) {
        this.feeForeignCurrency = feeForeignCurrency;
    }


    public String getCurrencyAccountDebit() {
        return currencyAccountDebit;
    }


    public void setCurrencyAccountDebit(String currencyAccountDebit) {
        this.currencyAccountDebit = currencyAccountDebit;
    }

    public String getPurposesPaymentEngDisplay() {
        return purposesPaymentEngDisplay;
    }

    public void setPurposesPaymentEngDisplay(String purposesPaymentEngDisplay) {
        this.purposesPaymentEngDisplay = purposesPaymentEngDisplay;
    }
}
