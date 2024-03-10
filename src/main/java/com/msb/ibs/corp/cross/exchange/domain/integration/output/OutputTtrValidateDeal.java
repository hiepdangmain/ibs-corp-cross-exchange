package com.msb.ibs.corp.cross.exchange.domain.integration.output;

public class OutputTtrValidateDeal {
    private String rateDealId;
    private OutputTtrDeal oldDeal;
    private OutputTtrDeal newDeal;
    public OutputTtrDeal getOldDeal() {
        return oldDeal;
    }
    public void setOldDeal(OutputTtrDeal oldDeal) {
        this.oldDeal = oldDeal;
    }
    public OutputTtrDeal getNewDeal() {
        return newDeal;
    }
    public void setNewDeal(OutputTtrDeal newDeal) {
        this.newDeal = newDeal;
    }
    public String getRateDealId() {
        return rateDealId;
    }
    public void setRateDealId(String rateDealId) {
        this.rateDealId = rateDealId;
    }
}
