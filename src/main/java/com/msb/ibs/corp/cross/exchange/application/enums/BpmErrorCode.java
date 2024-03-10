package com.msb.ibs.corp.cross.exchange.application.enums;

public enum BpmErrorCode {

    BLI200("BLI200", "Success"),
    BLIBB2("BLIBB2", "Created"),
    IBC405("IBC405", "Ibcorp Id đã tồn tại"),
    IBC425("IBC425", "Ibcorp Id không tồn tại")
    ;

    private final String code;
    private final String message;

    BpmErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
