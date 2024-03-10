package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.utils.CommonUtil;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.enums.ProcessTypeEnum;
import com.msb.ibs.corp.cross.exchange.application.enums.WorkflowEnum;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeDetailResponse;
import com.msb.ibs.corp.cross.exchange.domain.dto.DocumentGuaranteeDto;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeCondition;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeHistoryDetailDto;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocumentConfig;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeDocumentConfigRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import com.msb.ibs.corp.main.service.domain.base.BaseAfterConfirm;
import com.msb.ibs.corp.main.service.domain.enums.WorkflowStatusEnum;
import com.msb.ibs.corp.main.service.domain.output.TransactionConfirmOutput;
import com.msb.ibs.corp.main.service.domain.output.WorkflowProcessTaskOutput;
import com.msb.ibs.corp.main.service.domain.service.CommonMainService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeConfirmService extends BaseAfterConfirm<String, TransactionConfirmOutput, BbGuaranteeHistory> {

    @Autowired
    private BbGuaranteeHistoryRepository historyRepository;

    @Autowired
    private CommonMainService commonMainService;

    @Autowired
    private BbGuaranteeFileAttachmentRepository fileAttachmentRepository;

    @Autowired
    private BbGuaranteeHistoryRepository guaranteeHistoryRepository;

    @Autowired
    private BbGuaranteeHistoryDetailRepository detailRepository;

    @Autowired
    private BbGuaranteeDocumentConfigRepository bbGuaranteeDocumentConfigRepository;

    @Autowired
    private GuaranteeCommonService guaranteeCommonService;

    private boolean checkRecordUpdate = false;

    private UserPrincipal userLogin = null;

    @Override
    protected BbGuaranteeHistory getTransaction() throws LogicException {
        return historyRepository.selectBytranSn(tranSn);
    }

    @Override
    protected void startWorkFlowTask(String request, InternalRequest internalRequest) throws LogicException {
        String workflowName = WorkflowEnum.GUARANTEE_CROSS_EXCHANGE.value();
        userLogin = internalRequest.getUserPrincipal();

        String taskId = CommonUtil.genWorkflowTaskId(workflowName, orderTransaction.getTranSn());

        orderTransaction.setWfProcessId(taskId);
        if(AppConstant.STATUS.UPDA.equalsIgnoreCase(orderTransaction.getStatus())){
            orderTransaction.setStatus(AppConstant.STATUS.DUPD);
            orderTransaction.setTransnActive(AppConstant.GUARANTEE_HISTORY.ACTIVE);
            checkRecordUpdate = true;
        }
        else
            orderTransaction.setStatus(WorkflowStatusEnum.SUBMITTED_TO_CHECKER.getValue());
        orderTransaction.setWfStatus(WorkflowStatusEnum.SUBMITTED_TO_CHECKER.getValue());
        orderTransaction.setWfUpdatedTime(new Date());
        orderTransaction.setCustomerMkName(internalRequest.getUserPrincipal().getUsername());

        //set info lenh
        List<BbGuaranteeHistoryDetail> historyDetails = detailRepository.selectByTranSn(tranSn);
        List<GuaranteeHistoryDetailDto> details = new ArrayList<>();
        Map<String, GuaranteeHistoryDetailDto> mapDetail = new HashMap<>();
        boolean sendMailChecker = false;
        for (BbGuaranteeHistoryDetail detail : historyDetails) {
            GuaranteeHistoryDetailDto detailDto = new GuaranteeHistoryDetailDto();
            BeanUtils.copyProperties(detail, detailDto);
            details.add(detailDto);
            mapDetail.put(detailDto.getParam(), detailDto);
            if (AppConstant.BB_GUARANTEE_FIELD.notificationTo.equalsIgnoreCase(detail.getParam())) {
                if (StringUtil.isNotEmpty(detail.getValue())
                        && detail.getValue().contains(AppConstant.NOTIFY_CHECKER)) {
                    sendMailChecker = true;
                }
            }
        }
        // get all file document
        List<DocumentGuaranteeDto> documents = new ArrayList<>();
        List<BbGuaranteeFileAttachment> listFile = fileAttachmentRepository.findByTransRefer(tranSn);
      /*  if (listFile.size() > 0){
            // sắp xep lai list Order theo thu tu seq
            Collections.sort(listFile, (m1, m2) -> m1.compareTo(m1, m2));
            for (BbGuaranteeFileAttachment fileAttachment : listFile) {
                DocumentGuaranteeDto documentGuarantee = new DocumentGuaranteeDto();
                BeanUtils.copyProperties(fileAttachment, documentGuarantee);
                documents.add(documentGuarantee);
            }
        }*/
        if (listFile.size() > 0) {
            // sắp xep lai list Order theo thu tu seq
            Collections.sort(listFile, (m1, m2) -> m1.compareTo(m1, m2));
            List<BbGuaranteeDocumentConfig> documentConfigs = bbGuaranteeDocumentConfigRepository.
                    findListDocumentConfig(List.of("0", orderTransaction.getTypeGuarantee()));
            for (BbGuaranteeFileAttachment fileAttachment : listFile) {
                DocumentGuaranteeDto documentGuarantee = new DocumentGuaranteeDto();
                BeanUtils.copyProperties(fileAttachment, documentGuarantee);
                for (BbGuaranteeDocumentConfig documentConfig : documentConfigs) {
                    if (documentConfig.getSeqNo().intValue() == fileAttachment.getDocumentId().intValue()) {
                        documentGuarantee.setDocumentNameVn(documentConfig.getDocNameVn());
                        documentGuarantee.setDocumentNameEn(documentConfig.getDocNameEn());
                    }
                }
                documents.add(documentGuarantee);
            }
        }

        GuaranteeDetailResponse info = new GuaranteeDetailResponse();
        info.setGuaranteeHistory(orderTransaction);
        info.setDetails(details);
        info.setDocuments(documents);
        info.setEmailParams(guaranteeCommonService.getParamEmail(orderTransaction, mapDetail));

        WorkflowProcessTaskOutput outputConfirm = commonMainService.startTaskToWorkflow(taskId, orderTransaction.getAmount(), userLogin.getCorpId(), userLogin.getUserId(),
                userLogin.getUsername(), ProcessTypeEnum.GUARANTEE.getKey(), orderTransaction.getTranSn(),
                workflowName, null, null, sendMailChecker ? List.of(AppConstant.NOTIFY_CHECKER) : null, null,
                orderTransaction.getExpiredDate(), null, null, info, internalRequest.getToken());
        if (outputConfirm.getAssigneeList().size() > 0) {
            orderTransaction.setAssigneeCk(String.join(", ", outputConfirm.getAssigneeList()));
            if (StringUtil.isNotEmpty(outputConfirm.getTaskIdList().get(0).getTaskLevel())) {
                orderTransaction.setLastCk(outputConfirm.getTaskIdList().get(0).getTaskLevel());
            }
        }

    }

    @Override
    protected void processBusiness(String request, InternalRequest internalRequest) throws LogicException {

    }

    @Override
    protected void saveData() {
        // đổi trạng thai lich su
        if(checkRecordUpdate){
            // get all ban ghi chinh sua
            GuaranteeCondition condition = new GuaranteeCondition();
            condition.setTranSn(orderTransaction.getTransnRefer() != null ? orderTransaction.getTransnRefer() : orderTransaction.getTranSn());
            List<BbGuaranteeHistory> listData = new ArrayList<>();
            listData = guaranteeHistoryRepository.findByCondiiton(condition, userLogin.getCorpId());
            if (listData != null) {
                listData = listData.stream().filter(b -> !b.getTranSn().equalsIgnoreCase(tranSn)).collect(Collectors.toList());
            }
            // update all cac ban ghi chinh sua cu ve inActive
            for (BbGuaranteeHistory item : listData) {
                item.setTransnActive(AppConstant.GUARANTEE_HISTORY.IN_ACTIVE);
            }
            guaranteeHistoryRepository.saveAllList(listData);
        } else {
            // tao moi lan dau
            orderTransaction.setTransnActive(AppConstant.GUARANTEE_HISTORY.ACTIVE);
        }
        // đổi trạng thái file
        saveDocument();

    }

    @Override
    protected TransactionConfirmOutput buildResponse() {
        return new TransactionConfirmOutput(tranSn, orderTransaction.getStatus(), orderTransaction.getWfStatus(), orderTransaction.getWfProcessId());
    }

    private void saveDocument() {
        fileAttachmentRepository.updateAllDocumentByStatus(tranSn, AppConstant.STATUS_RECORD.DPEN,"Y");
    }
}
