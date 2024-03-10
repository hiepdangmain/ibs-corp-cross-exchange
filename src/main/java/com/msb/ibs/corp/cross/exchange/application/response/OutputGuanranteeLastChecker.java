package com.msb.ibs.corp.cross.exchange.application.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputGuanranteeLastChecker {
     private boolean lastChecker;
}
