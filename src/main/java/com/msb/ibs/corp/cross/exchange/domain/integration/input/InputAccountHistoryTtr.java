package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import java.math.BigDecimal;
import java.util.Date;

public class InputAccountHistoryTtr {
    private String historyType;
    private String coreSn;
    private String doc;
    private String acctNo;
    private String fromDate;
    private String toDate;// not today
    private BigDecimal fromAmount;
    private BigDecimal toAmount;
    private String channel;
    private String status;
    private Integer page = 1;
    private Integer pageSize = 10;
    private Date fromDateQuery;
    private Date toDateQuery;
    private String fieldName;
    private String orderBy;
    private Integer userId;
    private String transType;
    private String dcSign;
    private String rmNo;
    private String batchNo;
    private String serviceType;
    private String rateType;
    private String recordStatus;
    private String paymentFrom;

    public InputAccountHistoryTtr() {
        super();
    }

    public InputAccountHistoryTtr(String historyType, String coreSn, String acctNo, String doc, String fromDate, String toDate, BigDecimal fromAmount,
                                  BigDecimal toAmount, String channel, String status, String fieldName , String orderBy, Integer userId, String transType, String dcSign, String rmNo, Integer page, Integer pageSize, String batchNo) {
        super();
        this.historyType = historyType;
        this.coreSn = coreSn;
        this.doc = doc;
        this.acctNo = acctNo;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.fromAmount = fromAmount;
        this.toAmount = toAmount;
        this.channel = channel;
        this.status = status;
        this.fieldName= fieldName;
        this.orderBy = orderBy;
        this.userId = userId;
        this.transType = transType;
        this.dcSign = dcSign;
        this.rmNo = rmNo;
        this.page = page;
        this.pageSize = pageSize;
        this.batchNo = batchNo;
    }

    public String getCoreSn() {
        return coreSn;
    }

    public void setCoreSn(String coreSn) {
        this.coreSn = coreSn;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getDoc() {
        return doc;
    }

    public void setDoc(String doc) {
        this.doc = doc;
    }

    public BigDecimal getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(BigDecimal fromAmount) {
        this.fromAmount = fromAmount;
    }

    public BigDecimal getToAmount() {
        return toAmount;
    }

    public void setToAmount(BigDecimal toAmount) {
        this.toAmount = toAmount;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public Date getFromDateQuery() {
        return fromDateQuery;
    }

    public void setFromDateQuery(Date fromDateQuery) {
        this.fromDateQuery = fromDateQuery;
    }

    public Date getToDateQuery() {
        return toDateQuery;
    }

    public void setToDateQuery(Date toDateQuery) {
        this.toDateQuery = toDateQuery;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getHistoryType() {
        return historyType;
    }

    public void setHistoryType(String historyType) {
        this.historyType = historyType;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getDcSign() {
        return dcSign;
    }

    public void setDcSign(String dcSign) {
        this.dcSign = dcSign;
    }

    public String getRmNo() {
        return rmNo;
    }

    public void setRmNo(String rmNo) {
        this.rmNo = rmNo;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public String getPaymentFrom() {
        return paymentFrom;
    }

    public void setPaymentFrom(String paymentFrom) {
        this.paymentFrom = paymentFrom;
    }
}
