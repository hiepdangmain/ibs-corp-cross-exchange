package com.msb.ibs.corp.cross.exchange.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "BB_GUARANTEE_DOCUMENT_CONFIG")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BbGuaranteeDocumentConfig implements Serializable {
    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "Document_name_vn")
    private String docNameVn;

    @Column(name = "Document_name_EN")
    private String docNameEn;

    @Column(name = "bpm_code")
    private String bpmCode;

    @Column(name = "mandatory")
    private String mandatory;

    @Column(name = "SEQ_NO")
    private Integer seqNo;

    @Column(name = "debt")
    private String debt;

    @Column(name = "type")
    private String type;

    @Column(name = "value_type")
    private String valueType;
}
