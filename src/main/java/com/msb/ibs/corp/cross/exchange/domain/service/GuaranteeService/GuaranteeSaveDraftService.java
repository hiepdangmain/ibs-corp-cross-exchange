package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.utils.DateUtil;
import com.msb.ibs.common.utils.KeyGenerator;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.enums.AttachmentTypeEnum;
import com.msb.ibs.corp.cross.exchange.application.model.DetailGuarantee;
import com.msb.ibs.corp.cross.exchange.application.model.DocumentGuarantee;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeRequest;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeDetailResponse;
import com.msb.ibs.corp.cross.exchange.domain.dto.DocumentGuaranteeDto;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeHistoryDetailDto;
import com.msb.ibs.corp.cross.exchange.domain.entity.*;
import com.msb.ibs.corp.cross.exchange.domain.repository.*;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeSaveDraftService {

    private BbGuaranteeHistoryRepository guaranteeHistoryRepository;

    private BbGuaranteeHistoryDetailRepository guaranteeHistoryDetailRepository;

    private BbGuaranteeFileAttachmentRepository fileAttachmentRepository;

    private GuaranteeCommonService guaranteeCommonService;

    private BkSwiftInfoRepository bkSwiftInfoRepository;

    private BbGuaranteeDocumentRepository documentRepository;

    private BbGuaranteeDocumentConfigRepository guaranteeDocumentConfigRepository;

    private String tranSn ;

    private BbGuaranteeHistory bbGuaranteeHistory = null;

    private String typeGua = "";

    private String amount = "";

    private String currency = "";

    private String commitGuarantee = "";

    private String commit2 = "";

    private String applicant = "";

    private List<Integer> listDocumentId = new ArrayList<>();

    private List<Integer> listId = new ArrayList<>();

    private GuaranteeDetailResponse output = new GuaranteeDetailResponse();

    private String receiveGuarantee = "";

    public GuaranteeSaveDraftService(BbGuaranteeHistoryRepository guaranteeHistoryRepository,
                                     BbGuaranteeHistoryDetailRepository guaranteeHistoryDetailRepository,
                                     BbGuaranteeFileAttachmentRepository fileAttachmentRepository,
                                     GuaranteeCommonService guaranteeCommonService,
                                     BkSwiftInfoRepository bkSwiftInfoRepository,
                                     BbGuaranteeDocumentRepository documentRepository,
                                     BbGuaranteeDocumentConfigRepository guaranteeDocumentConfigRepository) {
        this.guaranteeHistoryRepository = guaranteeHistoryRepository;
        this.guaranteeHistoryDetailRepository = guaranteeHistoryDetailRepository;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.guaranteeCommonService = guaranteeCommonService;
        this.bkSwiftInfoRepository = bkSwiftInfoRepository;
        this.documentRepository = documentRepository;
        this.guaranteeDocumentConfigRepository = guaranteeDocumentConfigRepository;
    }

    public GuaranteeDetailResponse execute(GuaranteeRequest guaranteeRequest, UserPrincipal principal) throws LogicException {
        // check la ghi moi hay chinh sua
        if(StringUtil.isEmpty(guaranteeRequest.getTranSn())) {
            tranSn = KeyGenerator.singleton.getKey();
        } else {
            bbGuaranteeHistory = guaranteeHistoryRepository.selectBytranSn(guaranteeRequest.getTranSn());
            if (bbGuaranteeHistory == null)
                throw new BadRequestException("ERROR.GUARANTEE.CREATE.WRONG.TRAN.SN");
            // check neu la chinh sua thi can dung user tao
            if (!(AppConstant.STATUS.DRAF.equalsIgnoreCase(bbGuaranteeHistory.getStatus())
                    || AppConstant.STATUS.UPDA.equalsIgnoreCase(bbGuaranteeHistory.getStatus())
                    || bbGuaranteeHistory.getUserId().equals(principal.getUserId()))) {
                throw new BadRequestException("ERROR.GUARANTEE.CREATE.WRONG.USER.AND.STATUS.TRAN.SN");
            }
            tranSn = guaranteeRequest.getTranSn();
        }
        /*if (StringUtil.isEmpty(guaranteeRequest.getNewTranSn())) {
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        }
        tranSn = guaranteeRequest.getNewTranSn();
        if (StringUtil.isNotEmpty(guaranteeRequest.getTranSn())) {
            bbGuaranteeHistory = guaranteeHistoryRepository.selectBytranSn(guaranteeRequest.getTranSn());
            if (bbGuaranteeHistory == null)
                throw new BadRequestException("ERROR.GUARANTEE.CREATE.WRONG.TRAN.SN");
            // check neu la chinh sua thi can dung user tao
            if (!(AppConstant.STATUS.DRAF.equalsIgnoreCase(bbGuaranteeHistory.getStatus())
                    || AppConstant.STATUS.UPDA.equalsIgnoreCase(bbGuaranteeHistory.getStatus())
                    || bbGuaranteeHistory.getUserId().equals(principal.getUserId()))) {
                throw new BadRequestException("ERROR.GUARANTEE.CREATE.WRONG.USER.AND.STATUS.TRAN.SN");
            }
            tranSn = guaranteeRequest.getTranSn();
        }*/

        validateStep1(guaranteeRequest, principal);
        if (guaranteeRequest.getDocuments().size() > 0) {
            // truoc khi update tai lieu dua len thi update ve newR
            fileAttachmentRepository.updateAllDocumentByStatus(tranSn, AppConstant.STATUS_RECORD.NEWR, "N");
            validateDocument(guaranteeRequest.getDocuments(), guaranteeRequest, principal);
        }
        saveData(guaranteeRequest, principal);

        return output;
    }

    private void validateStep1(GuaranteeRequest guaranteeRequest, UserPrincipal principal) throws LogicException {
        boolean checkFormGuarantee = false;
        boolean checkOtherGuarantee = false;
        String checkGuaranteeStart = "";
        String checkGuaranteeEnd = "";
        boolean checkCommit2 = false;
        String checkContentCommit = "";
        Date startDate = null;
        for (DetailGuarantee item : guaranteeRequest.getListOrder()) {
            switch (item.getParams()) {
                case "applicant":
                    if (!"1".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");

                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (item.getValue().length() > 500)
                            throw new LogicException("ERROR.GUARANTEE.APPLICANT.MAX.LENGTH");
                    }
                    applicant = item.getValue();
                    break;
                case "certCode":
                    if (!"2".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        // check So DKKD
                        if (!guaranteeCommonService.checkCertCode(principal.getCifNo(), item.getValue()))
                            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.CERT.CODE.WRONG");
                    }
                    break;
                case "certCodeDate":
                    if (!"3".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        Date date = DateUtil.getEndTimeOfDate(item.getValue(), DateUtil.FORMAT_DATE_ddMMyyyy);
                        Date today = null;
                        try {
                            today = DateUtil.getEndTimeOfDate(new Date());
                        } catch (Exception e) {
                            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.CERT.CODE.DATE.WRONG");
                        }
                        //validate date k duoc qua ngay hien tai
                        if (date == null)
                            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.CERT.CODE.DATE.WRONG");
                        if (date.compareTo(today) > 0) {
                            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.CERT.CODE.DATE.WRONG");
                        }
                    }
                    break;
                case "certCodePlace":
                    if (!"4".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (item.getValue().length() > 100)
                            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.CERT.CODE.PLACE.MAX.LENGTH");
                    }
                    break;
                case "formGuarantee":
                    if (!"5".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (!(item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_1)
                                || item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_2)
                                || item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_3)))
                            throw new LogicException("ERROR.GUARANTEE.FORM.INVALID");
                        if (item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_2)) {
                            checkFormGuarantee = true;
                        }
                        commitGuarantee = item.getValue();
                    }
                    break;
                case "swiftCode":
                    if (!"6".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkFormGuarantee) {
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            BkSwiftInfo swiftInfo = bkSwiftInfoRepository.findBkSwiftInfoByswiftCode(item.getValue());
                            if (swiftInfo == null)
                                throw new LogicException("ERROR.GUARANTEE.SWIFT.INFO.INVALID");
                        }
                    }
                    break;
                case "notificationTo":
                    if (!"7".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    /*if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");*/

                    String[] listData = item.getValue().split("##");
                    /*if (listData.length == 1) {
                        if (!(listData[0].equalsIgnoreCase(AppConstant.NOTIFY_MAKER) || listData[0].equalsIgnoreCase(AppConstant.NOTIFY_CHECKER)))
                            throw new LogicException("ERROR.GUARANTEE.NOTIFY.INVALID");
                    } else if (listData.length == 2) {
                        if (!(listData[0].equalsIgnoreCase(AppConstant.NOTIFY_PARTNER)))
                            throw new LogicException("ERROR.GUARANTEE.NOTIFY.INVALID");
                    }*/
                    break;
                case "typeGuarantee":
                    if (!"8".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");

                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (!(item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_1)
                                || item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_2)
                                || item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_3)
                                || item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_4)
                                || item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_5)
                                || item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_6)
                                || item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_7)
                                || item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_8)
                                || item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_9)))
                            throw new LogicException("ERROR.GUARANTEE.TYPE.INVALID");
                        if (item.getValue().equalsIgnoreCase(AppConstant.TYPE_GUARANTEE_8)) {
                            checkOtherGuarantee = true;
                        }
                        typeGua = item.getValue();
                    }
                    break;
                case "otherGuarantee":
                    if (!"9".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");

                    if (checkOtherGuarantee) {
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            if (item.getValue().length() > 100)
                                throw new LogicException("ERROR.GUARANTEE.TYPE.OTHER.MAX.LENGTH");
                        }
                    }
                    break;
                case "beneficiary":
                    if (!"10".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        // Bên nhận bảo lãnh
                        if (item.getSeqNo().length() > 200)
                            throw new LogicException("ERROR.GUARANTEE.BENEFICIARY.MAX.LENGTH");
                    }
                    receiveGuarantee = item.getValue();
                    break;
                case "beneficiaryAddress":
                    if (!"11".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        // Địa chỉ Bên nhận bảo lãnh
                        if (item.getValue().length() > 200)
                            throw new LogicException("ERROR.GUARANTEE.BENEFICIARY.ADDRESS.MAX.LENGTH");
                    }
                    break;
                case "amount":
                    if (!"12".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        // Số tiền
                        if (!StringUtil.isRegexMatches(item.getValue(), StringUtil.regexNumberHaveDot)) {
                            throw new LogicException("ERROR.GUARANTEE.AMOUNT.INVALID");
                        } else {
                            if (item.getValue().contains(".")) {
                                if (item.getValue().length() > 17)
                                    throw new LogicException("ERROR.GUARANTEE.AMOUNT.MAX.LENGTH");
                            } else if (item.getValue().length() > 16)
                                throw new LogicException("ERROR.GUARANTEE.AMOUNT.MAX.LENGTH");
                        }
                        amount = item.getValue();
                    }
                    break;
                case "currency":
                    if (!"13".equalsIgnoreCase(item.getSeqNo())) {
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    }
                    List<BbGuaranteeDocument> findBbGuaranteeDocumentByType = documentRepository.findBbGuaranteeDocumentByType("CURRENCY");
                    boolean checkCurrency = false;
                    for (BbGuaranteeDocument currency : findBbGuaranteeDocumentByType) {
                        if (currency.getLabelVn().equalsIgnoreCase(item.getValue())) {
                            checkCurrency = true;
                            break;
                        }
                    }
                    if (!checkCurrency)
                        throw new LogicException("ERROR.GUARANTEE.WRONG.CURRENCY");
                    // check k dc nhap so thap phan cua VND , JPY
                    if ("VND".equalsIgnoreCase(item.getValue())
                            || "JPY".equalsIgnoreCase(item.getValue())) {
                        if (!StringUtil.isRegexMatches(amount, StringUtil.regexNumber)) {
                            throw new LogicException("ERROR.GUARANTEE.AMOUNT.INVALID");
                        }
                    }
                    currency = item.getValue();
                    break;
                case "guaranteeStart":
                    if (!"14".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        //Bắt đầu hiệu lực
                        if(!(item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_START_DATE)||
                                item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_START_EVENT)))
                            throw new LogicException("ERROR.GUARANTEE.START.INVALID");
                        checkGuaranteeStart = item.getValue();
                    }

                    break;
                case "startDate":
                    if (!"15".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if(checkGuaranteeStart.equalsIgnoreCase(AppConstant.GUARANTEE_START_DATE))
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            Date date = DateUtil.getEndTimeOfDate(item.getValue(), DateUtil.FORMAT_DATE_ddMMyyyy);
                            if (date == null)
                                throw new LogicException("ERROR.GUARANTEE.START.DATE.WRONG");
                            //validate date k duoc chon ngay qua khu
                            if (date.compareTo(new Date()) < 0) {
                                throw new LogicException("ERROR.GUARANTEE.START.DATE.WRONG");
                            }
                            startDate = date;
                        }
                    break;
                case "startEventDate":
                    if (!"16".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkGuaranteeStart.equalsIgnoreCase(AppConstant.GUARANTEE_START_EVENT)) {
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            if (item.getValue().length() > 1000)
                                throw new LogicException("ERROR.GUARANTEE.START.EVENT.DATE.MAX.LENGTH");
                        }
                    }
                    break;
                case "guaranteeEnd": //Kết thúc hiệu lực
                    if (!"17".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if(!(item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_END_DATE)||
                                item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_END_EVENT)))
                            throw new LogicException("ERROR.GUARANTEE.END.INVALID");
                    }
                    checkGuaranteeEnd = item.getValue();
                    break;
                case "expiredDate": //Ngày hết hạn hiệu lực
                    if (!"18".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkGuaranteeEnd.equalsIgnoreCase(AppConstant.GUARANTEE_END_DATE)) {
                        Date date = DateUtil.getEndTimeOfDate(item.getValue(), DateUtil.FORMAT_DATE_ddMMyyyy);
                        if (date == null)
                            throw new LogicException("ERROR.GUARANTEE.EXPIRED.DATE.WRONG");
                        //validate date k duoc chon ngay qua khu
                        if (date.compareTo(new Date()) < 0) {
                            throw new LogicException("ERROR.GUARANTEE.EXPIRED.DATE.WRONG");
                        }
                        if (startDate != null) {
                            if (date.compareTo(startDate) < 0) {
                                throw new LogicException("ERROR.GUARANTEE.EXPIRED.DATE.WRONG");
                            }
                        }
                    }
                    break;
                case "endEventDate":
                    if (!"19".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkGuaranteeEnd.equalsIgnoreCase(AppConstant.GUARANTEE_END_EVENT)) {
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            if (item.getValue().length() > 2000)
                                throw new LogicException("ERROR.GUARANTEE.END.EVENT.DATE.MAX.LENGTH");
                        }
                    }
                    break;
                case "expectEndDate":
                    if (!"20".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkGuaranteeEnd.equalsIgnoreCase(AppConstant.GUARANTEE_END_EVENT)) {
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            Date date = DateUtil.getEndTimeOfDate(item.getValue(), DateUtil.FORMAT_DATE_ddMMyyyy);
                            if (date == null)
                                throw new LogicException("ERROR.GUARANTEE.EXPECT.DATE.WRONG");
                            //validate date k duoc chon ngay qua khu
                            if (date.compareTo(new Date()) < 0) {
                                throw new LogicException("ERROR.GUARANTEE.EXPECT.DATE.WRONG");
                            }
                            if (startDate != null) {
                                if (date.compareTo(startDate) < 0) {
                                    throw new LogicException("ERROR.GUARANTEE.EXPIRED.DATE.WRONG");
                                }
                            }
                        }
                    }

                    break;
                case "formatTextGuarantee":
                    if (!"21".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if(item.getValue().length() > 500)
                            throw new LogicException("ERROR.GUARANTEE.FORMAT.TEXT.MAX.LENGTH");
                    }

                    break;

                case "commitGuarantee": // Mẫu cam kết bảo lãnh
                    if (!"22".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        // check mẫu cam kết bảo lãnh
                        if (!(item.getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_1)
                                || item.getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_2)
                                || item.getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_3)))
                            throw new BadRequestException("ERROR.GUARANTEE.COMMIT.FORM.INVALID");
                    }

                    break;
                case "contractNo": // Số Hợp đồng tín dụng
                    if (!"23".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    // so hop dong
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (item.getValue().length() > 100)
                            throw new LogicException("ERROR.GUARANTEE.CONTRACT.NO.MAX.LENGTH");
                    }
                    break;
                case "accountPayment"://Tài khoản thanh toán phí
                    if (!"24".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    // check stk
//                    AccountPermissionDto rolloutAccountInfo = getAccountPermission(item.getValue(), principal, token);;
//                    if (rolloutAccountInfo == null) {
//                        throw new LogicException("ACCOUNT.IS.NOT.BELONG.TO");
//                    }
                    break;
                case "commit1"://Cam kết 1
                    if (!"25".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        // check cam ket 1
                        if (!(item.getValue().equalsIgnoreCase("Y")
                                || item.getValue().equalsIgnoreCase("N")))
                            throw new LogicException("ERROR.GUARANTEE.COMMIT1.INVALID");
                    }

                    break;
                case "commit2"://Cam kết 2
                    if (!"26".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    // check cam ket 2
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (!(item.getValue().equalsIgnoreCase("Y")
                                || item.getValue().equalsIgnoreCase("N"))) {
                            throw new LogicException("ERROR.GUARANTEE.COMMIT2.INVALID");
                        } else {
                            if (item.getValue().equalsIgnoreCase("Y")) {
                                checkCommit2 = true;
                            }
                        }
                        commit2 = item.getValue();
                    }
                    break;
                case "commit2Date"://Ngày Bổ sung hợp đồng
                    if (!"27".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkCommit2) {
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            Date date = DateUtil.stringToDate(item.getValue(), DateUtil.FORMAT_DATE_ddMMyyyy);
                            if (date == null)
                                throw new LogicException("ERROR.GUARANTEE.COMMIT2.DATE.WRONG");
                            if(DateUtil.getDaysBetween2Dates(new Date(), date ) > 30 || DateUtil.getDaysBetween2Dates(new Date(), date ) < 0)
                                throw new LogicException("ERROR.GUARANTEE.COMMIT2.DATE.WRONG");
                        }
                    }
                    break;
                case "commit3"://Cam kết 3
                    if (!"28".equalsIgnoreCase(item.getSeqNo()))
                        throw new BadRequestException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (!(item.getValue().equalsIgnoreCase("Y")
                                || item.getValue().equalsIgnoreCase("N"))) {
                            throw new LogicException("ERROR.GUARANTEE.COMMIT3.INVALID");
                        }
                    }
                    break;
                case "commitOther"://Cam kết khác
                    if (!"29".equalsIgnoreCase(item.getSeqNo()))
                        throw new BadRequestException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (!(item.getValue().equalsIgnoreCase("Y")
                                || item.getValue().equalsIgnoreCase("N"))) {
                            throw new LogicException("ERROR.GUARANTEE.COMMIT.OTHER.INVALID");
                        } else {
                            if (item.getValue().equalsIgnoreCase("Y"))
                                checkContentCommit = item.getValue();
                        }
                    }
                    break;
                case "commitOtherContent"://Noi dung Cam kết khac
                    if (!"30".equalsIgnoreCase(item.getSeqNo()))
                        throw new BadRequestException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkContentCommit.equalsIgnoreCase("Y")){
                        if (StringUtil.isNotEmpty(item.getValue())) {
                            if (item.getValue().length() > 500)
                                throw new LogicException("ERROR.GUARANTEE.COMMIT.OTHER.MAX.LENGTH");
                        }
                    }
                    break;
                case "commit4"://Cam kết 4
                    if (!"31".equalsIgnoreCase(item.getSeqNo()))
                        throw new BadRequestException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (!(item.getValue().equalsIgnoreCase("Y")
                                || item.getValue().equalsIgnoreCase("N"))) {
                            throw new LogicException("ERROR.GUARANTEE.COMMIT4.INVALID");
                        }
                    }
                    break;
                case "commitDefault"://Bổ sung vào phần Cam kết Mặc định
                    if (AppConstant.GUARANTEE_END_EVENT.equalsIgnoreCase(checkGuaranteeEnd)) {
                        if (!"32".equalsIgnoreCase(item.getSeqNo())) {
                            throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                        }
                        if (StringUtil.isEmpty(item.getValue()) || !"Y".equalsIgnoreCase(item.getValue())) {
                            throw new LogicException("ERROR.GUARANTEE.COMMIT.DEFAULT.MISS.PARAM");
                        }
                    }
                    break;
                default:
                    throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
            }
        }
    }


    private void validateDocument(List<DocumentGuarantee> documents, GuaranteeRequest guaranteeRequest, UserPrincipal principal) throws LogicException {
        Map<Integer, DocumentGuarantee> listIdsUpload = new HashMap<>();
        for (DocumentGuarantee item : documents) {
            listIdsUpload.put(item.getDocumentId(), item);
            listId.add(item.getId());
            listDocumentId.add(item.getDocumentId());
        }
        if (listDocumentId.contains(2))
            throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.DOCUMENT.ID.IS.VALID");
        // lay list danh sach file dinh kem
        List<String> listFile = new ArrayList<>();
        listFile.add("0");
        listFile.add(typeGua);
        List<BbGuaranteeDocumentConfig> listDocumentConfig = guaranteeDocumentConfigRepository.findListDocumentConfig(listFile);
        List<Integer> listDb = listDocumentConfig.stream().map(e -> e.getSeqNo()).collect(Collectors.toList());

        // get list Dc phep no
        List<BbGuaranteeDocumentConfig> listDocumentConfigDebt = listDocumentConfig.stream().filter(b -> "Y".equalsIgnoreCase(b.getDebt())).collect(Collectors.toList());
        List<Integer> listDebtDb = listDocumentConfigDebt.stream().map(e -> e.getSeqNo()).collect(Collectors.toList());

        // check file Upload vs file trong DB xem da dung chua
        for (Map.Entry<Integer, DocumentGuarantee> e : listIdsUpload.entrySet()) {
            Integer id = e.getKey();
            if (!listDb.contains(id))
                throw new LogicException("ERROR.GUARANTEE.FILE_ATTACH.INVALID");

        }
        // check file input
        for (DocumentGuarantee item : documents) {
            if (StringUtil.isEmpty(item.getFileName())) {
                if (StringUtil.isNotEmpty(item.getDebt())) {
                    if (!(item.getDebt().equalsIgnoreCase("Y") || item.getDebt().equalsIgnoreCase("N")))
                        throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.INPUT.IS.VALID");
                    if (item.getDebt().equalsIgnoreCase("N")) {
                        if (StringUtil.isNotEmpty(item.getDebtDate())) {
                            throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.INPUT.IS.VALID");
                        }
                    } else {
                        if (StringUtil.isEmpty(item.getDebtDate())) {
                            throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.INPUT.IS.VALID");
                        }
                    }
                    // insert file no
                    List<BbGuaranteeFileAttachment> listFileDebt = new ArrayList<>();
                    if (StringUtil.isNotEmpty(item.getDebtDate())) {
                        BbGuaranteeFileAttachment fileAttachment = new BbGuaranteeFileAttachment();
                        fileAttachment.setDocumentId(item.getDocumentId());
                        fileAttachment.setAttachmentType(AttachmentTypeEnum.SERVER.value());
                        fileAttachment.setCorpId(principal.getCorpId());
                        fileAttachment.setStatus(AppConstant.STATUS_RECORD.DPEN);
                        fileAttachment.setCreateBy(principal.getUserId());
                        fileAttachment.setCreateTime(new Date());
                        fileAttachment.setDebt(item.getDebt());
                        fileAttachment.setDebtDate(item.getDebtDate());
                        fileAttachment.setTransRefer(tranSn);
//                        fileAttachment.setChoose("Y");
                        listFileDebt.add(fileAttachment);
                    }
                    if (listFileDebt.size() > 0) {
                        fileAttachmentRepository.saveAll(listFileDebt);
                    }
                }
            } else {
                if ("Y".equalsIgnoreCase(item.getDebt()) || StringUtil.isNotEmpty(item.getDebtDate())) {
                    throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.INPUT.IS.VALID");
                }
            }
        }

    }

    private List<Integer> getListMandatory(GuaranteeRequest guaranteeRequest) {
        List<Integer> listMandatory = null;
        if (!commitGuarantee.equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_1)) {
            listMandatory.add(1);
        }
        if (applicant.toLowerCase().contains("liên danh")) {
            listMandatory.add(3);
        }
        switch (typeGua) {
            case AppConstant.TYPE_GUARANTEE_1:
                listMandatory.add(5);
                break;
            case AppConstant.TYPE_GUARANTEE_2:
                if ("N".equalsIgnoreCase(commit2))
                    listMandatory.add(6);
                if ("Y".equalsIgnoreCase(commit2))
                    listMandatory.add(7);
                break;
            case AppConstant.TYPE_GUARANTEE_3:
                if ("N".equalsIgnoreCase(commit2))
                    listMandatory.add(8);
                break;
            case AppConstant.TYPE_GUARANTEE_4:
                if ("N".equalsIgnoreCase(commit2)) {
                    listMandatory.add(11);
                }
                if ("Y".equalsIgnoreCase(commit2))
                    listMandatory.add(7);
                break;
            case AppConstant.TYPE_GUARANTEE_5:
                if ("N".equalsIgnoreCase(commit2))
                    listMandatory.add(10);
                break;
            default:
                break;
        }
        return listMandatory;
    }

    private void saveData(GuaranteeRequest guaranteeRequest, UserPrincipal principal) {
        Date createTime = new Date();
        // save bang history
        initOrderTransaction (guaranteeRequest, principal);
        saveGuaranteeHistory(createTime, guaranteeRequest);
        // save bang history detail
        if (StringUtil.isNotEmpty(guaranteeRequest.getTranSn())) {
            int a = guaranteeHistoryDetailRepository.deleteByTranRefer(guaranteeRequest.getTranSn());
        }
        saveGuaranteeHistoryDetail(createTime, guaranteeRequest, principal);
        // save document
        if(guaranteeRequest.getDocuments().size() > 0){
            saveDocument(guaranteeRequest.getDocuments());
        } else {
            // Neu xoa het file thi update ve trang thai NEWR
            fileAttachmentRepository.updateAllDocumentByStatus(tranSn, AppConstant.STATUS_RECORD.NEWR,"N");
        }
    }


    private void initOrderTransaction(GuaranteeRequest guaranteeRequest, UserPrincipal principal) {
        if (StringUtil.isEmpty(guaranteeRequest.getTranSn())) {
            bbGuaranteeHistory = new BbGuaranteeHistory();
        }
        bbGuaranteeHistory.setTranSn(tranSn);
        if(StringUtil.isNotEmpty(amount))
            bbGuaranteeHistory.setAmount(new BigDecimal(amount));
        bbGuaranteeHistory.setCreateBy(principal.getUserId());
        bbGuaranteeHistory.setCorpId(principal.getCorpId());
        bbGuaranteeHistory.setCifNo(principal.getCifNo());
        if(StringUtil.isNotEmpty(typeGua))
            bbGuaranteeHistory.setTypeGuarantee(typeGua);
        if(StringUtil.isNotEmpty(currency))
            bbGuaranteeHistory.setCurrency(currency);
        if(StringUtil.isNotEmpty(receiveGuarantee))
            bbGuaranteeHistory.setReceiveGuarantee(receiveGuarantee);
        bbGuaranteeHistory.setCustomerMkName(principal.getUsername());
        bbGuaranteeHistory.setUserId(principal.getUserId());
        bbGuaranteeHistory.setFee(guaranteeRequest.getFee());
    }

    private void saveGuaranteeHistory(Date createTime, GuaranteeRequest guaranteeRequest) {
        if(StringUtil.isEmpty(guaranteeRequest.getTranSn())) {
            bbGuaranteeHistory.setCreateTime(createTime);
        }
        if (!AppConstant.STATUS.UPDA.equalsIgnoreCase(bbGuaranteeHistory.getStatus())) {
            bbGuaranteeHistory.setStatus(AppConstant.STATUS.DRAF);
        }
        bbGuaranteeHistory.setRequestType(AppConstant.GUARANTEE_HISTORY.REQUEST_TYPE_NEW);
        bbGuaranteeHistory.setTransnActive(AppConstant.GUARANTEE_HISTORY.ACTIVE);
        guaranteeHistoryRepository.save(bbGuaranteeHistory);
        output.setGuaranteeHistory(bbGuaranteeHistory);
    }

    private void saveGuaranteeHistoryDetail(Date createTime, GuaranteeRequest request, UserPrincipal principal) {
        List<BbGuaranteeHistoryDetail> listDetail = new ArrayList<>();
        List<GuaranteeHistoryDetailDto> listDetailDto = new ArrayList<>();
        for (DetailGuarantee guarantee : request.getListOrder()){
            BbGuaranteeHistoryDetail detail = new BbGuaranteeHistoryDetail();
            GuaranteeHistoryDetailDto detailDto = new GuaranteeHistoryDetailDto();
            detail.setTranRefer(tranSn);
            detail.setCreateBy(principal.getUserId());
            detail.setCorpId(principal.getCorpId());
            detail.setCreateTime(new Date());
            detail.setParam(guarantee.getParams());
            detail.setValue(guarantee.getValue());
            detail.setSeqNo(guarantee.getSeqNo());

            detailDto.setParam(guarantee.getParams());
            detailDto.setValue(guarantee.getValue());
            detailDto.setSeqNo(guarantee.getSeqNo());

            listDetail.add(detail);
            listDetailDto.add(detailDto);
        }
        guaranteeHistoryDetailRepository.insert(listDetail);
        output.setDetails(listDetailDto);
    }

    private void saveDocument(List<DocumentGuarantee> documents) {
        fileAttachmentRepository.updateAllDocumentById(listId, AppConstant.STATUS_RECORD.DPEN, tranSn);
        List<DocumentGuaranteeDto> listDto = new ArrayList<>();
        for (DocumentGuarantee item : documents){
            DocumentGuaranteeDto documentGuaranteeDto = new DocumentGuaranteeDto();
            BeanUtils.copyProperties(item, documentGuaranteeDto);
            listDto.add(documentGuaranteeDto);
        }
        output.setDocuments(listDto);
    }
}
