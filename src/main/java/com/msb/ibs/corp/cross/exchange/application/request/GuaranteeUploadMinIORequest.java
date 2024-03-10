package com.msb.ibs.corp.cross.exchange.application.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuaranteeUploadMinIORequest {
    private String fileId;
    private String channel = "BPM";
}
