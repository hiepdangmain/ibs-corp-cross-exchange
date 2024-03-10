package com.msb.ibs.corp.cross.exchange.application.request;

import com.msb.ibs.corp.cross.exchange.application.model.DetailGuarantee;
import com.msb.ibs.corp.cross.exchange.application.model.DocumentGuarantee;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GuaranteeRequest {

    @Schema(name = "listOrder", format = "List<DetailGuarantee>", description = "list Danh sach order")
    private List<DetailGuarantee> listOrder;

    @Schema(name = "documents", format = "List<Document>", description = "Danh sach ma tai lieu")
    private List<DocumentGuarantee> documents;

    @Schema(name = "tranSn", format = "String", description = "neu chinh sua lai ma tranSn thi truyen truong nay")
    private String tranSn;

    @Schema(name = "newTranSn", format = "String", description = "Mã tranSn mới")
    private String newTranSn;

    @Schema(name = "fee", format = "Number", description = "915.12")
    private BigDecimal fee;
}
