package com.msb.ibs.corp.cross.exchange.domain.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "BB_GUARANTEE_META_DATA")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BbGuaranteeMetaData {

    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "USER_ID")
    private Integer userId;
    @Column(name = "CORP_ID")
    private Integer corpId;
    @Column(name = "APPLICANT")
    private String applicant;
    @Column(name = "CERT_CODE")
    private String certCode;
    @Column(name = "CERT_CODE_DATE")
    private String certCodeDate;
    @Column(name = "CERT_CODE_PLACE")
    private String certCodePlace;
    @Column(name = "BENEFICIARY")
    private String beneficiary;
    @Column(name = "BENEFICIARY_ADDRESS")
    private String beneficiaryAddress;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "CREATED_TIME")
    private Date createdTime;
    @Column(name = "UPDATED_TIME")
    private Date updatedTime;

    @PrePersist
    public void prePersist() {
        createdTime = new Date();
        updatedTime = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        updatedTime = new Date();
    }

}
