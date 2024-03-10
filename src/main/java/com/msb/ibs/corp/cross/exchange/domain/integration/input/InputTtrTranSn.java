package com.msb.ibs.corp.cross.exchange.domain.integration.input;

public class InputTtrTranSn {
    private String  tranSn;


    public InputTtrTranSn(String tranSn) {
        super();
        this.tranSn = tranSn;

    }

    public InputTtrTranSn() {
        super();
    }
    public String getTranSn() {
        return tranSn;
    }

    public void setTranSn(String tranSn) {
        this.tranSn = tranSn;
    }
}
