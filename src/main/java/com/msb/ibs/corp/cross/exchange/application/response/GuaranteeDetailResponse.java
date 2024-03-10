package com.msb.ibs.corp.cross.exchange.application.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.msb.ibs.corp.cross.exchange.application.model.DetailGuarantee;
import com.msb.ibs.corp.cross.exchange.application.model.DocumentGuarantee;
import com.msb.ibs.corp.cross.exchange.domain.dto.DocumentGuaranteeDto;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeHistoryDetailDto;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.OutputDetailReject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeDetailResponse {
    @Schema(name = "guaranteeHistory", format = "BbGuaranteeHistory", description = "Giao dich bao lanh")
    private BbGuaranteeHistory guaranteeHistory;
    @Schema(name = "details", format = "List<GuaranteeHistoryDetailDto>", description = "Thong tin chi tiet bao lanh")
    private List<GuaranteeHistoryDetailDto> details;
    @Schema(name = "documents", format = "List<DocumentGuaranteeDto>", description = "Danh sach tai lieu")
    private List<DocumentGuaranteeDto> documents;
    @Schema(name = "listReject", format = "List<OutputDetailReject>", description = "List nội dung phản hồi")
    private List<OutputDetailReject> listReject;
    private Map<String, Object> emailParams;
}
