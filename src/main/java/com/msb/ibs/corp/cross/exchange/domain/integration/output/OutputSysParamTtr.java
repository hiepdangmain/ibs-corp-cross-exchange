package com.msb.ibs.corp.cross.exchange.domain.integration.output;

import java.util.List;

public class OutputSysParamTtr {
    private List<OutputBkSysParam> paymentFroms;
    private List<OutputBkSysParam> paidFeeSources;
    private List<OutputBkSysParam> rates;
    public List<OutputBkSysParam> remitanceMethods;
    public List<OutputBkSysParam> receiverTypes;
    public List<OutputBkSysParam> transportTypes;

    public List<OutputBkSysParam> getPaymentFroms() {
        return paymentFroms;
    }

    public void setPaymentFroms(List<OutputBkSysParam> paymentFroms) {
        this.paymentFroms = paymentFroms;
    }

    public List<OutputBkSysParam> getPaidFeeSources() {
        return paidFeeSources;
    }

    public void setPaidFeeSources(List<OutputBkSysParam> paidFeeSources) {
        this.paidFeeSources = paidFeeSources;
    }

    public List<OutputBkSysParam> getRates() {
        return rates;
    }

    public void setRates(List<OutputBkSysParam> rates) {
        this.rates = rates;
    }

    public List<OutputBkSysParam> getRemitanceMethods() {
        return remitanceMethods;
    }

    public void setRemitanceMethods(List<OutputBkSysParam> remitanceMethods) {
        this.remitanceMethods = remitanceMethods;
    }

    public List<OutputBkSysParam> getReceiverTypes() {
        return receiverTypes;
    }

    public void setReceiverTypes(List<OutputBkSysParam> receiverTypes) {
        this.receiverTypes = receiverTypes;
    }

    public List<OutputBkSysParam> getTransportTypes() {
        return transportTypes;
    }

    public void setTransportTypes(List<OutputBkSysParam> transportTypes) {
        this.transportTypes = transportTypes;
    }
}
