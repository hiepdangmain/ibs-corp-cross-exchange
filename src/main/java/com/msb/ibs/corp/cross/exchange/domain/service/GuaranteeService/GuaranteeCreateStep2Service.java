package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.utils.DateUtil;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.model.DetailGuarantee;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeRequest;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocument;
import com.msb.ibs.corp.cross.exchange.domain.entity.BkSwiftInfo;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeDocumentRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BkSwiftInfoRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GuaranteeCreateStep2Service {

    private BbGuaranteeDocumentRepository documentRepository;

    public GuaranteeCreateStep2Service(BbGuaranteeDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public boolean validateStep2(UserPrincipal principal, GuaranteeRequest guaranteeRequest) throws LogicException {
        if (guaranteeRequest.getListOrder() == null || guaranteeRequest.getListOrder().size() == 0)
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        boolean checkOtherGuarantee = false;
        String checkGuaranteeStart = "";
        String checkGuaranteeEnd = "";
        String checkAmount = "";
        Date startDate = null;
        for (DetailGuarantee item : guaranteeRequest.getListOrder()) {
            switch (item.getParams()) {
                case AppConstant.BB_GUARANTEE_FIELD.typeGuarantee:
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
                    }
                    break;
                case AppConstant.BB_GUARANTEE_FIELD.otherGuarantee:
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
                case AppConstant.BB_GUARANTEE_FIELD.beneficiary:
                    if (!"10".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    else {
                        // Bên nhận bảo lãnh
                        if (item.getSeqNo().length() > 200)
                            throw new LogicException("ERROR.GUARANTEE.BENEFICIARY.MAX.LENGTH");
                    }
                    break;
                case AppConstant.BB_GUARANTEE_FIELD.beneficiaryAddress:
                    if (!"11".equalsIgnoreCase(item.getSeqNo()))
                        throw new BadRequestException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue())) {
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    } else {
                        // Địa chỉ Bên nhận bảo lãnh
                        if (item.getValue().length() > 200)
                            throw new LogicException("ERROR.GUARANTEE.BENEFICIARY.ADDRESS.MAX.LENGTH");
                    }
                    break;
                case AppConstant.BB_GUARANTEE_FIELD.amount:
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
                            } else if (item.getValue().length() > 15)
                                throw new LogicException("ERROR.GUARANTEE.AMOUNT.MAX.LENGTH");
                        }
                    }
                    checkAmount = item.getValue();
                    break;
                case AppConstant.BB_GUARANTEE_FIELD.currency:
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
                        if (!StringUtil.isRegexMatches(checkAmount, StringUtil.regexNumber)) {
                            throw new LogicException("ERROR.GUARANTEE.AMOUNT.INVALID");
                        }
                    }
                    break;
                case AppConstant.BB_GUARANTEE_FIELD.guaranteeStart:
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
                case AppConstant.BB_GUARANTEE_FIELD.startDate:
                    if (!"15".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if(checkGuaranteeStart.equalsIgnoreCase(AppConstant.GUARANTEE_START_DATE))
                        if(StringUtil.isEmpty(item.getValue()))
                            throw new LogicException("ERROR.GUARANTEE.START.DATE.MISS.PARAM");
                        else {
                            Date date = DateUtil.getEndTimeOfDate(item.getValue(), DateUtil.FORMAT_DATE_ddMMyyyy);
                            if (date == null)
                                throw new LogicException("ERROR.GUARANTEE.START.DATE.WRONG");
                            if (date.compareTo(new Date()) < 0) {
                                throw new LogicException("ERROR.GUARANTEE.START.DATE.WRONG");
                            }
                            startDate = date;
                        }
                    break;
                case AppConstant.BB_GUARANTEE_FIELD.startEventDate:
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
                case AppConstant.BB_GUARANTEE_FIELD.guaranteeEnd: //Kết thúc hiệu lực
                    if (!"17".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    if(!(item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_END_DATE)||
                            item.getValue().equalsIgnoreCase(AppConstant.GUARANTEE_END_EVENT)))
                        throw new LogicException("ERROR.GUARANTEE.END.INVALID");
                    checkGuaranteeEnd = item.getValue();
                    break;
                case AppConstant.BB_GUARANTEE_FIELD.expiredDate: //Ngày hết hạn hiệu lực
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
                case AppConstant.BB_GUARANTEE_FIELD.endEventDate:
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
                case AppConstant.BB_GUARANTEE_FIELD.expectEndDate:
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
                case AppConstant.BB_GUARANTEE_FIELD.formatTextGuarantee:
                    if (!"21".equalsIgnoreCase(item.getSeqNo()))
                        throw new LogicException("ERROR.GUARANTEE.WRONG.SEQ");
                    if (StringUtil.isEmpty(item.getValue()))
                        throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
                    if(item.getValue().length() > 500)
                        throw new LogicException("ERROR.GUARANTEE.FORMAT.TEXT.MAX.LENGTH");
                    break;
                default:
                    throw new LogicException("ERROR.GUARANTEE.REQUEST.INVALID");
            }
            }
            return true;
        }

    }


