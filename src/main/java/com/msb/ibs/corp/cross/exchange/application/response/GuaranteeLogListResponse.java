package com.msb.ibs.corp.cross.exchange.application.response;

import com.msb.ibs.corp.cross.exchange.domain.dto.Metadata;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeLogListResponse {
    private List<BbGuaranteeLog> guaranteeLogList;
    private Metadata metadata;
}
