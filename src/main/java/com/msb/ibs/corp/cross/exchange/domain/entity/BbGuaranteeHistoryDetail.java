package com.msb.ibs.corp.cross.exchange.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "BB_GUARANTEE_HISTORY_DETAIL")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class BbGuaranteeHistoryDetail implements Serializable {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_Sequence")
    @SequenceGenerator(name = "id_Sequence", sequenceName = "SEQ_GUARANTEE_HISTORY_DETAIL_ID", allocationSize = 1)
    private Integer id;

    @Column(name = "corp_id")
    private Integer corpId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "param")
    private String param;

    @Column(name = "value")
    private String value;

    @Column(name = "seq_no")
    private String seqNo;

    @Column(name = "CREATE_BY")
    private Integer createBy;

    @CreationTimestamp
    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "note")
    private String note;

    @Column(name = "mandatory")
    private String mandatory;

    @Column(name = "tran_refer")
    private String tranRefer;

}
