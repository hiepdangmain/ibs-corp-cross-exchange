package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import lombok.Data;

@Data
public class InfoRequirement {
    private String guaranteeType;
    private String currencyCode;
    private String issueAmount;
    private String issueDate;
    private String effectDate;
    private String expireDate;
    private String startedEvent;
    private String expireEvent;
    private String expireDateExpect;
    private String feeAccount;
    private String guaranteedGetName;
    private String guaranteedGetAddress;
    private String guaranteedPurpose;
    private String otherCommitments;
}
