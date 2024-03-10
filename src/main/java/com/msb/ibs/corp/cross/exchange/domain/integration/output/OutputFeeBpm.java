package com.msb.ibs.corp.cross.exchange.domain.integration.output;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OutputFeeBpm {
    private BigDecimal rate;
    private String segment;
    private BigDecimal rootPrice;
    private String vipCode;
    private String status;
}
