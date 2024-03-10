package com.msb.ibs.corp.cross.exchange.application.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentGuarantee {
    private Integer documentId; //id ho so trong DB
    private Integer id; // id tu tang trong DB
    private String fileName;
    private String debt; //Nơ
    private String debtDate; // ngày nơ

}
