package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InputGuaranteeCheckLimit {
    private Long cifNumber;

    public InputGuaranteeCheckLimit(Long cifNumber) {
        this.cifNumber = cifNumber;
    }
}
