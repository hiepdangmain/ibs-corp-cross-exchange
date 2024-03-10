package com.msb.ibs.corp.cross.exchange.application.response;

import com.msb.ibs.corp.cross.exchange.domain.dto.Metadata;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeListResponse {
    private List<BbGuaranteeHistory> guaranteeHistoryList;
    private Metadata metadata;
}
