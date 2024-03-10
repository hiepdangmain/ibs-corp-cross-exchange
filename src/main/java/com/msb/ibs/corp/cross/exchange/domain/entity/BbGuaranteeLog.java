package com.msb.ibs.corp.cross.exchange.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "BB_GUARANTEE_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BbGuaranteeLog implements Serializable {

    @Id
    @Column(name = "TRAN_SN")
    private String tranSn;

    @Column(name = "ACTION")
    private String action;

    @Column(name = "OLD_STATUS")
    private String oldStatus;

    @Column(name = "NEW_STATUS")
    private String newStatus;

    @Column(name = "maker_msb")
    private String makerMsb;

    @Column(name = "CREATE_BY")
    private Integer createBy;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "code_guarantee")
    private String codeGuarantee;

    @Column(name = "bpm_code")
    private String bpmCode;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "cif_no")
    private String cifNo;

    @Column(name = "receive_guarantee")
    private String receiveGuarantee;

    @Column(name = "type_guarantee")
    private String typeGuarantee;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "customer_send_time")
    private Date customerSendTime; // Checker Duyetj xong thi insert vao day

    @Column(name = "time_msb_receive")
    private Date msbReceiveTime;

    @Column(name = "time_msb_request")
    private Date msbRequestTime;

    @Column(name = "time_msb_end")
    private Date msbEndTime;

    @Column(name = "reject_content")
    private String rejectContent;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "msb_update_time")
    private Date msbUpdateTime;

    @Column(name = "applicant")
    private String applicant; // ben nhan bao lanh

    @Column(name = "transn_refer")
    private String transnRefer;
}
