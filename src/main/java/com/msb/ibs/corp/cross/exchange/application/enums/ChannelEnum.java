package com.msb.ibs.corp.cross.exchange.application.enums;

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
@Getter
@AllArgsConstructor
public enum ChannelEnum {
	IB("IB", "Internet banking"), MB("MB", "Mobile");

	private final String key;
	private final String value;

	public static boolean contains(String name) {
		if (!StringUtils.hasText(name)) {
			return false;
		}

		ChannelEnum[] lst = ChannelEnum.values();
		for (ChannelEnum obj : lst) {
			if (obj.getKey().equalsIgnoreCase(name.trim())) {
				return true;
			}
		}
		return false;
	}
}
