package com.msb.ibs.corp.cross.exchange.domain.integration.output;

import com.msb.ibs.common.response.ApiResponseData;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponseBpm<T, S> {
    private String timestamp;
    private int httpStatus;
    private String requestId;
    private T data;
    private S status;
    private String error;
    private String path;
}
