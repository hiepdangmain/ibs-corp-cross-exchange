package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class InputGuaranteeFee {
    private String feeAccount;
    private BigDecimal issueAmount;
    private String currencyCode;
    private String typeBusiness;
    private String guaranteeType;
    private String formType;
    private String issueDate;
    private String effectDate;
    private String expireDate;
}
