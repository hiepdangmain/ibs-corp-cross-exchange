package com.msb.ibs.corp.cross.exchange.application.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RedisKey {
    ACCESS_TOKEN_BPM("ACCESS_TOKEN_BPM", 1800);

    private final String key;
    private final long expireTime;
}
