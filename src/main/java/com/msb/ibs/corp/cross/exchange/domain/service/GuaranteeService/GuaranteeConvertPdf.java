package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.utils.CommonStringUtils;
import com.msb.ibs.common.utils.DateUtil;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeConverPdfRequest;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeDeleteRequest;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuaranteeDownload;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeHistoryDetailDto;
import com.msb.ibs.corp.cross.exchange.domain.entity.*;
import com.msb.ibs.corp.cross.exchange.domain.repository.*;
import com.msb.ibs.corp.cross.exchange.domain.service.Common.CommonService;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeConvertPdf {

    @Autowired
    private BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository;

    @Autowired
    private BbGuaranteeHistoryDetailRepository bbGuaranteeHistoryDetailRepository;

    @Autowired
    private GuaranteeCommonService guaranteeCommonService;

    @Autowired
    private BkSwiftInfoRepository bkSwiftInfoRepository;

    @Autowired
    private BbGuaranteeFileAttachmentRepository fileAttachmentRepository;

    @Autowired
    private BbGuaranteeDocumentConfigRepository bbGuaranteeDocumentConfigRepository;

    @Autowired
    CommonService commonService;

    private String amount;
    private String currency;
    private String typeGuarantee = "";

    private String commit2Content = "";
    private String commit3Content = "";
    private String commitOtherContent = "";
    private String fileAttachContent = "";

    private Map<String, GuaranteeHistoryDetailDto> details = new HashMap<>();
    private String contentAll = "<b>A. Thông tin khách hàng/Applicant:</b> <br/><br/>";

    public OutputGuaranteeDownload execute (UserPrincipal principal, GuaranteeConverPdfRequest request) throws LogicException {
        if (request.getTranSn() == null || StringUtil.isEmpty(request.getType()) ) {
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        }
        if (StringUtil.isNotEmpty(request.getType())) {
            if (!("1".equalsIgnoreCase(request.getType()) || "2".equalsIgnoreCase(request.getType())))
                throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        }
        BbGuaranteeHistory bbGuaranteeHistory = bbGuaranteeHistoryRepository.selectBytranSn(request.getTranSn());
        if (bbGuaranteeHistory == null) {
            throw new BadRequestException("ERROR.GUARANTEE.CONVERT.REPORT.WRONG.TRAN.SN");
        }
        if (principal.getCorpId().compareTo(bbGuaranteeHistory.getCorpId()) != 0)
            throw new BadRequestException("ERROR.GUARANTEE.CONVERT.REPORT.WRONG.CORP.ID");


        Map<String, Object> params = new HashMap<String, Object>();
        List<BbGuaranteeHistoryDetail> listDetails = bbGuaranteeHistoryDetailRepository.selectByTranSn(request.getTranSn());
        if (listDetails.size() > 0) {
            for (BbGuaranteeHistoryDetail item : listDetails) {
                GuaranteeHistoryDetailDto detailGuarantee = new GuaranteeHistoryDetailDto();
                BeanUtils.copyProperties(item, detailGuarantee);
                details.put(detailGuarantee.getParam(), detailGuarantee);


               /* switch (item.getParam()) {
                    case "applicant":
                        if (StringUtil.isNotEmpty(item.getValue()))
                            params.put("applicant", item.getValue());
                        break;
                    case "certCode":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("certCode", item.getValue());
                        }
                        break;
                    case "certCodeDate":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("certCodeDate", item.getValue());
                        }
                        break;
                    case "certCodePlace":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("certCodePlace", item.getValue());
                        }
                        break;
                    case "formGuarantee":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            String formGuarantee = "";
                            if (item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_1)) {
                                formGuarantee = "Thư bảo lãnh";
                                params.put("checkFormGuarantee", true);
                            } else if (item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_1)) {
                                formGuarantee = "Hợp đồng bảo lãnh";
                                params.put("checkFormGuarantee", true);
                            } else {
                                formGuarantee = "Điện swift";
                                params.put("checkFormGuarantee", false);
                            }
                            params.put("formGuarantee", formGuarantee);

                        }
                        break;
                    case "swiftCode":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            BkSwiftInfo swiftInfo = bkSwiftInfoRepository.findBkSwiftInfoByswiftCode(item.getValue());
                            if (swiftInfo != null)
                                params.put("swiftCodeName", swiftInfo.getSwiftName());
                            params.put("swiftCode", item.getValue());
                        }

                        break;
                    case "typeGuarantee":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            if (item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_1)) {
                                typeGuarantee += "Bảo lãnh dự thầu" + "\n";
                            } else if (item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_2)) {
                                typeGuarantee += "Bảo lãnh thực hiện hợp đồng" + "\n";
                            } else if (item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_3)) {
                                typeGuarantee += "Bảo lãnh thanh toán" + "\n";
                            } else if (item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_4)) {
                                typeGuarantee += "Bảo lãnh tạm ứng" + "\n";
                            } else if (item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_5)) {
                                typeGuarantee += "Bảo lãnh bảo hành" + "\n";
                            } else if (item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_6)) {
                                typeGuarantee += "Bảo lãnh vay vốn" + "\n";
                            } else if (item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_7)) {
                                typeGuarantee += "Bảo lãnh thuế" + "\n";
                            }
                        }
                        break;
                    case "otherGuarantee":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("typeGuarantee", item.getValue());
                            typeGuarantee += item.getValue() ;
                        }
                        break;
                    case "beneficiary":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("beneficiary", item.getValue());
                        }
                        break;
                    case "beneficiaryAddress":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("beneficiaryAddress", item.getValue());
                        }
                        break;
                    case "amount":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            DecimalFormat df = new DecimalFormat("#,##0.00");
                            params.put("amount", item.getValue());
                            amount = df.format(new BigDecimal(item.getValue())).toString();
                        }
                        break;
                    case "currency":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("currency", item.getValue());
                            currency = item.getValue();
                        }
                        break;
                    case "guaranteeStart":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("guaranteeStart", item.getValue());
                        }
                        break;
                    case "startDate":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("startDate", item.getValue());
                        }
                        break;
                    case "startEventDate":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("startEventDate", item.getValue());
                        }
                        break;
                    case "guaranteeEnd": //Kết thúc hiệu lực
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("guaranteeEnd", item.getValue());
                            if(item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_END_EVENT)) {
                                params.put("checkGuaranteeEnd", true);
                            } else {
                                params.put("checkGuaranteeEnd", false);
                            }
                        }
                        break;
                    case "expiredDate": //Ngày hết hạn hiệu lực
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("expiredDate", item.getValue());
                        }
                        break;
                    case "endEventDate":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("endEventDate", item.getValue());
                        }
                        break;
                    case "expectEndDate":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("expectEndDate", item.getValue());
                        }
                        break;
                    case "formatTextGuarantee":
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("formatTextGuarantee", item.getValue());
                        }
                        break;

                    case "commitGuarantee": // Mẫu cam kết bảo lãnh
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("commitGuarantee", item.getValue());
                            if (item.getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_1)){
                                params.put("commitGuarantee", "Theo mẫu của MSB/<i>As per MSB’s templates</i>");
                            } else if (item.getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_2)) {
                                params.put("commitGuarantee", "Theo mẫu của các cơ quan nhà nước có thẩm quyền/<i>As per authority’s templates (Specify the name of State authorities:…)</i>");
                            } else {
                                params.put("commitGuarantee", "Theo mẫu chúng tôi cung cấp (đính kèm) được MSB chấp thuận/<i>As per applicant’s templates which are accepted by MSB</i>");
                            }
                        }
                        break;
                    case "contractNo": // Số Hợp đồng tín dụng
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("contractNo", item.getValue());
                        }
                        break;
                    case "accountPayment"://Tài khoản thanh toán phí
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("accountPayment", item.getValue());
                        }
                        break;
                    case "commit1"://Cam kết 1
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("commit1", item.getValue());
                        }
                        break;
                    case "commit2"://Cam kết 2

                        break;
                    case "commit2Date"://Ngày Bổ sung hợp đồng
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            commit2Content = "\n"+"- Bổ sung Hợp đồng kinh tế chậm nhất ngày " + item.getValue();
                            params.put("commit2", commit2Content);

                        }
                        break;
                    case "commit3"://Cam kết 3
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            if (item.getValue().equalsIgnoreCase("Y")) {
                                commit3Content = "\n"+"- “- Hạn mức của chúng tôi tại MSB sẽ được sử dụng để phát hành bảo lãnh bảo đảm cho nghĩa vụ của các thành viên trong liên danh và chúng tôi cam kết sẽ thực hiện toàn bộ các khoản phải trả cho MSB liên quan tới bảo lãnh được phát hành, không phụ thuộc vào hành vi vi phạm phát sinh từ bất kỳ thành viên nào của liên danh. ";
                                params.put("commit3", commit3Content);
                            }
                        }
                        break;
                    case "commitOther"://Cam kết khác
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            params.put("commitOther", item.getValue());
                        }
                        break;
                    case "commitOtherContent"://Noi dung Cam kết khac
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            commitOtherContent += item.getValue();
                            params.put("commitOtherContent", commitOtherContent);
                        }
                        break;
                    default:
                        break;
                }*/
            }
        }
        OutputGuaranteeDownload output = new OutputGuaranteeDownload();
        output.setFileName(AppConstant.GUARANTEE_FILE_PROPOSAL_FORM);

        // get ten file
        List<BbGuaranteeFileAttachment> listFile = fileAttachmentRepository.findByTransRefer(request.getTranSn());
        if (listFile.size() > 0) {
            List<BbGuaranteeDocumentConfig> documentConfigs = bbGuaranteeDocumentConfigRepository.
                    findListDocumentConfig(List.of("0", bbGuaranteeHistory.getTypeGuarantee()));
            for (BbGuaranteeFileAttachment file : listFile) {
                for (BbGuaranteeDocumentConfig documentConfig : documentConfigs) {
                    if (documentConfig.getSeqNo().intValue() == file.getDocumentId().intValue()) {
                        fileAttachContent += "<br/><br/>- "+ documentConfig.getDocNameVn() + ": "+"<br/>";
                    }
                }
                if (StringUtil.isNotEmpty(file.getDebtDate())) {
                    fileAttachContent += "<b>"+(file.getFileName() != null ?  file.getFileName() : "" )+ "   Hoàn chứng từ: " + file.getDebtDate() + "</b><br/><br/>";
                } else {
                    fileAttachContent += "<b>"+ file.getFileName() + "</b><br/>";
                }
            }
        }
        getContentHeader();
        getContentFooter(principal, request);

       /* //cach doc so tien
        params.put("amountEN", StringUtil.getStringAmountEN(amount, currency).toUpperCase());
        params.put("amountVN", StringUtil.getStringAmount(amount, currency).toUpperCase());

        params.put("nickChecker", principal.getNick());
        params.put("userNameChecker", principal.getUsername());
        params.put("certCodeChecker", principal.getCertCode());
        params.put("dateSign", DateUtil.formatDate(new Date(), DateUtil.FORMAT_DATE_ddMMyyyyHHmmss).toString());
        params.put("typeGuarantee", typeGuarantee);
        params.put("fileAttachContent", fileAttachContent );*/
        // ngay tao
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(bbGuaranteeHistory.getCreateTime());
        params.put("dateCreate", "Ngày "+ calendar.get(Calendar.DAY_OF_MONTH) + " tháng "+ (calendar.get(Calendar.MONTH) +1 ) + " năm " +calendar.get(Calendar.YEAR));
        params.put("contentAll", contentAll );
        byte[] data = convertByte("ReportGuarantee", params);
        output.setFileContent(data);
        return output;
    }

    private void getContentFooter(UserPrincipal principal, GuaranteeConverPdfRequest request) {
        contentAll += "<br/><br/> <br/><br/><br/><br/><b>ĐẠI DIỆN ĐƠN VỊ</b>";
        contentAll += "<br/><br/> Tên người đại diện:               " + "<b>" + (request.getType().equalsIgnoreCase("2") ? (principal.getNick() != null ? principal.getNick() : "") :"") + "</b>";
        contentAll += "<br/><br/> Mã người dùng :                   " + "<b>" + (request.getType().equalsIgnoreCase("2") ? (principal.getUsername() != null ? principal.getUsername() : "") : "") + "</b>";
        contentAll += "<br/><br/> Số CMND/Hộ chiếu/CCCD :           " + "<b>" + (request.getType().equalsIgnoreCase("2") ? (principal.getCertCode() != null ? principal.getCertCode() : "") : "") + "</b>";
        contentAll += "<br/><br/> Ngày ký:                          " + "<b>" +  (request.getType().equalsIgnoreCase("2") ? DateUtil.formatDate(new Date(), DateUtil.FORMAT_DATE_ddMMyyyy) : "") + "</b>" + "<br/><br/><br/>";;
    }

    private void getContentHeader() {
        contentAll += "1.  Tên Khách hàng/Applicant: "+details.get("applicant").getValue() + "  (”"+"Tôi/ Chúng tôi” trong Đề nghị này)/" +"<i>(“I/We” in this Application)</i>";
        contentAll += "<br/><br/>2.  Mã số doanh nghiệp/ GPHĐ /" +"<i>Enterprise Code/ License for Establishment & Operation : </i>"+ details.get("certCode").getValue()+"<br/><br/>  Cấp ngày/ <i>Date of issue </i>: " +details.get("certCodeDate").getValue();
        contentAll +="<br/><br/>Nơi cấp/<i>Place of issue : </i>"+ details.get("certCodePlace").getValue();
        // check loai bao lanh
        if (StringUtil.isNotEmpty(details.get("typeGuarantee").getValue())) {
            if (details.get("typeGuarantee").getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_1)) {
                typeGuarantee += "Bảo lãnh dự thầu" + "\n";
            } else if (details.get("typeGuarantee").getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_2)) {
                typeGuarantee += "Bảo lãnh thực hiện hợp đồng" + "\n";
            } else if (details.get("typeGuarantee").getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_3)) {
                typeGuarantee += "Bảo lãnh thanh toán" + "\n";
            } else if (details.get("typeGuarantee").getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_4)) {
                typeGuarantee += "Bảo lãnh tạm ứng" + "\n";
            } else if (details.get("typeGuarantee").getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_5)) {
                typeGuarantee += "Bảo lãnh bảo hành" + "\n";
            } else if (details.get("typeGuarantee").getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_6)) {
                typeGuarantee += "Bảo lãnh vay vốn" + "\n";
            } else if (details.get("typeGuarantee").getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_7)) {
                typeGuarantee += "Bảo lãnh thuế" + "\n";
            } else if (details.get("typeGuarantee").getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_8)) {
                typeGuarantee += details.get("otherGuarantee") != null ? details.get("otherGuarantee").getValue() : "" + "\n";
            } else if (details.get("typeGuarantee").getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_9)) {
                typeGuarantee += "Bảo lãnh nhà" + "\n";
            }
            contentAll +="<br/><br/><b>B.  Nội dung đề nghị/<i>Request</i>:</b><br/>" +
                    "Với mọi trách nhiệm về phía mình, chúng tôi đề nghị MSB phát hành bảo lãnh với nội dung chi tiết như sau/ <i>With our full responsibilities, we would like to request MSB to issue a bank guarantee with details as follows:</i>";
            contentAll +="<br/><br/><b>1.  Loại bảo lãnh</b>/<i>Type of Guarantee</i>: "+ typeGuarantee;
        }

        contentAll +="<br/><br/><b>2.  Nghĩa vụ được bảo lãnh và căn cứ phát sinh nghĩa vụ được bảo lãnh</b>/<i>Guaranteed obligations and underlying grounds for the guaranteed obligations: </i><br/>"+ details.get("formatTextGuarantee").getValue();
        contentAll +="<br/><br/><b>3.  Bên nhận bảo lãnh (Bên thụ hưởng)</b>/<i>(Beneficiary)</i>: " + details.get("beneficiary").getValue();
        contentAll +="<br/><br/>Địa chỉ/Address : " + details.get("beneficiaryAddress").getValue();

        if ("VND".equalsIgnoreCase(details.get("currency").getValue())
                || "JPY".equalsIgnoreCase(details.get("currency").getValue())) {
            DecimalFormat df = new DecimalFormat("#,##0");
            contentAll +="<br/><br/><b>4.  Số Tiền Bảo Lãnh, Đồng Tiền Bảo Lãnh</b>/<i>Guarantee Amount, Currency </i>:	"+ df.format(new BigDecimal(details.get("amount").getValue())) + " " +details.get("currency").getValue();
        } else {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            contentAll +="<br/><br/><b>4.  Số Tiền Bảo Lãnh, Đồng Tiền Bảo Lãnh</b>/<i>Guarantee Amount, Currency </i>:	"+ df.format(new BigDecimal(details.get("amount").getValue())) + " " +details.get("currency").getValue();
        }
        contentAll +="<br/><br/>(Bằng chữ)/ <i>(In words)</i>: "+ StringUtil.getStringAmount(details.get("amount").getValue(), details.get("currency").getValue()).toUpperCase();
        contentAll +="<br/><br/>(Bằng chữ tiếng Anh)/ <i>(In words)</i>: "+ StringUtil.getStringAmountEN(details.get("amount").getValue(), details.get("currency").getValue()).toUpperCase();

        contentAll +="<br/><br/><b>5.  Thời hạn hiệu lực của cam kết bảo lãnh</b>/<i>Validity Period of Guarantee</i>:<br/><br/>";
        contentAll += "Ngày/ Sự kiện bắt đầu hiệu lực: " + (details.get("startDate") != null ? details.get("startDate").getValue() : "")+ (details.get("startEventDate") != null ? details.get("startEventDate").getValue() : "");
        if (details.get("guaranteeEnd") != null) {
            if (details.get("guaranteeEnd").getValue().equalsIgnoreCase(AppConstant.GUARANTEE_END_EVENT)) {
                contentAll += "<br/><br/>Sự kiện hết hiệu lực: " + (details.get("endEventDate") != null ? details.get("endEventDate").getValue() : "");
            } else {
                contentAll += "<br/><br/>Ngày hết hiệu lực (dự kiến): " + (details.get("expiredDate") != null ? details.get("expiredDate").getValue() : "");
            }
            if(details.get("expectEndDate") != null){
                contentAll += "<br/><br/>Ngày hết hiệu lực (dự kiến): " + (details.get("expectEndDate") != null ? details.get("expectEndDate").getValue() : "");
            }
        }
        if (StringUtil.isNotEmpty(details.get("formGuarantee").getValue())) {
            if (details.get("formGuarantee").getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_1)) {
                contentAll += "<br/><br/><b>6.  Hình thức phát hành cam kết bảo lãnh</b>/<i>Form of guarantee commitment</i>: " + " Thư bảo lãnh";
            } else if (details.get("formGuarantee").getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_1)) {
                contentAll += "<br/><br/><b>6.  Hình thức phát hành cam kết bảo lãnh</b>/<i>Form of guarantee commitment</i>: " + " Hợp đồng bảo lãnh";
            } else {
                contentAll += "<br/><br/><b>6.  Hình thức phát hành cam kết bảo lãnh</b>/<i>Form of guarantee commitment</i>:" + " Điện swift";
            }
            contentAll +="<br/><br/><b>7.  Phương thức phát hành bảo lãnh</b>/<i>Method of guarantee issuance</i>: ";
            if (details.get("formGuarantee").getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_3)){
                BkSwiftInfo swiftInfo = bkSwiftInfoRepository.findBkSwiftInfoByswiftCode(details.get("swiftCode").getValue());
                if (swiftInfo != null){
                    contentAll += "SWIFT (Ngân hàng/<i>Bank: </i> " + swiftInfo.getSwiftName() +" Mã SWIFT/<i>Swift code</i>: "+swiftInfo.getSwiftCode();
                }
            } else {
                contentAll += "Văn bản giấy/<i>in writing </i> ";
            }
        }
        contentAll +="<br/><br/> <b>8. Mẫu cam kết bảo lãnh</b>/<i>Templates of guarantee commitment: </i> ";
        if (StringUtil.isNotEmpty(details.get("commitGuarantee").getValue())) {
            if (details.get("commitGuarantee").getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_1)){
                contentAll += "Theo mẫu của MSB/<i>As per MSB’s templates</i> ";
            } else if (details.get("commitGuarantee").getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_2)) {
                contentAll += "Theo mẫu của các cơ quan nhà nước có thẩm quyền/<i>As per authority’s templates (Specify the name of State authorities:…)</i>";
            } else {
                contentAll += "Theo mẫu chúng tôi cung cấp (đính kèm) được MSB chấp thuận/<i>As per applicant’s templates which are accepted by MSB</i>";
            }
        }
        contentAll +="<br/><br/><b>9.  Phí/ Fee: Theo quy định của MSB</b>(<i>As stipulated by MSB</i>)";
        contentAll +="<br/><br/>Phương thức thanh toán phí /<i>Fee payment method</i>: Trích nợ Tài khoản thanh toán số /<i>Debit from current account no</i>: "+ "<b>"+details.get("accountPayment").getValue()+"</b>";
        contentAll +="<br/><br/><b>D. Các nội dung khác/ <i>Other contents</i>: </b>";

        contentAll +="<br/><br/>Tôi/Chúng tôi cam kết <br/>" +
                "<i>I/We undertake that</i>:<br/>" +
                "-  Chấp hành đúng các quy định có liên quan của MSB và của pháp luật<br/>" +
                "<i> I/We will comply with all applicable laws and MSB regulations.</i>  <br/>" +
                "<br/>" +
                "-  Chịu trách nhiệm trước pháp luật và MSB về tính chính xác và chân thực của những thông tin, hồ sơ cung cấp cho MSB.<br/>" +
                "<i> I am/We are legally responsible and liable for the accuracy and truthfulness of the information provided herein. </i><br/>" +
                "<br/>" +
                "-   Vào ngày MSB thanh toán cho bên thụ hưởng theo yêu cầu, Đơn đề nghị phát hành bảo lãnh này sẽ được xem là sự xác nhận nợ của tôi/chúng tôi đối với MSB. Theo đó, MSB có quyền đơn phương quyết định việc  áp dụng các biện pháp cần thiết quy định tại Thỏa thuận cấp bảo lãnh hạn mức để thu hồi nợ phát sinh từ nghĩa vụ bảo lãnh.<br/>" +
                "<i>On the date MSB pays the Beneficiary upon presentation of a complying demand, this Application shall constitute my/our acknowledgement of debt to MSB, which shall, therefore, entitle MSB to take any measures deemed necessary in accordance with the terms of the relevant Guarantee Facility Agreement for the purpose of recovering any amount payable to it as a result of said payment.</i> <br/>" +
                "<br/>" +
                "-  Bằng việc đề nghị MSB phát hành bảo lãnh vô điều kiện, tôi/chúng tôi cam kết đã hiểu rõ rủi ro của bảo lãnh vô điều kiện là khi yêu cầu thực hiện nghĩa vụ bảo lãnh, Bên nhận bảo lãnh không phải chứng minh sự vi phạm của tôi/chúng tôi, MSB cũng không có nghĩa vụ và trách nhiệm phải xác minh sự vi phạm của tôi/chúng tôi. MSB sẽ thực hiện nghĩa vụ bảo lãnh nếu yêu cầu thực hiện nghĩa vụ bảo lãnh của Bên nhận bảo lãnh đáp ứng đầy đủ các điều kiện theo Cam kết bảo lãnh mà tôi/chúng tôi yêu cầu MSB phát hành theo Đơn đề nghị này. Tôi/Chúng tôi cam kết thanh toán vô điều kiện cho MSB khoản tiền MSB đã trả thay tôi/chúng tôi cho Bên nhận bảo lãnh theo nghĩa vụ bảo lãnh đã cam kết.<br/>" +
                "<i>By requesting MSB issue an unconditional guarantee, I/We undertake that I am/we are aware of the risk of unconditional guarantee in which the Beneficiary, when requesting MSB perform the guarantee obligations, will not be obligated to prove my/our failure to fulfill my/our obligations and MSB will not assume any duties and obligations with respect to the verification of such failure. MSB will perform the guarantee obligations upon the complying demand under the Guarantee which is requested to issue herein. I/We further unconditionally undertake to reimburse MSB for any and all amounts paid, on my/our behalf, to the Beneficiary in accordance with the terms of the Guarantee.</i> <br/>"
                + getCommitDefault()
                + getCommit2()
                + getCommit3()
                + getCommit4()
                + "<br/>-  Cam kết khác/ <i>other commitments</i>: "+(details.get("commitOtherContent") != null ? details.get("commitOtherContent").getValue() : "") +
                "<br/>"+"Đề nghị này nếu được MSB phê duyệt sẽ tự động tạo thành một bộ phận không thể tách rời của Hợp đồng tín dụng hạn mức và/hoặc Thỏa thuận cấp bảo lãnh hạn mức (Áp dụng với trường hợp bảo lãnh hạn mức)/ Thỏa thuận cấp bảo lãnh (Áp dụng đối với trường hợp  bảo lãnh từng lần) "+(details.get("contractNo") != null ?"<b>số: "+details.get("contractNo").getValue()+"</b>" : "")+" đã ký giữa Tôi/ chúng tôi và MSB. Nếu nội dung chấp thuận của MSB khác với nội dung mà tôi/chúng tôi đề nghị thì nội dung chấp thuận của MSB đương nhiên có giá trị thực hiện./<br/>"+
                "<i>This Application, if approved by MSB, will automatically constitute an integral part of the Factility/Credit Agreement and/or Bank Guarantee Limit Agreement (Applied for guarantee under a limit)/ Bank Guarantee Agreement (Applied for one time guarantee) "+(details.get("contractNo") != null ?"<b>No: "+details.get("contractNo").getValue()+"</b>" : "")+" signed between I/We and MSB. If MSB’s approval is different from our request, the former shall prevail.</i><br/>";

        contentAll +="<br/><br/><b>Tài liệu đính kèm/<i>Attachment</i>:</b><br/>" ;
        contentAll += fileAttachContent;
    }

    private byte[] convertByte(String serviceName, Map<String, Object> params) {
        byte[] bytes = commonService.generatePDFReport(serviceName, params);
        return bytes;
    }

    private String getCommitDefault() {
        StringBuilder str = new StringBuilder();
        if (isPresentParam(AppConstant.BB_GUARANTEE_FIELD.guaranteeEnd, "2")) {
            str.append("<br/>");
            str.append("-  Sau 01 năm nếu Bảo lãnh tiếp tục có hiệu lực thì MSB được toàn quyền thực hiện thu phí thêm 01 năm hoặc theo thời hạn khi đủ điều kiện tất toán Bảo lãnh, áp dụng theo biểu phí ban hành tại thời điểm thu phí.<br/>");
            str.append("<i>If, after one (01) year of the effective date, the guarantee is still valid, MSB may, and is entitled to, charge the Customer fees incurred for an additional one-(01)-year period or for a duration until the guarantee obligations have been fulfilled and charges apply as per the applicable schedule of fees and charges. </i><br/>");
        }
        return str.toString();
    }

    private String getCommit2() {
        StringBuilder str = new StringBuilder();
        if (isPresentParam(AppConstant.BB_GUARANTEE_FIELD.commit2, "Y")) {
            str.append("<br/>");
            str.append("-  Bổ sung Hợp đồng kinh tế chậm nhất ngày ").append(details.get("commit2Date").getValue()).append("<br/>");
            str.append("<i>The Customer shall send its business contract to MSB on ").append(details.get("commit2Date").getValue()).append("at latest.<br/>");
        }
        return str.toString();
    }

    private String getCommit3() {
        StringBuilder str = new StringBuilder();
        if (isPresentParam(AppConstant.BB_GUARANTEE_FIELD.commit3, "Y")) {
            str.append("<br/>");
            str.append("-  Hạn mức của chúng tôi tại MSB sẽ được sử dụng để phát hành bảo lãnh bảo đảm cho nghĩa vụ của các thành viên trong liên danh và chúng tôi cam kết sẽ thực hiện toàn bộ các khoản phải trả cho MSB liên quan tới bảo lãnh được phát hành, không phụ thuộc vào hành vi vi phạm phát sinh từ bất kỳ thành viên nào của liên danh.<br/>");
            str.append("<i>Our limit at MSB will be used to issue guarantees for the obligations of the members of the Joint venture, and we undertake to make all amounts payable to the MSB in connection with the underwriting issued, regardless of the breach arising from any member of the Joint venture. (Applicable in case the customer issues a guarantee for the whole joint venture.) </i><br/>");
        }
        return str.toString();
    }

    private String getCommit4() {
        StringBuilder str = new StringBuilder();
        if (isPresentParam(AppConstant.BB_GUARANTEE_FIELD.commit4, "Y")) {
            str.append("<br/>");
            str.append("-  Khi MSB, theo đề nghị của tôi/chúng tôi, chấp nhận phát hành Cam kết bảo lãnh có nội dung (hoặc nội dung tương tự): “Ngân hàng đồng ý các thay đổi, bổ sung hoặc điều chỉnh các điều kiện của Hợp đồng hoặc bất kỳ tài liệu nào liên quan đến Hợp đồng ký giữa Bên được bảo lãnh và Bên nhận bảo lãnh sẽ không làm thay đổi bất kỳ nghĩa vụ bảo lãnh của Ngân hàng theo Cam kết bảo lãnh cũng như hiệu lực của Cam kết bảo lãnh”, tôi/chúng tôi hiểu và đồng ý rằng các thay đổi, bổ sung hoặc điều chỉnh các điều kiện của Hợp đồng và/hoặc bất kỳ tài liệu nào liên quan đến Hợp đồng ký giữa Bên được bảo lãnh và Bên nhận bảo lãnh sẽ không làm thay đổi bất kỳ nghĩa vụ bảo lãnh của MSB theo Cam kết bảo lãnh cũng như không làm thay đổi bất kỳ nghĩa vụ nào của tôi/chúng tôi với MSB. Tôi/Chúng tôi theo đây cam kết chịu mọi trách nhiệm với MSB nếu có bất cứ tranh chấp nào xảy ra.<br/>");
            str.append("<i>In consideration Vietnam Maritime Commercial Joint Stock Bank (MSB), at my/our request, accepts to issue the Guarantee which includes the wordings (or similar to): “The Bank agree that any amendments, insertions or adjustments of the terms and conditions of the Contract and/or any documents relating to the Contract signed by and between The Beneficiary and the Applicant will not affect the Bank’s liabilities and obligations under the Guarantee as well as the effectiveness of the Guarantee”, I/we understand and agree that any amendments, insertions or adjustments of the terms and conditions of the Contract and/or any documents relating to the Contract signed by and between The Beneficiary and the Applicant will not affect the Bank’s liabilities and obligations under the Guarantee as well as will not affect my/our liabilities and obligations to MSB. We hereby undertake to be liable to MSB in case of any disputes. </i><br/>");
        }
        return str.toString();
    }

    private boolean isPresentParam(String key, String value) {
        GuaranteeHistoryDetailDto detailDto = details.get(key);
        return detailDto != null && value.equalsIgnoreCase(detailDto.getValue());
    }
}
