package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import lombok.Data;

import java.util.List;

@Data
public class CheckListItem {
    private String code;
    private List<FileItem> files;
}
