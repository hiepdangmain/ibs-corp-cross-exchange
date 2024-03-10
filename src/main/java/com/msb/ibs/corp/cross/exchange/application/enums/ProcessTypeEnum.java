package com.msb.ibs.corp.cross.exchange.application.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
@Getter
@AllArgsConstructor
public enum ProcessTypeEnum {
	GUARANTEE("GUARANTEE", "Bảo lãnh");

	private final String key;
	private final String value;

	public static boolean contains(String name) {
		if (!StringUtils.hasText(name)) {
			return false;
		}

		ProcessTypeEnum[] lst = ProcessTypeEnum.values();
		for (ProcessTypeEnum obj : lst) {
			if (obj.getKey().equalsIgnoreCase(name.trim())) {
				return true;
			}
		}
		return false;
	}
}
