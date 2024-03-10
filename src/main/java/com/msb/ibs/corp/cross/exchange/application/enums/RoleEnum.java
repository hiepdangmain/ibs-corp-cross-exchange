package com.msb.ibs.corp.cross.exchange.application.enums;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
public enum RoleEnum {
	MAKER("4"), CHECKER("3"), ADMIN("2");

	private final String value;

	RoleEnum(String value) {
		this.value = value;
	}

	public String value() {
		return this.value;
	}
}
