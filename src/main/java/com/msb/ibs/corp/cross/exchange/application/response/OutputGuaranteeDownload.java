package com.msb.ibs.corp.cross.exchange.application.response;

import lombok.Data;

@Data
public class OutputGuaranteeDownload {
    private String fileName;
    private byte[] fileContent;
}
