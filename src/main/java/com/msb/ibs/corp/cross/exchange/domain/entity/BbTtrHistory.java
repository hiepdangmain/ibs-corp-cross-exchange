package com.msb.ibs.corp.cross.exchange.domain.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
@Entity
@Table(name = "BB_TTR_HISTORY")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BbTtrHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "TRAN_SN")
	private String tranSn;

	@Column(name = "CORE_SN")
	private String coreSn;

	@Column(name = "CORP_ID")
	private Integer corpId;

	@Column(name = "USER_ID")
	private Integer userId;

	@Column(name = "ROLLOUT_ACCT_NO")
	private String rolloutAcctNo;

	@Column(name = "ROLLOUT_ACCT_NAME")
	private String rolloutAcctName;

	@Column(name = "ROLLOUT_ADDRESS")
	private String rolloutAddress;

	@Column(name = "PAYMENT_FROM")
	private String paymentFrom;

	@Column(name = "AMOUNT")
	private BigDecimal amount;

	@Column(name = "AMOUNT_IN_WORD")
	private String amountInWord;

	@Column(name = "AMOUNT_CONVERT")
	private String amountConvert;

	@Column(name = "CURRENCY")
	private String currency;

	@Column(name = "BNFC_SWIFT_CODE")
	private String bnfcSwiftCode;

	@Column(name = "BNFC_ACCT_NO")
	private String bnfcAcctNo;

	@Column(name = "BNFC_NAME")
	private String bnfcName;

	@Column(name = "BNFC_BANK_ID")
	private String bnfcBankId;

	@Column(name = "BNFC_BANK_NAME")
	private String bnfcBankName;

	@Column(name = "BNFC_BRANCH_ID")
	private String bnfcBranchId;

	@Column(name = "BNFC_BRANCH_NAME")
	private String bnfcBranchName;

	@Column(name = "BNFC_COUNTRY")
	private String bnfcCountry;

	@Column(name = "BNFC_ALIAS")
	private String bnfcAlias;

	@Column(name = "BNFC_BANK_ADDR")
	private String bnfcBankAddr;

	@Column(name = "RECEIVER_NAME")
	private String receiverName;

	@Column(name = "IS_IMPORT_EXPORT")
	private String isImportExport;

	@Column(name = "RECEIVER_TYPE")
	private String receiverType;

	@Column(name = "PAID_FEE_SOURCE")
	private String paidFeeSource;

	@Column(name = "FEE")
	private BigDecimal fee;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "RECORD_STATUS")
	private String recordStatus;

	@Column(name = "REMARK")
	private String remark;

	@Column(name = "MSB_STATUS")
	private String msbStatus;

	@Column(name = "APPROVE_CONTENT")
	private String approveContent;

	@Column(name = "REJECT_CONTENT")
	private String rejectContent;

	@Column(name = "WF_UPDATED_TIME")
	private Date wfUpdatedTime;

	@Column(name = "WF_STATUS")
	private String wfStatus;

	@Column(name = "WF_TASKID")
	private Integer wfTaskid;

	@Column(name = "CHANNEL_CODE")
	private String channelCode;
	
	@Column(name = "WF_PROCESS_ID")
	private String wfProcessId;

	@Column(name = "APPROVE_PRIORITY")
	private Integer approvePriority;

	@Column(name = "EXPIRE_DATE")
	private Date expireDate;

	@Column(name = "TRANS_DATE")
	private Date transDate;

	@Column(name = "WAITING_APPROVE")
	private Integer waitingApprove;

	@Column(name = "REMITANCE_METHOD")
	private String remitanceMethod;

	@Column(name = "PURPOSES_PAYMENT")
	private Integer purposesPayment;

	@Column(name = "RATE_TYPE")
	private String rateType;

	@Column(name = "RATE_DEAL")
	private String rateDeal;

	@Column(name = "CREATE_BY")
	private Integer createBy;

	@CreationTimestamp
	@Column(name = "CREATE_TIME")
	private Date createTime;

	@Column(name = "UPDATE_BY")
	private Integer updateBy;

	@UpdateTimestamp
	@Column(name = "UPDATE_TIME")
	private Date updateTime;

	@Column(name = "SALE_TYPE")
	private String saleType;

	@Column(name = "ITEM_TYPE")
	private String itemType;

	@Column(name = "TRADE_FEE")
	private Integer tradeFee;

	@Column(name = "TRANSPORT_TYPE")
	private String transportType;

	@Column(name = "CONTRACT_NO")
	private String contractNo;

	@Column(name = "BILL_NO")
	private String billNo;

	@Column(name = "FORM_NO")
	private String formNo;

	@Column(name = "RECEIVER_ADDR")
	private String receiverAddr;

	@Column(name = "RATE_DEAL_ID")
	private String rateDealId;

	@Column(name = "MSB_FEE")
	private BigDecimal msbFee;

	@Column(name = "BNFC_FEE")
	private BigDecimal bnfcFee;

	@Column(name = "ITEM_ID")
	private Integer itemId;

	@Column(name = "RM_CODE")
	private String rmCode;

	@Column(name = "STATE_BANK_STATUS")
	private String stateBankStatus;

	@Column(name = "TRANSACTION_TYPE")
	private String transactionType;

	@Column(name = "IMB_SWIFT_NO")
	private String imbSwiftNo;

	@Column(name = "IMB_BANK_NAME")
	private String imbBankName;

	@Column(name = "IMB_BANK_ADDR")
	private String imbBankAddr;

	@Column(name = "IMB_COUNTRY")
	private String imbCountry;

	@Column(name = "SERVICE_TYPE")
	private String serviceType;

	@Column(name = "DEBIT_ACCT_NO")
	private String debitAcctNo;

	@Column(name = "RATE_FEE")
	private BigDecimal rateFee;

	@Column(name = "TOTAL_FEE")
	private BigDecimal totalFee;

	@Column(name = "NOTIFICATION")
	private String notification;

	@Column(name = "ISSUING_DATE")
	private Date issuingDate;
	
	@Column(name = "REF_NO")
	private String refNo;
	
	@Column(name = "ORG_NO")
	private String orgNo;
	
	@Column(name = "RATE_MSB")
	private BigDecimal rateMsb;
	
	@Column(name = "RATE_MSB2")
	private BigDecimal rateMsb2;
	
	@Column(name = "CHECK_FEE_STATUS")
	private String checkFeeStatus;
}
