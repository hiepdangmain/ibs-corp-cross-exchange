package com.msb.ibs.corp.cross.exchange.application.enums;

public enum StatusEnum {

	PENDING("PEND"), ACTIVE("ACTV"), DELETE("DLTD"), FROZEN("FRZU"), CLOSE("CLOS");

	private final String value;

	StatusEnum(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}

	public String getValue() {
		return this.value;
	}

}
