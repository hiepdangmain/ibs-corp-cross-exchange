package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InputGuaranteeCancel {
    private String ibCorpId;
    private String feedBack;
    private Long cifNumber;
}
