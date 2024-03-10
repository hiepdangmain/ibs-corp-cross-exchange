package com.msb.ibs.corp.cross.exchange.domain.dto;

import lombok.Data;

@Data
public class DocumentGuaranteeDto {
    private Integer id;
    private String documentNameVn;
    private String documentNameEn;
    private String fileName;
    private String debtDate;
    private String debt;
    private Integer documentId;
    private String attachmentType;
}
