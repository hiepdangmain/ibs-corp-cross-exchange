package com.msb.ibs.corp.cross.exchange.application.request;

import com.msb.ibs.corp.cross.exchange.application.model.DetailGuarantee;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class GuaranteeFeeRequest {
    @Schema(name = "listOrder", format = "List<DetailGuarantee>", description = "Danh sach order (truy van maker)")
    private List<DetailGuarantee> listOrder;
    @Schema(name = "tranSn", format = "String", description = "Ma giao dich (truy van checker)")
    private String tranSn;
}
