package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.utils.DateUtil;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.model.DetailGuarantee;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeRequest;
import com.msb.ibs.corp.cross.exchange.domain.entity.BkSwiftInfo;
import com.msb.ibs.corp.cross.exchange.domain.repository.BkSwiftInfoRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GuaranteeCreateStep1Service {
    private BkSwiftInfoRepository bkSwiftInfoRepository;
    private GuaranteeCommonService guaranteeCommonService;

    public GuaranteeCreateStep1Service(BkSwiftInfoRepository bkSwiftInfoRepository, GuaranteeCommonService guaranteeCommonService) {
        this.bkSwiftInfoRepository = bkSwiftInfoRepository;
        this.guaranteeCommonService = guaranteeCommonService;
    }

    public boolean validateStep1 (UserPrincipal principal, GuaranteeRequest guaranteeRequest) throws LogicException {
        if(guaranteeRequest.getListOrder() == null || guaranteeRequest.getListOrder().size() == 0)
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");

        //check han muc tu BPM
        guaranteeCommonService.checkLimit(principal.getCifNo(), null);

        boolean checkFormGuarantee = false;
        for (DetailGuarantee item : guaranteeRequest.getListOrder()) {
            switch (item.getParams()) {
                case AppConstant.BB_GUARANTEE_FIELD.applicant:
                    if (!"1".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");

                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    else {
                        if (item.getValue().length() > 500)
                            throw new LogicException("ERROR.GUARANTEE.APPLICANT.MAX.LENGTH");
                    }
                    break;
                case AppConstant.BB_GUARANTEE_FIELD.certCode:
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
                case AppConstant.BB_GUARANTEE_FIELD.certCodeDate:
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
                case AppConstant.BB_GUARANTEE_FIELD.certCodePlace:
                    if (!"4".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue())) {
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    } else {
                        if (item.getValue().length() > 100)
                            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.CERT.CODE.PLACE.MAX.LENGTH");
                    }
                    break;
                case AppConstant.BB_GUARANTEE_FIELD.formGuarantee:
                    if (!"5".equalsIgnoreCase(item.getSeqNo()))
                        throw new BadRequestException("ERROR.GUARANTEE.WRONG.SEQ");
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
                case AppConstant.BB_GUARANTEE_FIELD.swiftCode:
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
                case AppConstant.BB_GUARANTEE_FIELD.notificationTo:
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
                default:
                    throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
            }
        }
        return true;
    }

}
