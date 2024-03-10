package com.msb.ibs.corp.cross.exchange.application.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class GuaranteeFeeResponse {
    private BigDecimal fee;
    private String currency;

    public GuaranteeFeeResponse(BigDecimal fee, String currency) {
        this.fee = fee;
        this.currency = currency;
    }
}
