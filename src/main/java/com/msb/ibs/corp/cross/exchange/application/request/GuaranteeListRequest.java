package com.msb.ibs.corp.cross.exchange.application.request;

import com.msb.ibs.corp.cross.exchange.domain.dto.Metadata;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeListRequest {
    @Schema(name = "tranSn", format = "String", description = "Ma giao dich")
    private String tranSn;
    @Schema(name = "status", format = "String", description = "Trang thai")
    private List<String> status;
    @Schema(name = "corpId", description = "CorpId")
    private Integer corpId;
    @Schema(name = "keyWord", format = "String", description = "Tu khoa")
    private String keyWord;
    @Schema(name = "cifNo", format = "String", description = "CIF doanh nghiep")
    private String cifNo;
    @Schema(name = "bpmId", format = "String", description = "Ma BPM")
    private String bpmId;
    @Schema(name = "guaranteeType", format = "String", description = "Loai bao lanh")
    private String guaranteeType;
    @Schema(name = "dateBy", format = "String", description = "1: ngay tao/2: ngay gui/3: ngay msb duyet")
    private String dateBy;
    @Schema(name = "fromDate", format = "dd/MM/yyyy", description = "Tu ngay")
    private String fromDate;
    @Schema(name = "toDate", format = "dd/MM/yyyy", description = "Den ngay")
    private String toDate;
    @Schema(name = "metadata", format = "Metadata", description = "pageNo, pageSize")
    private Metadata metadata;

}
