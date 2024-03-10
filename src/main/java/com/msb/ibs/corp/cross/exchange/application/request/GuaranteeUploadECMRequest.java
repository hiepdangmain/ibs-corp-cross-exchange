package com.msb.ibs.corp.cross.exchange.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuaranteeUploadECMRequest {
    private Integer fileId;
}
