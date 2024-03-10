package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.base.Query;
import com.msb.ibs.common.enums.MailType;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.utils.ResponseUtils;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeUpdateStatusRequest;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeHistoryDetailDto;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeLog;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeLogRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tiep nhan, tu choi, yeu cau cap nhat vs hoan thanh ho so
 * (Tich hop BPM).
 *
 * @author HaiNQ3
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeUpdateStatusV2Service implements Query<GuaranteeUpdateStatusRequest, ResponseEntity<?>> {

    @Autowired
    private BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository;
    @Autowired
    private BbGuaranteeLogRepository bbGuaranteeLogRepository;
    @Autowired
    private BbGuaranteeHistoryDetailRepository bbGuaranteeHistoryDetailRepository;
    @Autowired
    private GuaranteeCommonService guaranteeCommonService;
    @Autowired
    private GuaranteeMetaDataService guaranteeMetaDataService;

    private BbGuaranteeHistory guaranteeHistory;
    private Map<String, GuaranteeHistoryDetailDto> details;
    private String mailTemplate;
    private String mailType;
    //private String email;

    @Override
    public void validate(GuaranteeUpdateStatusRequest request) {
        if (StringUtil.isEmpty(request.getTranSn())) {
            throw new BadRequestException("ERROR.GUARANTEE.TRAN_SN.REQUIRED");
        }
        if ((AppConstant.BPM_ACTION.RECEIVE.equalsIgnoreCase(request.getAction())
                || AppConstant.BPM_ACTION.TRANSFER.equalsIgnoreCase(request.getAction()))
                && StringUtil.isEmpty(request.getBpmUser())) {
            throw new BadRequestException("ERROR.GUARANTEE.BPM_USER.REQUIRED");
        }
        guaranteeHistory = bbGuaranteeHistoryRepository.getTranSnActive(request.getTranSn());
        if (guaranteeHistory == null) {
            throw new BadRequestException("ERROR.GUARANTEE.TRAN_SN.NOT.EXIST");
        }
        if (AppConstant.STATUS.FAIL.equals(guaranteeHistory.getStatus())) {
            throw new BadRequestException("ERROR.GUARANTEE.STATUS.INVALID");
        }
        //Hoan thanh: trang thai dang xu ly
        /*if (AppConstant.BPM_ACTION.COMPLETE.equalsIgnoreCase(request.getAction())
                && !AppConstant.STATUS.DPEN.equalsIgnoreCase(guaranteeHistory.getStatus())) {
            throw new BadRequestException("ERROR.GUARANTEE.STATUS.NOT.PROCESSING");
        }
        //Tiep nhan: trang thai cho tiep nhan || cho duyet chinh sua
        if (AppConstant.BPM_ACTION.RECEIVE.equalsIgnoreCase(request.getAction())
                && !AppConstant.STATUS.PEND.equalsIgnoreCase(guaranteeHistory.getStatus())
                && !AppConstant.STATUS.DUPD.equalsIgnoreCase(guaranteeHistory.getStatus())) {
            throw new BadRequestException("ERROR.GUARANTEE.STATUS.NOT.WAIT_RECEIVE.UPDATED");
        }
        //Tu choi, yeu cau chinh sua: trang thai cho tiep nhan || cho duyet chinh sua || dang xu ly
        if ((AppConstant.BPM_ACTION.REJECT.equalsIgnoreCase(request.getAction())
                || AppConstant.BPM_ACTION.REQ_UPDATE.equalsIgnoreCase(request.getAction()))
                && !AppConstant.STATUS.PEND.equalsIgnoreCase(guaranteeHistory.getStatus())
                && !AppConstant.STATUS.DUPD.equalsIgnoreCase(guaranteeHistory.getStatus())
                && !AppConstant.STATUS.DPEN.equalsIgnoreCase(guaranteeHistory.getStatus())) {
            throw new BadRequestException("ERROR.GUARANTEE.STATUS.NOT.WAIT_RECEIVE.PROCESSING");
        }*/

        if (AppConstant.BPM_ACTION.REJECT.equalsIgnoreCase(request.getAction())
                && StringUtil.isEmpty(request.getContent())) {
            throw new BadRequestException("ERROR.GUARANTEE.REASON.REQUIRED");
        }

        if (AppConstant.BPM_ACTION.REQ_UPDATE.equalsIgnoreCase(request.getAction())
                && StringUtil.isEmpty(request.getContent())) {
            request.setContent("Yêu cầu chỉnh sửa");
        }

        /*if (AppConstant.BPM_ACTION.COMPLETE.equalsIgnoreCase(request.getAction())
                && (StringUtil.isEmpty(request.getBpmCode()) || StringUtil.isEmpty(request.getGuaranteeCode()))) {
            throw new BadRequestException("ERROR.GUARANTEE.BPM.GUARANTEE.CODE.REQUIRED");
        }*/

        List<BbGuaranteeHistoryDetail> historyDetails = bbGuaranteeHistoryDetailRepository.selectByTranSn(request.getTranSn());
        details = new HashMap<>();
        for (BbGuaranteeHistoryDetail detail : historyDetails) {
            GuaranteeHistoryDetailDto detailGuarantee = new GuaranteeHistoryDetailDto();
            BeanUtils.copyProperties(detail, detailGuarantee);
            details.put(detailGuarantee.getParam(), detailGuarantee);
        }
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = {BadRequestException.class, Exception.class})
    public ResponseEntity<?> process(GuaranteeUpdateStatusRequest request) {
        switch (request.getAction()) {
            case AppConstant.BPM_ACTION.RECEIVE:
                receive(request);
                break;
            case AppConstant.BPM_ACTION.REJECT:
                reject(request);
                break;
            case AppConstant.BPM_ACTION.REQ_UPDATE:
                reqUpdate(request);
                break;
            case AppConstant.BPM_ACTION.COMPLETE:
                complete(request);
                break;
            case AppConstant.BPM_ACTION.TRANSFER:
                transfer(request);
                break;
            case AppConstant.BPM_ACTION.AUTO_CLOSE:
                autoClose(request);
                break;
            default:
                throw new BadRequestException("ERROR.ACTION.INVALID");
        }
        bbGuaranteeHistoryRepository.save(guaranteeHistory);

        //send mail
        if (request.isSendMail() && mailTemplate != null) {
            // get all RM
            String mailRM = guaranteeCommonService.getEmailRM(guaranteeHistory.getCifNo());
            /*if (StringUtil.isNotEmpty(email)) {
                mailRM += "," + email;
            }*/
            guaranteeCommonService.sendMail(guaranteeHistory, details, mailTemplate, mailType, mailRM, request.getLang());
        }
        return ResponseUtils.success(true);
    }

    private void receive(GuaranteeUpdateStatusRequest request) {
        guaranteeHistory.setStatus(AppConstant.STATUS.DPEN);
        guaranteeHistory.setMsbUpdateBy(-1);
        guaranteeHistory.setMsbUpdateTime(new Date());
        guaranteeHistory.setMakerMsb(request.getBpmUser());
        guaranteeHistory.setEmailUser(request.getEmailUser());
        guaranteeHistory.setPhoneUser(request.getPhoneUser());
        if (guaranteeHistory.getMsbReceiveTime() == null) {
            guaranteeHistory.setMsbReceiveTime(new Date());
        }
        if (StringUtil.isEmpty(guaranteeHistory.getBpmCode()) && StringUtil.isNotEmpty(request.getBpmCode())) {
            guaranteeHistory.setBpmCode(request.getBpmCode());
        }
        //email = request.getEmailUser();
        saveLog(AppConstant.BPM_ACTION.RECEIVE, AppConstant.STATUS.DPEN, request.getBpmUser(), null, null);
        mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_PROCESS;
        mailType = MailType.MSB_APPROVED_REJECT.getValue();
    }

    private void reject(GuaranteeUpdateStatusRequest request) {
        guaranteeHistory.setStatus(AppConstant.STATUS.REJE);
        guaranteeHistory.setMsbUpdateBy(-1);
        guaranteeHistory.setMsbUpdateTime(new Date());
        guaranteeHistory.setRejectContent(request.getContent());
        guaranteeHistory.setAssigneeCk(null);
        if (StringUtil.isEmpty(guaranteeHistory.getBpmCode()) && StringUtil.isNotEmpty(request.getBpmCode())) {
            guaranteeHistory.setBpmCode(request.getBpmCode());
        }
        String bpmUser = request.getBpmUser();
        if (StringUtil.isEmpty(bpmUser))
            bpmUser = guaranteeHistory.getMakerMsb();
        saveLog(AppConstant.BPM_ACTION.REJECT, AppConstant.STATUS.REJE, bpmUser, request.getContent(), null);
        mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_REJECT;
        mailType = MailType.MSB_APPROVED_REJECT.getValue();
        //email = guaranteeHistory.getEmailUser();
    }

    private void reqUpdate(GuaranteeUpdateStatusRequest request) {
        guaranteeHistory.setStatus(AppConstant.STATUS.UPDA);
        guaranteeHistory.setMsbUpdateBy(-1);
        guaranteeHistory.setMsbUpdateTime(new Date());
        guaranteeHistory.setRejectContent(request.getContent());
        if (StringUtil.isEmpty(guaranteeHistory.getBpmCode()) && StringUtil.isNotEmpty(request.getBpmCode())) {
            guaranteeHistory.setBpmCode(request.getBpmCode());
        }
        String bpmUser = request.getBpmUser();
        if (StringUtil.isEmpty(bpmUser))
            bpmUser = guaranteeHistory.getMakerMsb();
        saveLog(AppConstant.BPM_ACTION.REQ_UPDATE, AppConstant.STATUS.UPDA, bpmUser, request.getContent(), null);
        mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_UPDATE;
        mailType = MailType.MSB_APPROVED_REJECT.getValue();
        //email = guaranteeHistory.getEmailUser();
    }

    private void complete(GuaranteeUpdateStatusRequest request) {
        guaranteeHistory.setMsbUpdateBy(-1);
        guaranteeHistory.setMsbUpdateTime(new Date());
        guaranteeHistory.setStatus(AppConstant.STATUS.DSUC);
        guaranteeHistory.setAssigneeCk(null);
        String bmpCode = request.getBpmCode();
        String guaranteeCode = request.getRefNumber();
        String bpmUser = request.getBpmUser();
        if (StringUtil.isNotEmpty(bmpCode))
            guaranteeHistory.setBpmCode(bmpCode);
        if (StringUtil.isNotEmpty(guaranteeCode))
            guaranteeHistory.setCodeGuarantee(guaranteeCode);
        saveLog(AppConstant.BPM_ACTION.COMPLETE, AppConstant.STATUS.DSUC, bpmUser, bmpCode, guaranteeCode);
        mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_COMPLETE;
        mailType = MailType.MSB_APPROVED_REJECT.getValue();
        //email = guaranteeHistory.getEmailUser();
        //luu thong tin goi nho ben bao lanh va ben duoc bao lanh
        guaranteeMetaDataService.insertBbGuaranteeMetaData(guaranteeHistory);
    }

    private void transfer(GuaranteeUpdateStatusRequest request) {
        guaranteeHistory.setStatus(AppConstant.STATUS.DPEN);
        guaranteeHistory.setMsbUpdateBy(-1);
        guaranteeHistory.setMsbUpdateTime(new Date());
        guaranteeHistory.setMakerMsb(request.getBpmUser());
        guaranteeHistory.setEmailUser(request.getEmailUser());
        guaranteeHistory.setPhoneUser(request.getPhoneUser());
        if (StringUtil.isEmpty(guaranteeHistory.getBpmCode()) && StringUtil.isNotEmpty(request.getBpmCode())) {
            guaranteeHistory.setBpmCode(request.getBpmCode());
        }
        //email = request.getEmailUser();
        //saveLog(AppConstant.BPM_ACTION.TRANSFER, AppConstant.STATUS.DPEN, request.getBpmUser(), null, null);
        //mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_PROCESS;
        //mailType = MailType.MSB_APPROVED_REJECT.getValue();
    }

    private void autoClose(GuaranteeUpdateStatusRequest request) {
        guaranteeHistory.setStatus(AppConstant.STATUS.CKTM);
        guaranteeHistory.setMsbUpdateBy(-1);
        guaranteeHistory.setMsbUpdateTime(new Date());
    }

    private void saveLog(String action, String newStatus, String bpmUser, String content, String number) {
        BbGuaranteeLog log = new BbGuaranteeLog();
        BeanUtils.copyProperties(guaranteeHistory, log);
        log.setTranSn(guaranteeHistory.getTranSn());
        log.setOldStatus(guaranteeHistory.getStatus());
        log.setNewStatus(newStatus);
        log.setAction(action);
        log.setCreateBy(-1);
        log.setMakerMsb(bpmUser);
        log.setEmail("");
        if (action.equalsIgnoreCase(AppConstant.BPM_ACTION.RECEIVE)) {
            log.setMsbReceiveTime(new Date());
        } else if (action.equalsIgnoreCase(AppConstant.BPM_ACTION.REJECT)) {
            log.setMsbEndTime(new Date());
            log.setRejectContent(content);
        } else if (action.equalsIgnoreCase(AppConstant.BPM_ACTION.COMPLETE)) {
            log.setMsbEndTime(new Date());
            log.setBpmCode(content);
            log.setCodeGuarantee(number);
        } else if (action.equalsIgnoreCase(AppConstant.BPM_ACTION.REQ_UPDATE)) {
            log.setMsbRequestTime(new Date());
            log.setRejectContent(content);
        }
        bbGuaranteeLogRepository.save(log);
    }
}
