package com.msb.ibs.corp.cross.exchange.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class GuaranteeUploadRequest {

    @Schema(name = "fileName", format = "String", description = "Ten file")
    private String fileName;

    @Schema(name = "documentId", format = "Integer", description = "Loai file")
    private Integer documentId;

    @Schema(name = "fileContent", format = "String", description = "Noi dung file")
    private String fileContent;

    @Schema(name = "debt", format = "String", description = "No file")
    private String debt;

    @Schema(name = "debtDate", format = "String", description = "Ngay no")
    private String debtDate;

    @Schema(name = "newTranSn", format = "String", description = "Mã giao dịch")
    private String newTranSn;
}
