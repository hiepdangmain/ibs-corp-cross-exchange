package com.msb.ibs.corp.cross.exchange.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeDetailRequest {
    @Schema(name = "tranSn", format = "String", description = "Ma giao dich")
    private String tranSn;
}
