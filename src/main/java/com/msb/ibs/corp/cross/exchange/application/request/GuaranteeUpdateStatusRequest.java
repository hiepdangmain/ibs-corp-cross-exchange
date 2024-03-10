package com.msb.ibs.corp.cross.exchange.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuaranteeUpdateStatusRequest {
    @Schema(name = "tranSn", format = "String", description = "Ma giao dich IB", example = "")
    private String tranSn;
    @Schema(name = "action", format = "String", description = "ACTION", example = "REJECT")
    private String action;
    @Schema(name = "content", format = "String", description = "Phan hoi tu choi/chinh sua", example = "")
    private String content;
    @Schema(name = "bpmCode", format = "String", description = "Ma BPM", example = "")
    private String bpmCode;
    @Schema(name = "guaranteeCode", format = "String", description = "So bao lanh", example = "")
    private String guaranteeCode;
    @Schema(name = "sendMail", format = "boolean", description = "Co gui mail tu IB (mac dinh = true)", example = "false")
    private boolean sendMail = true;
    @Schema(name = "bpmUser", format = "String", description = "Ten nguoi xu ly phia BPM", example = "")
    private String bpmUser;
    @Schema(name = "emailUser", format = "String", description = "Email nguoi xu ly phia BPM", example = "")
    private String emailUser;
    @Schema(name = "phoneUser", format = "String", description = "So DT nguoi xu ly phia BPM", example = "")
    private String phoneUser;
    @Schema(name = "lang", format = "String", description = "Ngon ngu", example = "vi")
    private String lang;
    @Schema(name = "refNumber", format = "String", description = "So bao lanh", example = "")
    private String refNumber;
}
