package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.utils.BeanUtil;
import com.msb.ibs.common.utils.DateUtil;
import com.msb.ibs.common.utils.KeyGenerator;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.enums.AttachmentTypeEnum;
import com.msb.ibs.corp.cross.exchange.application.enums.WorkflowEnum;
import com.msb.ibs.corp.cross.exchange.application.model.DetailGuarantee;
import com.msb.ibs.corp.cross.exchange.application.model.DocumentGuarantee;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeRequest;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeCondition;
import com.msb.ibs.corp.cross.exchange.domain.entity.*;
import com.msb.ibs.corp.cross.exchange.domain.repository.*;
import com.msb.ibs.corp.cross.exchange.domain.service.BpmService;
import com.msb.ibs.corp.cross.exchange.domain.service.Common.CommonService;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import com.msb.ibs.corp.main.service.domain.base.BaseStepOneToConfirm;
import com.msb.ibs.corp.main.service.domain.dto.AccountPermissionDto;
import com.msb.ibs.corp.main.service.domain.input.AuthTransactionCreateInput;
import com.msb.ibs.corp.main.service.domain.output.AuthTransactionCreateOutput;
import com.msb.ibs.corp.main.service.domain.output.StepOneToConfirmOutput;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeCreateService extends BaseStepOneToConfirm<GuaranteeRequest, StepOneToConfirmOutput> {

    private CommonService commonService;

    private BbGuaranteeDocumentConfigRepository guaranteeDocumentConfigRepository;

    private GuaranteeCommonService guaranteeCommonService;

    private BkSwiftInfoRepository bkSwiftInfoRepository;

    private String tranSn ;

    private BbGuaranteeHistory bbGuaranteeHistory = null;

    private BbGuaranteeHistoryRepository guaranteeHistoryRepository;

    private BbGuaranteeHistoryDetailRepository guaranteeHistoryDetailRepository;

    private GuaranteeRequest request = null;

    private BbGuaranteeDocumentRepository documentRepository;

    private String typeGua = "";

    private String amount = "";

    private String currency = "";

    private String commitGuarantee = "";

    private String commit2 = "";

    private String applicant = "";

    private InternalRequest infoUser = null;

    private String oldStatus = "";

    private BbGuaranteeFileAttachmentRepository fileAttachmentRepository;

    private List<Integer> listId = new ArrayList<>();

    private List<Integer> listDocumentId = new ArrayList<>();

    private String receiveGuarantee = "";
    private final BpmService bpmService;
    private final GuaranteeSyncService guaranteeSyncService;

    public GuaranteeCreateService(CommonService commonService,
                                  BbGuaranteeDocumentConfigRepository guaranteeDocumentConfigRepository,
                                  GuaranteeCommonService guaranteeCommonService,
                                  BkSwiftInfoRepository bkSwiftInfoRepository,
                                  BbGuaranteeHistoryRepository guaranteeHistoryRepository,
                                  BbGuaranteeHistoryDetailRepository guaranteeHistoryDetailRepository,
                                  BbGuaranteeDocumentRepository documentRepository,
                                  BbGuaranteeFileAttachmentRepository fileAttachmentRepository,
                                  BpmService bpmService,
                                  GuaranteeSyncService guaranteeSyncService) {
        this.commonService = commonService;
        this.guaranteeDocumentConfigRepository = guaranteeDocumentConfigRepository;
        this.guaranteeCommonService = guaranteeCommonService;
        this.bkSwiftInfoRepository = bkSwiftInfoRepository;
        this.guaranteeHistoryRepository = guaranteeHistoryRepository;
        this.guaranteeHistoryDetailRepository = guaranteeHistoryDetailRepository;
        this.documentRepository = documentRepository;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.bpmService = bpmService;
        this.guaranteeSyncService = guaranteeSyncService;
    }

    @Override
    protected void validate(GuaranteeRequest guaranteeRequest, InternalRequest internalRequest) throws LogicException {

        if (guaranteeRequest.getListOrder() == null) {
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        }
        String typeGuarantee = null;
        for (DetailGuarantee detailGuarantee: guaranteeRequest.getListOrder()) {
            if (AppConstant.BB_GUARANTEE_FIELD.typeGuarantee.equals(detailGuarantee.getParams())) {
                typeGuarantee = detailGuarantee.getValue();
                break;
            }
        }
//        if (guaranteeRequest.getDocuments().size() == 0 && !AppConstant.TYPE_GUARANTEE_4.equals(typeGuarantee)) {
//            throw new LogicException("ERROR.GUARANTEE.FILE_ATTACH.MANDATORY.MISS");
//        }
        // check la ghi moi hay chinh sua
        if(StringUtil.isEmpty(guaranteeRequest.getTranSn())) {
            tranSn = KeyGenerator.singleton.getKey();
        } else {
            bbGuaranteeHistory = guaranteeHistoryRepository.selectBytranSn(guaranteeRequest.getTranSn());
            if (bbGuaranteeHistory == null)
                throw new BadRequestException("ERROR.GUARANTEE.CREATE.WRONG.TRAN.SN");
            // check tranSN
            tranSn = guaranteeRequest.getTranSn();
            oldStatus = guaranteeRequest.getTranSn();
            // neu la trang thai chinh sua thi sinh ra tran SN moi
            if(AppConstant.STATUS.UPDA.equalsIgnoreCase(bbGuaranteeHistory.getStatus())) {
                tranSn = KeyGenerator.singleton.getKey();
            }
            // check neu la chinh sua thi can dung user tao
            if (!((AppConstant.STATUS.DRAF.equalsIgnoreCase(bbGuaranteeHistory.getStatus())
                    || AppConstant.STATUS.UPDA.equalsIgnoreCase(bbGuaranteeHistory.getStatus()))
                    && bbGuaranteeHistory.getUserId().equals(internalRequest.getUserPrincipal().getUserId())))
                throw new BadRequestException("ERROR.GUARANTEE.CREATE.WRONG.USER.AND.STATUS.TRAN.SN");
        }

        //check han muc tu BPM
        //guaranteeCommonService.checkLimit(internalRequest.getUserPrincipal().getCifNo(), tranSn);

        /*// TH back ve tu MH confirm thi xoa het cac du lieu tu man create (Do tranSn dc sinh ra truoc)
        BbGuaranteeHistory bbGuaranteeHistoryNew = guaranteeHistoryRepository.selectBytranSn(guaranteeRequest.getNewTranSn());
        if (bbGuaranteeHistoryNew != null) {
            //delete ban ghi do
            guaranteeHistoryRepository.deleteByByTranSn(bbGuaranteeHistoryNew.getTranSn());
            guaranteeHistoryDetailRepository.deleteByTranRefer(bbGuaranteeHistoryNew.getTranSn());
        }
        // check la ghi moi hay chinh sua
        if(StringUtil.isEmpty(guaranteeRequest.getTranSn())) {
            tranSn = guaranteeRequest.getNewTranSn();
        } else {
            bbGuaranteeHistory = guaranteeHistoryRepository.selectBytranSn(guaranteeRequest.getTranSn());
            if (bbGuaranteeHistory == null)
                throw new BadRequestException("ERROR.GUARANTEE.CREATE.WRONG.TRAN.SN");
            // check tranSN
            oldStatus = guaranteeRequest.getTranSn();
            tranSn = guaranteeRequest.getTranSn();
            // neu la trang thai chinh sua thi sinh ra tran SN moi
            if(AppConstant.STATUS.UPDA.equalsIgnoreCase(bbGuaranteeHistory.getStatus())) {
                tranSn = guaranteeRequest.getNewTranSn();
            }
            // check neu la chinh sua thi can dung user tao
            if (!((AppConstant.STATUS.DRAF.equalsIgnoreCase(bbGuaranteeHistory.getStatus())
                    || AppConstant.STATUS.UPDA.equalsIgnoreCase(bbGuaranteeHistory.getStatus()))
                    && bbGuaranteeHistory.getUserId().equals(internalRequest.getUserPrincipal().getUserId())))
                throw new BadRequestException("ERROR.GUARANTEE.CREATE.WRONG.USER.AND.STATUS.TRAN.SN");
        }*/
    }

    @Override
    protected void processBusiness(GuaranteeRequest guaranteeRequest, InternalRequest internalRequest) throws LogicException {
        infoUser = internalRequest;
        validateStep1(guaranteeRequest, internalRequest.getUserPrincipal(),internalRequest.getToken());
        validateDocument(guaranteeRequest.getDocuments(), guaranteeRequest);
        initOrderTransaction(guaranteeRequest, internalRequest);
        request = guaranteeRequest;

        //check so phi du thu
        /*BbGuaranteeHistoryDetail guaranteeHistoryDetail = guaranteeHistoryDetailRepository
                .selectByTranSnAndParam(bbGuaranteeHistory.getTranSn(), AppConstant.BB_GUARANTEE_FIELD.accountPayment);
        if (guaranteeHistoryDetail != null) {
            GuaranteeFeeRequest guaranteeFeeRequest = new GuaranteeFeeRequest();
            guaranteeFeeRequest.setTranSn(bbGuaranteeHistory.getTranSn());
            guaranteeFeeRequest.setFeeAccount(guaranteeHistoryDetail.getValue());
            GuaranteeFeeResponse response = guaranteeCommonService.checkFee(guaranteeFeeRequest, internalRequest.getToken());
            if (response != null && response.isBlock() && !response.isFeeValid()) {
                throw new LogicException("ERROR.GUARANTEE.FEE.GREATER.THAN.AVAILABLE.BALANCE");
            }
        }*/
    }

    @Override
    protected AuthTransactionCreateInput setupParamAuthorizedTransaction(UserPrincipal userPrincipal) {
        return commonService.buildAuthorizedTransactionInput(userPrincipal, tranSn,
                bbGuaranteeHistory.getAmount(), WorkflowEnum.GUARANTEE_CROSS_EXCHANGE.value(), bbGuaranteeHistory.getCurrency(), GuaranteeConfirmService.class.getName());
    }

    @Override
    protected void saveData() {
        Date createTime = new Date();
        // save bang history
        saveGuaranteeHistory(createTime);
        // save bang history detail
        saveGuaranteeHistoryDetail(createTime);
        // save document
        saveDocument(request.getDocuments(), bbGuaranteeHistory);

    }

    @Override
    protected StepOneToConfirmOutput buildResponse(AuthTransactionCreateOutput authTransactionCreateOutput) {
        return StepOneToConfirmOutput.builder().authTransactionOutput(authTransactionCreateOutput)
                .objectInfo(bbGuaranteeHistory).build();
    }

    private void validateStep1(GuaranteeRequest guaranteeRequest, UserPrincipal principal, String token) throws LogicException {
        boolean checkFormGuarantee = false;
        boolean checkOtherGuarantee = false;
        String checkGuaranteeStart = "";
        String checkGuaranteeEnd = "";
        boolean checkCommit2 = false;
        boolean checkCommit2Date = false;
        String checkContentCommit = "";
        Date startDate = null;
        for (DetailGuarantee item : guaranteeRequest.getListOrder()) {
            switch (item.getParams()) {
                case "applicant":
                    if (!"1".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");

                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    else {
                        if (item.getValue().length() > 500)
                            throw new LogicException("ERROR.GUARANTEE.APPLICANT.MAX.LENGTH");
                    }
                    applicant = item.getValue();
                    break;
                case "certCode":
                    if (!"2".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    else {
                        // check So DKKD
                        if (!guaranteeCommonService.checkCertCode(principal.getCifNo(), item.getValue()))
                            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.CERT.CODE.WRONG");
                    }
                    break;
                case "certCodeDate":
                    if (!"3".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    else {
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
                    if (StringUtil.isEmpty(item.getValue())) {
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    } else {
                        if (item.getValue().length() > 100)
                            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.CERT.CODE.PLACE.MAX.LENGTH");
                    }
                    break;
                case "formGuarantee":
                    if (!"5".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue())) {
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    } else {
                        if (!(item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_1)
                                || item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_2)
                                || item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_3)))
                            throw new LogicException("ERROR.GUARANTEE.FORM.INVALID");
                        if (item.getValue().equalsIgnoreCase(AppConstant.FORM_GUARANTEE_3)) {
                            checkFormGuarantee = true;
                        }
                    }
                    break;
                case "swiftCode":
                    if (!"6".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkFormGuarantee) {
                        if (StringUtil.isEmpty(item.getValue())) {
                            throw new LogicException("ERROR.GUARANTEE.SWIFT.INFO.MISS.PARAM");
                        } else {
                            BkSwiftInfo swiftInfo = bkSwiftInfoRepository.findBkSwiftInfoByswiftCode(item.getValue());
                            if (swiftInfo == null)
                                throw new LogicException("ERROR.GUARANTEE.SWIFT.INFO.INVALID");
                        }
                    }
                    break;
                case "notificationTo":
                    if (!"7".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    String[] listData = item.getValue().split("##");
                    if (listData.length > 0){
                        for (String s : listData ){
                            if (!(AppConstant.NOTIFY_MAKER.equalsIgnoreCase(s) || AppConstant.NOTIFY_CHECKER.equalsIgnoreCase(s)))
                                throw new BadRequestException("ERROR.GUARANTEE.NOTIFY.INVALID");
                        }
                    }
                    break;
                case "typeGuarantee":
                    if (!"8".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");

                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    else {
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
                        if (StringUtil.isEmpty(item.getValue()))
                            throw new LogicException("ERROR.GUARANTEE.TYPE.OTHER.INVALID");
                        else {
                            if (item.getValue().length() > 100)
                                throw new LogicException("ERROR.GUARANTEE.TYPE.OTHER.MAX.LENGTH");
                        }
                    }
                    break;
                case "beneficiary":
                    if (!"10".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    else {
                        // Bên nhận bảo lãnh
                        if (item.getSeqNo().length() > 200)
                            throw new LogicException("ERROR.GUARANTEE.BENEFICIARY.MAX.LENGTH");
                    }
                    receiveGuarantee = item.getValue();
                    break;
                case "beneficiaryAddress":
                    if (!"11".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue())) {
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    } else {
                        // Địa chỉ Bên nhận bảo lãnh
                        if (item.getValue().length() > 200)
                            throw new LogicException("ERROR.GUARANTEE.BENEFICIARY.ADDRESS.MAX.LENGTH");
                    }
                    break;
                case "amount":
                    if (!"12".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue())) {
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    } else {
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
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    //Bắt đầu hiệu lực
                    if(!(item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_START_DATE)||
                            item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_START_EVENT)))
                        throw new LogicException("ERROR.GUARANTEE.START.INVALID");
                    checkGuaranteeStart = item.getValue();
                    break;
                case "startDate":
                    if (!"15".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkGuaranteeStart.equalsIgnoreCase(AppConstant.GUARANTEE_START_DATE))
                        if (StringUtil.isEmpty(item.getValue()))
                            throw new LogicException("ERROR.GUARANTEE.START.DATE.MISS.PARAM");
                        else {
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
                        if (StringUtil.isEmpty(item.getValue()))
                            throw new LogicException("ERROR.GUARANTEE.START.EVENT.DATE.MISS.PARAM");
                        else {
                            if (item.getValue().length() > 1000)
                                throw new LogicException("ERROR.GUARANTEE.START.EVENT.DATE.MAX.LENGTH");
                        }
                    }
                    break;
                case "guaranteeEnd": //Kết thúc hiệu lực
                    if (!"17".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    if(!(item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_END_DATE)||
                            item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_END_EVENT)))
                        throw new LogicException("ERROR.GUARANTEE.END.INVALID");
                    checkGuaranteeEnd = item.getValue();
                    break;
                case "expiredDate": //Ngày hết hạn hiệu lực
                    if (!"18".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkGuaranteeEnd.equalsIgnoreCase(AppConstant.GUARANTEE_END_DATE)) {
                        if (StringUtil.isEmpty(item.getValue()))
                            throw new LogicException("ERROR.GUARANTEE.EXPIRED.DATE.MISS.PARAM");
                        else {
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
                    }
                    break;
                case "endEventDate":
                    if (!"19".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkGuaranteeEnd.equalsIgnoreCase(AppConstant.GUARANTEE_END_EVENT)) {
                        if (StringUtil.isEmpty(item.getValue()) )
                            throw new LogicException("ERROR.GUARANTEE.END.EVENT.DATE.AND.EXPECT.END.DATE.MISS.PARAM");
                        else {
                            if (item.getValue().length() > 2000)
                                throw new LogicException("ERROR.GUARANTEE.END.EVENT.DATE.MAX.LENGTH");
                        }
                    }
                    break;
                case "expectEndDate":
                    if (!"20".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (checkGuaranteeEnd.equalsIgnoreCase(AppConstant.GUARANTEE_END_EVENT)) {
                        if (StringUtil.isEmpty(item.getValue()))
                            throw new LogicException("ERROR.GUARANTEE.END.EVENT.DATE.AND.EXPECT.END.DATE.MISS.PARAM");
                        else {
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
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    if(item.getValue().length() > 500)
                        throw new LogicException("ERROR.GUARANTEE.FORMAT.TEXT.MAX.LENGTH");
                    break;

                case "commitGuarantee": // Mẫu cam kết bảo lãnh
                    if (!"22".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    // check mẫu cam kết bảo lãnh
                    if (!(item.getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_1)
                            || item.getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_2)
                            || item.getValue().equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_3)))
                        throw new BadRequestException("ERROR.GUARANTEE.COMMIT.FORM.INVALID");
                    commitGuarantee = item.getValue();
                    break;
                case "contractNo": // Số Hợp đồng tín dụng
                    if (!"23".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    // so hop dong
                    if (StringUtil.isNotEmpty(item.getValue())) {
                        if (item.getValue().length() > 100)
                            throw new LogicException("ERROR.GUARANTEE.CONTRACT.NO.MAX.LENGTH");
                    }
                    break;
                case "accountPayment"://Tài khoản thanh toán phí
                    if (!"24".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    // check stk
                    AccountPermissionDto rolloutAccountInfo = getAccountPermission(item.getValue(), principal, token);;
                    if (rolloutAccountInfo == null) {
                        throw new LogicException("ERROR.GUARANTEE.ACCOUNT.FEE.IS.NOT.BELONG.TO");
                    }
                    break;
                case "commit1"://Cam kết 1
                    if (!"25".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    // check cam ket 1
                    if (!(item.getValue().equalsIgnoreCase("Y")
                            || item.getValue().equalsIgnoreCase("N")))
                        throw new LogicException("ERROR.GUARANTEE.COMMIT1.INVALID");
                    if (!item.getValue().equalsIgnoreCase("Y")){
                        throw new LogicException("ERROR.GUARANTEE.COMMIT1.WRONG");
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
                    checkCommit2Date = true;
                    if (checkCommit2) {
                        if (StringUtil.isEmpty(item.getValue()))
                            throw new LogicException("ERROR.GUARANTEE.COMMIT2.DATE.MISS.PARAM");
                        else {
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
                        if (StringUtil.isEmpty(item.getValue())) {
                            throw new LogicException("ERROR.GUARANTEE.COMMIT.OTHER.INVALID");
                        } else {
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
        // check neu k nhap commit date thi bao loi
        if (checkCommit2) {
            if (!checkCommit2Date) {
                throw new LogicException("ERROR.GUARANTEE.COMMIT2.DATE.WRONG");
            }
        }
    }

    private void validateDocument(List<DocumentGuarantee> documents, GuaranteeRequest guaranteeRequest) throws LogicException {
        Map<Integer, DocumentGuarantee> listIdsUpload = new HashMap<>();
        for (DocumentGuarantee item : documents) {
            listIdsUpload.put(item.getDocumentId(), item);
            listId.add(item.getId());
            listDocumentId.add(item.getDocumentId());
        }
        // check k dc upload file tai lieu pdf bao cao BL
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
        // check file bat buoc
        List<Integer> listMandatory = getListMandatory(guaranteeRequest);
        if (listMandatory != null) {
//            for(Integer item : listMandatory) {
//                if (!listDocumentId.contains(item))
//                    throw new LogicException("ERROR.GUARANTEE.FILE_ATTACH.MANDATORY.MISS");
//            }
            // check file được nợ
            for (Map.Entry<Integer, DocumentGuarantee> e : listIdsUpload.entrySet()) {
                Integer id = e.getKey();
                DocumentGuarantee document = e.getValue();
                if (!listDebtDb.contains(id)) {
                    // neu file k dc no thi k dc truyen len
                    if (StringUtil.isNotEmpty(document.getDebtDate()))
                        throw new LogicException("ERROR.GUARANTEE.FILE_ATTACH.DEBT.INVALID");
                } else {
                    if (StringUtil.isNotEmpty(document.getDebtDate())) {
                        if (!"Y".equalsIgnoreCase(document.getDebt()))
                            throw new LogicException("ERROR.GUARANTEE.FILE_ATTACH.DEBT.INPUT.INVALID");
                        if (StringUtil.isEmpty(document.getDebtDate()))
                            throw new LogicException("ERROR.GUARANTEE.FILE_ATTACH.DEBT.DATE.INPUT.MISS");
                        Date date = DateUtil.getEndTimeOfDate(document.getDebtDate(), DateUtil.FORMAT_DATE_ddMMyyyy);
                        if (date == null)
                            throw new BadRequestException("ERROR.GUARANTEE.FILE_ATTACH.DEBT.DATE.WRONG");
                        try {
                            if (date.compareTo(DateUtil.getEndTimeOfDate(new Date())) < 0) {
                                throw new LogicException("ERROR.GUARANTEE.DEBT.WRONG");
                            }
                        } catch (Exception e1) {
                            throw new LogicException("ERROR.GUARANTEE.DEBT.WRONG");
                        }
                    }
                }
            }
        }

    }

    private List<Integer> getListMandatory(GuaranteeRequest guaranteeRequest) {
        List<Integer> listMandatory = new ArrayList<>();
        if (!commitGuarantee.equalsIgnoreCase(AppConstant.COMMIT_GUARANTEE_1)) {
            listMandatory.add(1);
        }
        /*if (applicant.toLowerCase().contains("liên danh")) {
            listMandatory.add(3);
        }*/
        if (StringUtil.isEmpty(commit2)) {
            commit2 = "N";
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
                /*if ("N".equalsIgnoreCase(commit2))
                    listMandatory.add(8);*/
                listMandatory.add(8);
                break;
            case AppConstant.TYPE_GUARANTEE_4:
                if ("N".equalsIgnoreCase(commit2)) {
                    listMandatory.add(11);
                }
                /*if ("Y".equalsIgnoreCase(commit2))
                    listMandatory.add(12);*/
                break;
            case AppConstant.TYPE_GUARANTEE_5:
                /*if ("N".equalsIgnoreCase(commit2))
                    listMandatory.add(10);*/
                listMandatory.add(10);
                break;
            case AppConstant.TYPE_GUARANTEE_6:
                /*if ("N".equalsIgnoreCase(commit2))
                    listMandatory.add(10);*/
                listMandatory.add(14);
                break;
            case AppConstant.TYPE_GUARANTEE_7:
                /*if ("N".equalsIgnoreCase(commit2))
                    listMandatory.add(10);
                listMandatory.add(13);*/
                break;
            case AppConstant.TYPE_GUARANTEE_8:
                /*if ("N".equalsIgnoreCase(commit2))
                    listMandatory.add(10);*/
                listMandatory.add(16);
                break;
            case AppConstant.TYPE_GUARANTEE_9:
                listMandatory.add(20);
                break;
            default:
                break;
        }
        return listMandatory;
    }

    private AccountPermissionDto getAccountPermission(String accountNo, UserPrincipal userPrincipal, String token) throws LogicException {
        return commonService.getAccountPermission(accountNo, userPrincipal.getCorpId(), userPrincipal.getUserId(), userPrincipal.getRoleId(), token);
    }
    private void initOrderTransaction(GuaranteeRequest guaranteeRequest, InternalRequest internalRequest) {
        if(StringUtil.isEmpty(guaranteeRequest.getTranSn())) {
            bbGuaranteeHistory = new BbGuaranteeHistory();
            bbGuaranteeHistory.setTranSn(tranSn);
            bbGuaranteeHistory.setStatus(AppConstant.STATUS.NEWR);
        }
        bbGuaranteeHistory.setAmount(new BigDecimal(amount));
        bbGuaranteeHistory.setCreateBy(internalRequest.getUserPrincipal().getUserId());
        bbGuaranteeHistory.setUserId(internalRequest.getUserPrincipal().getUserId());
        bbGuaranteeHistory.setCorpId(internalRequest.getUserPrincipal().getCorpId());
        bbGuaranteeHistory.setCorpName(internalRequest.getUserPrincipal().getCorpName());
        bbGuaranteeHistory.setCifNo(internalRequest.getUserPrincipal().getCifNo());
        bbGuaranteeHistory.setTypeGuarantee(typeGua);
        bbGuaranteeHistory.setCurrency(currency);
        bbGuaranteeHistory.setReceiveGuarantee(receiveGuarantee);
        bbGuaranteeHistory.setExpiredDate(DateUtil.addDate(new Date(), 7)); // set 7 ngay cho duyet
        bbGuaranteeHistory.setApplicant(applicant);
        bbGuaranteeHistory.setFee(guaranteeRequest.getFee());
        request = guaranteeRequest;
    }

    private void saveGuaranteeHistory(Date createTime) {
        bbGuaranteeHistory.setRequestType(AppConstant.GUARANTEE_HISTORY.REQUEST_TYPE_NEW);
        if(StringUtil.isEmpty(request.getTranSn())) {
            bbGuaranteeHistory.setCreateTime(createTime);
            bbGuaranteeHistory.setTransnActive(AppConstant.GUARANTEE_HISTORY.ACTIVE);
            guaranteeHistoryRepository.save(bbGuaranteeHistory);
        } else {
            // chia thanh 2  case tao lenh tu luu nhap + hoac yeu cau chinh sua
            if(AppConstant.STATUS.UPDA.equalsIgnoreCase(bbGuaranteeHistory.getStatus())){
                BbGuaranteeHistory newRecord = new BbGuaranteeHistory();
                BeanUtil.copyNonNullProperties(bbGuaranteeHistory, newRecord);
                newRecord.setTranSn(tranSn);
                newRecord.setCustomerSendTime(null);
                newRecord.setCustomerCkName(null);
                newRecord.setRejectContent(null);
                newRecord.setListCkApprove(null);
                newRecord.setAssigneeCk(null);
                newRecord.setUpdateBy(null);
                newRecord.setUpdateTime(null);
                newRecord.setTransnActive(AppConstant.GUARANTEE_HISTORY.IN_ACTIVE);
                // check xem dang la chinh sua lan1 hay lan n
                GuaranteeCondition condition = new GuaranteeCondition();
                condition.setTranSn(request.getTranSn());
                List<BbGuaranteeHistory> listData = guaranteeHistoryRepository.findByCondiiton(condition, infoUser.getUserPrincipal().getCorpId());
                if(listData != null) {
                    if(StringUtil.isEmpty(listData.get(0).getTransnRefer())){
                        newRecord.setTransnRefer(oldStatus);
                    } else {
                        newRecord.setTransnRefer(listData.get(0).getTransnRefer());
                    }
                }
                guaranteeHistoryRepository.save(newRecord);
            } else if (AppConstant.STATUS.DRAF.equalsIgnoreCase(bbGuaranteeHistory.getStatus())){
                guaranteeHistoryRepository.updateByPrimaryKeySelective(bbGuaranteeHistory);
            }
        }
    }

    private void saveGuaranteeHistoryDetail(Date createTime) {
        List<BbGuaranteeHistoryDetail> listDetail = new ArrayList<>();
        for (DetailGuarantee guarantee : request.getListOrder()){
            BbGuaranteeHistoryDetail detail = new BbGuaranteeHistoryDetail();
            detail.setTranRefer(tranSn);
            detail.setCreateBy(infoUser.getUserPrincipal().getUserId());
            detail.setCorpId(infoUser.getUserPrincipal().getCorpId());
            detail.setCreateTime(new Date());
            detail.setParam(guarantee.getParams());
            detail.setValue(guarantee.getValue());
            detail.setSeqNo(guarantee.getSeqNo());
            listDetail.add(detail);
        }
        guaranteeHistoryDetailRepository.insert(listDetail);
    }

    private void saveDocument(List<DocumentGuarantee> documents, BbGuaranteeHistory bbGuaranteeHistory) {
        System.out.println("QuyenCV2 saveDocument listId : " +listId.size());
        System.out.println("QuyenCV2 saveDocument tranSn : " +tranSn);
        if(bbGuaranteeHistory.getStatus().equalsIgnoreCase(AppConstant.STATUS.UPDA)) {
            // TH tao 1 ban ghi xong quay lai man hinh confirm thi can xoa all cac tai lieu luu trc do
            fileAttachmentRepository.updateAllDocumentByStatus(tranSn, AppConstant.STATUS_RECORD.NEWR,"N");
            // tao 1 list file moi vs ma tranSn moi
            List<BbGuaranteeFileAttachment> listFileNew = new ArrayList<>();
            List<BbGuaranteeFileAttachment> listFile = fileAttachmentRepository.selectBylistId(listId);
            if (listFile.size() > 0) {
                BbGuaranteeFileAttachment fileAttachment = null;
                for (BbGuaranteeFileAttachment file : listFile) {
                    fileAttachment = new BbGuaranteeFileAttachment();
                    fileAttachment.setTransRefer(tranSn);
                    fileAttachment.setFileName(file.getFileName());
                    fileAttachment.setDocumentId(file.getDocumentId());
                    fileAttachment.setPathFile(file.getPathFile());
                    fileAttachment.setAttachmentType(file.getAttachmentType());
                    fileAttachment.setCorpId(file.getCorpId());
                    fileAttachment.setStatus(AppConstant.STATUS_RECORD.DPEN);
                    fileAttachment.setCreateBy(bbGuaranteeHistory.getUserId());
                    fileAttachment.setCreateTime(new Date());
                    fileAttachment.setDebt(file.getDebt());
                    fileAttachment.setDebtDate(file.getDebtDate());
//                    fileAttachment.setChoose("Y");
                    listFileNew.add(fileAttachment);
                }
                fileAttachmentRepository.saveAll(listFileNew);
            }
        } else if (bbGuaranteeHistory.getStatus().equalsIgnoreCase(AppConstant.STATUS.DRAF)) {
            fileAttachmentRepository.updateAllDocumentByStatus(tranSn, AppConstant.STATUS_RECORD.NEWR, "N");
            fileAttachmentRepository.updateAllDocumentById(listId, AppConstant.STATUS_RECORD.DPEN, tranSn);
        } else {
            fileAttachmentRepository.updateAllDocumentById(listId, AppConstant.STATUS_RECORD.DPEN, tranSn);
        }
        // insert các file fhi no
        for (DocumentGuarantee item : documents) {
            // insert file no
            List<BbGuaranteeFileAttachment> listFileDebt = new ArrayList<>();
            if (StringUtil.isNotEmpty(item.getDebtDate())) {
                BbGuaranteeFileAttachment fileAttachment = new BbGuaranteeFileAttachment();
                fileAttachment.setDocumentId(item.getDocumentId());
                fileAttachment.setAttachmentType(AttachmentTypeEnum.SERVER.value());
                fileAttachment.setCorpId(infoUser.getUserPrincipal().getCorpId());
                fileAttachment.setStatus(AppConstant.STATUS_RECORD.DPEN);
                fileAttachment.setCreateBy(infoUser.getUserPrincipal().getUserId());
                fileAttachment.setCreateTime(new Date());
                fileAttachment.setDebt(item.getDebt());
                fileAttachment.setDebtDate(item.getDebtDate());
                fileAttachment.setTransRefer(tranSn);
//                fileAttachment.setChoose("Y");
                listFileDebt.add(fileAttachment);
            }
            if (listFileDebt.size() > 0) {
                fileAttachmentRepository.saveAll(listFileDebt);
            }
        }
    }

}
