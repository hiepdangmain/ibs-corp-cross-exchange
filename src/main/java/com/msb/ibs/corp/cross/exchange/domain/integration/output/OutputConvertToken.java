package com.msb.ibs.corp.cross.exchange.domain.integration.output;

import lombok.Data;

@Data
public class OutputConvertToken {
    private String code;
    private String message;
    private int httpStatus;
    private String requestId;
    private ConvertTokenInfo data;
}
