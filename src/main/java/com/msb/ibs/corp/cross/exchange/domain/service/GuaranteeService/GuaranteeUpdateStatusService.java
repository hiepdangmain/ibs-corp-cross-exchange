package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.base.QueryPrinciple;
import com.msb.ibs.common.dto.UserPrincipal;
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

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeUpdateStatusService implements QueryPrinciple<GuaranteeUpdateStatusRequest, ResponseEntity<?>, UserPrincipal> {

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

    @Override
    public void validate(GuaranteeUpdateStatusRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        if (StringUtil.isEmpty(request.getTranSn())) {
            throw new BadRequestException("ERROR.GUARANTEE.TRAN_SN.REQUIRED");
        }
        guaranteeHistory = bbGuaranteeHistoryRepository.selectBytranSn(request.getTranSn());
        if (guaranteeHistory == null) {
            throw new BadRequestException("ERROR.GUARANTEE.TRAN_SN.NOT.EXIST");
        }
        if (guaranteeHistory.getMsbUpdateBy() != null
                && guaranteeHistory.getMsbUpdateBy().intValue() != principle.getUserId().intValue()) {
            throw new BadRequestException("ERROR.GUARANTEE.NOT.PERMISSION");
        }
        //Hoan thanh: trang thai dang xu ly
       if (AppConstant.IBS_ADMIN_ACTION.COMPLETE.equalsIgnoreCase(request.getAction())
                && !AppConstant.STATUS.DPEN.equalsIgnoreCase(guaranteeHistory.getStatus())) {
            throw new BadRequestException("ERROR.GUARANTEE.STATUS.NOT.PROCESSING");
        }
        //Tiep nhan: trang thai cho tiep nhan || cho duyet chinh sua
        if (AppConstant.IBS_ADMIN_ACTION.RECEIVE.equalsIgnoreCase(request.getAction())
                && !AppConstant.STATUS.PEND.equalsIgnoreCase(guaranteeHistory.getStatus())
                && !AppConstant.STATUS.DUPD.equalsIgnoreCase(guaranteeHistory.getStatus())) {
            throw new BadRequestException("ERROR.GUARANTEE.STATUS.NOT.WAIT_RECEIVE.UPDATED");
        }
        //Tu choi, yeu cau chinh sua: trang thai cho tiep nhan || cho duyet chinh sua || dang xu ly
        if ((AppConstant.IBS_ADMIN_ACTION.REJECT.equalsIgnoreCase(request.getAction())
                || AppConstant.IBS_ADMIN_ACTION.REQ_UPDATE.equalsIgnoreCase(request.getAction()))
                && !AppConstant.STATUS.PEND.equalsIgnoreCase(guaranteeHistory.getStatus())
                && !AppConstant.STATUS.DUPD.equalsIgnoreCase(guaranteeHistory.getStatus())
                && !AppConstant.STATUS.DPEN.equalsIgnoreCase(guaranteeHistory.getStatus())) {
            throw new BadRequestException("ERROR.GUARANTEE.STATUS.NOT.WAIT_RECEIVE.PROCESSING");
        }

        if (AppConstant.IBS_ADMIN_ACTION.REJECT.equalsIgnoreCase(request.getAction())
                || AppConstant.IBS_ADMIN_ACTION.REQ_UPDATE.equalsIgnoreCase(request.getAction())) {
            if (StringUtil.isEmpty(request.getContent())) {
                throw new BadRequestException("ERROR.GUARANTEE.REASON.REQUIRED");
            }
        }
        if (AppConstant.IBS_ADMIN_ACTION.COMPLETE.equalsIgnoreCase(request.getAction())
                && (StringUtil.isEmpty(request.getBpmCode()) || StringUtil.isEmpty(request.getGuaranteeCode()))) {
            throw new BadRequestException("ERROR.GUARANTEE.BPM.GUARANTEE.CODE.REQUIRED");
        }

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
    public ResponseEntity<?> process(GuaranteeUpdateStatusRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        switch (request.getAction()) {
            case AppConstant.IBS_ADMIN_ACTION.RECEIVE:
                receive(principle);
                break;
            case AppConstant.IBS_ADMIN_ACTION.REJECT:
                reject(request.getContent(), principle);
                break;
            case AppConstant.IBS_ADMIN_ACTION.REQ_UPDATE:
                reqUpdate(request.getContent(), principle);
                break;
            case AppConstant.IBS_ADMIN_ACTION.UPDATE:
                update(request.getBpmCode(), request.getGuaranteeCode(), principle);
                break;
            case AppConstant.IBS_ADMIN_ACTION.COMPLETE:
                complete(request.getBpmCode(), request.getGuaranteeCode(), principle);
                break;
            default:
                throw new BadRequestException("ERROR.ACTION.INVALID");
        }
        bbGuaranteeHistoryRepository.save(guaranteeHistory);

        //send mail
        if (mailTemplate != null) {
            // get all RM
            String mailRM = guaranteeCommonService.getEmailRM(guaranteeHistory.getCifNo());
            guaranteeCommonService.sendMail(guaranteeHistory, details, mailTemplate, mailType, mailRM, null);
        }
        return ResponseUtils.success(true);
    }

    private void receive(UserPrincipal principal) {
        guaranteeHistory.setStatus(AppConstant.STATUS.DPEN);
        guaranteeHistory.setMsbUpdateBy(principal.getUserId());
        guaranteeHistory.setMsbUpdateTime(new Date());
        guaranteeHistory.setMakerMsb(principal.getUsername());
        if (guaranteeHistory.getMsbReceiveTime() == null) {
            guaranteeHistory.setMsbReceiveTime(new Date());
        }
        saveLog(AppConstant.IBS_ADMIN_ACTION.RECEIVE, AppConstant.STATUS.DPEN, principal,null, null);
    }

    private void reject(String rejectContent, UserPrincipal principal) {
        guaranteeHistory.setStatus(AppConstant.STATUS.REJE);
        guaranteeHistory.setMsbUpdateBy(principal.getUserId());
        guaranteeHistory.setMsbUpdateTime(new Date());
        guaranteeHistory.setRejectContent(rejectContent);
        guaranteeHistory.setAssigneeCk(null);
        saveLog(AppConstant.IBS_ADMIN_ACTION.REJECT, AppConstant.STATUS.REJE, principal, rejectContent, null);
        if (guaranteeHistory.getMakerMsb() == null) guaranteeHistory.setMakerMsb(principal.getUsername());
        mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_REJECT;
        mailType = MailType.MSB_APPROVED_REJECT.getValue();
    }

    private void reqUpdate(String updateContent, UserPrincipal principal) {
        guaranteeHistory.setStatus(AppConstant.STATUS.UPDA);
        guaranteeHistory.setMsbUpdateBy(principal.getUserId());
        guaranteeHistory.setMsbUpdateTime(new Date());
        guaranteeHistory.setRejectContent(updateContent);
        saveLog(AppConstant.IBS_ADMIN_ACTION.REQ_UPDATE, AppConstant.STATUS.UPDA, principal, updateContent, null);
        if (guaranteeHistory.getMakerMsb() == null) guaranteeHistory.setMakerMsb(principal.getUsername());
        mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_UPDATE;
        mailType = MailType.MSB_APPROVED_REJECT.getValue();
    }

    private void update(String bmpCode, String guaranteeCode, UserPrincipal principal) {
//        saveLog(AppConstant.IBS_ADMIN_ACTION.UPDATE, guaranteeHistory.getStatus(), principal);
        guaranteeHistory.setMsbUpdateBy(principal.getUserId());
        guaranteeHistory.setMsbUpdateTime(new Date());
        if (StringUtil.isNotEmpty(bmpCode)) guaranteeHistory.setBpmCode(bmpCode);
        if (StringUtil.isNotEmpty(guaranteeCode)) guaranteeHistory.setCodeGuarantee(guaranteeCode);
    }

    private void complete(String bmpCode, String guaranteeCode, UserPrincipal principal) {
        guaranteeHistory.setMsbUpdateBy(principal.getUserId());
        guaranteeHistory.setMsbUpdateTime(new Date());
        guaranteeHistory.setStatus(AppConstant.STATUS.DSUC);
        guaranteeHistory.setAssigneeCk(null);
        if (StringUtil.isNotEmpty(bmpCode)) guaranteeHistory.setBpmCode(bmpCode);
        if (StringUtil.isNotEmpty(guaranteeCode)) guaranteeHistory.setCodeGuarantee(guaranteeCode);
        saveLog(AppConstant.IBS_ADMIN_ACTION.COMPLETE, AppConstant.STATUS.DSUC, principal, bmpCode, guaranteeCode);
        mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_COMPLETE;
        mailType = MailType.MSB_APPROVED_REJECT.getValue();
        //luu thong tin goi nho ben bao lanh va ben duoc bao lanh
        guaranteeMetaDataService.insertBbGuaranteeMetaData(guaranteeHistory);
    }

    private void saveLog(String action, String newStatus, UserPrincipal principal, String content, String number) {
        BbGuaranteeLog log = new BbGuaranteeLog();
        BeanUtils.copyProperties(guaranteeHistory, log);
        log.setTranSn(guaranteeHistory.getTranSn());
        log.setOldStatus(guaranteeHistory.getStatus());
        log.setNewStatus(newStatus);
        log.setAction(action);
        log.setCreateBy(principal.getUserId());
        log.setMakerMsb(principal.getUsername());
        log.setEmail(principal.getEmail());
        if (action.equalsIgnoreCase(AppConstant.IBS_ADMIN_ACTION.RECEIVE)) {
            log.setMsbReceiveTime(new Date());
        } else if (action.equalsIgnoreCase(AppConstant.IBS_ADMIN_ACTION.REJECT)) {
            log.setMsbEndTime(new Date());
            log.setRejectContent(content);
        } else if (action.equalsIgnoreCase(AppConstant.IBS_ADMIN_ACTION.COMPLETE)) {
            log.setMsbEndTime(new Date());
            log.setBpmCode(content);
            log.setCodeGuarantee(number);
        } else if (action.equalsIgnoreCase(AppConstant.IBS_ADMIN_ACTION.REQ_UPDATE)) {
            log.setMsbRequestTime(new Date());
            log.setRejectContent(content);
        }
        bbGuaranteeLogRepository.save(log);
    }
}
