package com.msb.ibs.corp.cross.exchange.application.enums;

import org.springframework.util.StringUtils;

public enum AttachmentTypeEnum {
    SERVER("SERVER"), ECM("ECM"), MIN_IO("MIN_IO");

    private final String value;

    AttachmentTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static boolean contains(String name) {
        if (!StringUtils.hasText(name)) {
            return false;
        }

        AttachmentTypeEnum[] lst = AttachmentTypeEnum.values();
        for (AttachmentTypeEnum obj : lst) {
            if (obj.value().equalsIgnoreCase(name.trim())) {
                return true;
            }
        }
        return false;
    }

}
