package com.msb.ibs.corp.cross.exchange.application.response;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocument;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocumentConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputGuaranteeCount {

    private String status;
    private int countStatus;
}
