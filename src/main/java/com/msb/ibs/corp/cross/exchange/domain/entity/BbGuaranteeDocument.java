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
@Table(name = "BB_GUARANTEE_DOCUMENT")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BbGuaranteeDocument implements Serializable {
    @Id
    @Column(name = "ID")
    private Integer id;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "VALUE")
    private String value;

    @Column(name = "LABEL_VN")
    private String labelVn;

    @Column(name = "LABEL_EN")
    private String labelEn;

    @Column(name = "SEQ_NO")
    private String seqNo;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "MANDATORY")
    private String mandatory;
}
