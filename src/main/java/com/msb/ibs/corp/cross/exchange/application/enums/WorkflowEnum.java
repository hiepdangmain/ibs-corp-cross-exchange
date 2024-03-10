package com.msb.ibs.corp.cross.exchange.application.enums;

public enum WorkflowEnum {

    GUARANTEE_CROSS_EXCHANGE("BB_GUARANTEE_HISTORY");

    private final String value;

    WorkflowEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
