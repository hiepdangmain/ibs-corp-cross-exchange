package com.msb.ibs.corp.cross.exchange.domain.integration.output;

public class OutputTtrValidateFee {
    private boolean changeFee = false;
    private String totalFee;

    public boolean isChangeFee() {
        return changeFee;
    }

    public void setChangeFee(boolean changeFee) {
        this.changeFee = changeFee;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }
}
