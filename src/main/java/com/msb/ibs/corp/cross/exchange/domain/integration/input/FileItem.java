package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import lombok.Data;

@Data
public class FileItem {
    private String fileId;
    private String name;
    private String path;
}
