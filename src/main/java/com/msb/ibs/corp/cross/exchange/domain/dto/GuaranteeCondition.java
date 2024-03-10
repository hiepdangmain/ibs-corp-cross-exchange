package com.msb.ibs.corp.cross.exchange.domain.dto;

import com.msb.ibs.corp.cross.exchange.application.model.DocumentGuarantee;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class GuaranteeCondition {
    @Schema(name = "applicant", format = "String", description = "Bên được bảo lãnh")
    private String applicant;

    @Schema(name = "certCode", format = "String", description = "Số ĐKKD")
    private String certCode;

    @Schema(name = "certCodeDate", format = "String", description = "Ngày cấp Số ĐKKD ")
    private String certCodeDate;

    @Schema(name = "certCodePlace", format = "String", description = "Nơi cấp Số ĐKKD")
    private String certCodePlace;

    @Schema(name = "formGuarantee", format = "String", description = "Hình thức phát hành")
    private String formGuarantee;

    @Schema(name = "swiftCode", format = "String", description = "Mã swift code")
    private String swiftCode;

    @Schema(name = "notificationTo", format = "String", description = "Gửi thông báo ")
    private String  notificationTo;

    @Schema(name = "typeGuarantee", format = "String", description = "Loại bảo lãnh")
    private String typeGuarantee;

    @Schema(name = "otherGuarantee", format = "String", description = "Loại bảo lãnh khác")
    private String otherGuarantee;

    @Schema(name = "beneficiary", format = "String", description = "Bên nhận bảo lãnh")
    private String beneficiary;

    @Schema(name = "beneficiaryAddress", format = "String", description = "Địa chỉ bên nhận bảo lãnh")
    private String beneficiaryAddress;

    @Schema(name = "amount", format = "String", description = "Số tiền")
    private String amount;

    @Schema(name = "currency", format = "String", description = "Loại tiền")
    private String currency;

    @Schema(name = "guaranteeStart", format = "String", description = "Bắt đầu hiệu lực")
    private String guaranteeStart;

    @Schema(name = "startDate", format = "String", description = "Ngày hiệu lực")
    private String startDate;

    @Schema(name = "startEventDate", format = "String", description = "Sự kiện bắt đầu hiệu lực")
    private String startEventDate;

    @Schema(name = "guaranteeEnd", format = "String", description = "Kết thúc hiệu lực")
    private String guaranteeEnd;

    @Schema(name = "expiredDate", format = "String", description = "Ngày hết hạn hiệu lực")
    private String expiredDate;

    @Schema(name = "endEventDate", format = "String", description = "Sự kiện kết thúc hiệu lực")
    private String endEventDate;

    @Schema(name = "expectEndDate", format = "String", description = "Ngày dự kiến kết thúc")
    private String expectEndDate;

    @Schema(name = "formatTextGuarantee", format = "String", description = "Nghĩa vụ được bảo lãnh và căn cứ phát sinh nghĩa vụ được bảo lãnh")
    private String formatTextGuarantee;

    @Schema(name = "commitGuarantee", format = "String", description = "Mẫu cam kết bảo lãnh")
    private String commitGuarantee;

    @Schema(name = "contractNo", format = "String", description = "Số Hợp đồng tín dụng")
    private String contractNo;

    @Schema(name = "accountPayment", format = "String", description = "Tài khoản thanh toán phí")
    private String accountPayment;

    @Schema(name = "commit1", format = "String", description = "Cam kết 1")
    private String commit1;

    @Schema(name = "commit2", format = "String", description = "Cam kết 2")
    private String commit2;

    @Schema(name = "commit2Date", format = "String", description = "Ngày Bổ sung hợp đồng")
    private String commit2Date;

    @Schema(name = "commit3", format = "String", description = "Cam kết 3")
    private String commit3;

    @Schema(name = "commitOther", format = "String", description = "Cam kết khac")
    private String commitOther;

    @Schema(name = "commitOtherContent", format = "String", description = "Noi dung Cam kết khac")
    private String commitOtherContent;

    @Schema(name = "documents", format = "List<Document>", description = "Danh sach ma tai lieu")
    private List<DocumentGuarantee> documents;

    @Schema(name = "tranSn", format = "String", description = "neu chinh sua lai ma tranSn thi truyen truong nay")
    private String tranSn;

    @Schema(name = "status", format = "String", description = "Trạng thái bản ghi")
    private String status;
}
