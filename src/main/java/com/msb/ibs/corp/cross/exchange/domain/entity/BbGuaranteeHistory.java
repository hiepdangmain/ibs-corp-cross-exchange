package com.msb.ibs.corp.cross.exchange.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "BB_GUARANTEE_HISTORY")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BbGuaranteeHistory implements Serializable {

    @Id
    @Column(name = "tran_sn")
    private String tranSn;

    @Column(name = "corp_id")
    private Integer corpId;

    @Column(name = "corp_name")
    private String corpName;

    @Column(name = "cif_no")
    private String cifNo;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "type_guarantee")
    private String typeGuarantee;

    @Column(name = "status")
    private String status;

    @Column(name = "record_status")
    private String recordStatus;

    @Column(name = "code_guarantee")
    private String codeGuarantee;

    @Column(name = "bpm_code")
    private String bpmCode;

    @Column(name = "CREATE_BY")
    private Integer createBy;

    @CreationTimestamp
    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "UPDATE_BY")
    private Integer updateBy;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;

    @Column(name = "maker_msb")
    private String makerMsb; //nhan su MSB tiep nhan xu ly

    @Column(name = "WF_UPDATED_TIME")
    private Date wfUpdatedTime;

    @Column(name = "WF_STATUS")
    private String wfStatus;

    @Column(name = "WF_TASKID")
    private Integer wfTaskid;

    @Column(name = "channel_code")
    private String channelCode;

    @Column(name = "wf_process_id")
    private String wfProcessId;

    @Column(name = "receive_guarantee")
    private String receiveGuarantee;

    @Column(name = "msb_update_by")
    private Integer msbUpdateBy;

    @Column(name = "msb_update_time")
    private Date msbUpdateTime;

    @Column(name = "currency")
    private String currency;

    @Column(name = "reject_content")
    private String rejectContent;

    @Column(name = "update_content")
    private String updateContent;

    @Column(name = "request_type")
    private String requestType;

    @Column(name = "transn_refer")
    private String transnRefer;

    @Column(name = "transn_active")
    private String transnActive;

    @Column(name = "expired_date")
    private Date expiredDate;

    @Column(name = "customer_send_time")
    private Date customerSendTime; // Checker Duyetj xong thi insert vao day

    @Column(name = "msb_receive_time")
    private Date msbReceiveTime;

    @Column(name = "customer_mk_name")
    private String customerMkName;

    @Column(name = "customer_ck_name")
    private String customerCkName;

    @Column(name = "assignee_ck")
    private String assigneeCk;

    @Column(name = "list_ck_approve")
    private String listCkApprove;

    @Column(name = "applicant")
    private String applicant; // ben nhan bao lanh

    @Column(name = "last_ck")
    private String lastCk; // checker cuoi cung

    @Column(name = "STATUS_SYNC")
    private String statusSync; //trang thai dong bo sang BPM

    @Column(name = "NEXT_SYNC_TIME")
    private Date nextSyncTime; //thoi gian tiep theo quet dong bo sang BPM

    @Column(name = "NUM_SYNC")
    private Integer numSync; //so lan dong bo sang BPM

    @Column(name = "BPM_ERROR_CODE")
    private String bpmErrorCode;

    @Column(name = "BPM_ERROR_MSG")
    private String bpmErrorMsg;

    @Column(name = "EMAIL_USER")
    private String emailUser; //email nhan su MSB tiep nhan xu ly

    @Column(name = "PHONE_USER")
    private String phoneUser; //phone nhan su MSB tiep nhan xu ly

    @Column(name = "EMAIL_CHECKER")
    private String emailChecker; //email checker duyet cuoi cung
}
