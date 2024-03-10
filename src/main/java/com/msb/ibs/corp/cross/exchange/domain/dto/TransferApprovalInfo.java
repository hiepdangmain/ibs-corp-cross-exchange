package com.msb.ibs.corp.cross.exchange.domain.dto;

import java.math.BigDecimal;

public class TransferApprovalInfo {
    private String wfProcessId;
    private BigDecimal amount;

    public String getWfProcessId() {
        return wfProcessId;
    }
    public void setWfProcessId(String wfProcessId) {
        this.wfProcessId = wfProcessId;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
