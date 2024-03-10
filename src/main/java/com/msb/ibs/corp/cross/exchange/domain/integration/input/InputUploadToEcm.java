package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InputUploadToEcm {
    private String fileName;

    private String data;

    private String folderName;
}
