package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import com.msb.ibs.corp.cross.exchange.domain.dto.TransferApprovalInfo;

import java.util.List;

public class InputAuthTransactionTTR {
    List<TransferApprovalInfo> transferApprovalList;
    int isApprove = 1; // 1 - approve 0 - reject 2 - upda - 3 -ckng
    private String type;
    String lang = "VN";

    public int getIsApprove() {
        return isApprove;
    }

    public void setIsApprove(int isApprove) {
        this.isApprove = isApprove;
    }

    public List<TransferApprovalInfo> getTransferApprovalList() {
        return transferApprovalList;
    }

    public void setTransferApprovalList(List<TransferApprovalInfo> transferApprovalList) {
        this.transferApprovalList = transferApprovalList;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
