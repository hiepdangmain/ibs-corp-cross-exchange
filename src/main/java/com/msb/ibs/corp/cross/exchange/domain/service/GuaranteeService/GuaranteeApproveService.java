package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.msb.ibs.common.constant.AppConstants;
import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.integration.ecm.EcmResponse;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.utils.FileTools;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.common.utils.TXResponseCodeUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.ApiGwConstants;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.enums.AttachmentTypeEnum;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeLog;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.ApiResponseBpm;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.OutputDataBpm;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.OutputStatusBpm;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeLogRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.BpmService;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import com.msb.ibs.corp.cross.exchange.infrastracture.utils.ResponseUtils;
import com.msb.ibs.corp.main.service.domain.base.BaseApprove;
import com.msb.ibs.corp.main.service.domain.contants.MainConstants;
import com.msb.ibs.corp.main.service.domain.dto.ResultTransaction;
import com.msb.ibs.corp.main.service.domain.enums.TransactionStatusEnum;
import com.msb.ibs.corp.main.service.domain.input.ApproveOrRejectTransactionInput;
import com.msb.ibs.corp.main.service.domain.input.GetListEmailInput;
import com.msb.ibs.corp.main.service.domain.input.UploadFileEcmInput;
import com.msb.ibs.corp.main.service.domain.output.ApproveTransactionOutput;
import com.msb.ibs.corp.main.service.domain.output.GetUserInternalResponse;
import com.msb.ibs.corp.main.service.domain.service.DocumentCertService;
import com.msb.ibs.corp.main.service.domain.service.EcmService;
import com.msb.ibs.corp.main.service.domain.service.UserService;
import com.msb.ibs.corp.main.service.domain.utils.MainUtil;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeApproveService extends BaseApprove<ApproveTransactionOutput> {

    @Autowired
    private BbGuaranteeHistoryRepository historyRepository;
    @Autowired
    private GuaranteeCommonService guaranteeCommonService;
    @Autowired
    private UserService userService;
    @Autowired
    private BbGuaranteeFileAttachmentRepository fileAttachmentRepository;
    @Autowired
    private EcmService ecmService;
    @Autowired
    private GuaranteeMetaDataService guaranteeMetaDataService;
    @Autowired
    private BpmService bpmService;
    @Autowired
    private GuaranteeSyncService guaranteeSyncService;
    @Autowired
    private DocumentCertService documentCertService;

    private BbGuaranteeHistory guaranteeHistory ;
    private List<BbGuaranteeFileAttachment> listFile = new ArrayList<>();

    @Override
    protected ResultTransaction processOnline(InternalRequest internalRequest) throws LogicException {
        ApproveOrRejectTransactionInput input = (ApproveOrRejectTransactionInput) internalRequest.getData();
        System.out.println("QuyenCv2 GuaranteeApproveService: processOnline "+ input.getTranSn());
        getOrderTransaction(input.getTranSn());
        listFile = fileAttachmentRepository.findByTransRefer(input.getTranSn());

        //day bao lanh sang BPM
        ApiResponseBpm<Object, Object> responseBpm = null;
        try {
            if (StringUtil.isEmpty(guaranteeHistory.getTransnRefer())) { //create
                responseBpm = bpmService.makePostCall(ApiGwConstants.BPM_CONFIG_GATEWAY.GUARANTEE_CREATE,
                        guaranteeCommonService.buildGuaranteeCreate(guaranteeHistory), new TypeReference<>() {}, null,
                        guaranteeHistory.getTranSn(), ThreadContext.get(AppConstants.Header.Request_Id));
            } else { //update
                Map<String, Object> routeParameters = new HashMap<>();
                routeParameters.put("tranSn", guaranteeHistory.getTransnRefer());
                responseBpm = bpmService.makePutCall(ApiGwConstants.BPM_CONFIG_GATEWAY.GUARANTEE_UPDATE,
                        guaranteeCommonService.buildGuaranteeUpdate(guaranteeHistory), routeParameters, new TypeReference<>() {},
                        null, guaranteeHistory.getTransnRefer(), ThreadContext.get(AppConstants.Header.Request_Id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ResponseUtils.isSuccess(responseBpm) || ResponseUtils.isCreated(responseBpm)) { //thanh cong
            logger.info("Create/update guarantee info to BPM success");
            OutputDataBpm outputDataBpm = bpmService.parseData(responseBpm);
            if (outputDataBpm != null) guaranteeHistory.setBpmCode(outputDataBpm.getRequestCode());
            guaranteeHistory.setStatusSync(null);

            //send mail cho RM tiep nhan xu ly
            guaranteeHistory.setCustomerCkName(internalRequest.getUserPrincipal().getUsername());
            guaranteeHistory.setEmailChecker(internalRequest.getUserPrincipal().getEmail());
            guaranteeCommonService.sendEmail(guaranteeHistory);
        } else if (ResponseUtils.isTimeout(responseBpm)) {
            logger.info("Create/update guarantee info to BPM Timeout");
            guaranteeHistory.setStatusSync(AppConstant.STATUS.NEWR);
            guaranteeHistory.setNextSyncTime(new Date());
            guaranteeHistory.setNumSync(0);
        } else { //that bai
            OutputStatusBpm outputStatusBpm = bpmService.parseStatus(responseBpm);
            if (outputStatusBpm != null) {
                guaranteeHistory.setBpmErrorCode(outputStatusBpm.getCode());
                guaranteeHistory.setBpmErrorMsg(outputStatusBpm.getMessage());
            }
            logger.info("Create/update guarantee info to BPM fail with errorCode={}, errorMsg={}", guaranteeHistory.getBpmErrorCode(),
                    guaranteeHistory.getBpmErrorMsg());
            //send mail cho CCO1 vao BPM huy ho so
            if (StringUtil.isNotEmpty(guaranteeHistory.getTransnRefer())
                    && StringUtil.isNotEmpty(guaranteeHistory.getBpmCode())
                    && StringUtil.isNotEmpty(guaranteeHistory.getEmailUser())) {
                guaranteeCommonService.sendMailCancel(guaranteeHistory, AppConstant.MAIL_TEMPLATE.GUARANTEE_CANCEL);
            }
            return MainUtil.genResultTransactionFromError(TXResponseCodeUtil.getTXResponseCodeByCode(MainConstants.TXResponseCode.ON_FAIL),
                    input.getTranSn(), input.getFlow(), internalRequest.getLanguage());
        }

        return MainUtil.genResultTransactionFromError(TXResponseCodeUtil.getTXResponseCodeByCode("0"),
                input.getTranSn(), input.getFlow(), internalRequest.getLanguage(), true);
    }

    @Override
    protected ResultTransaction processOffline(InternalRequest internalRequest) throws LogicException {
        System.out.println("QuyenCV2 : processOffline");
        return processOnline(internalRequest);
    }

    @Override
    protected ApproveTransactionOutput buildResponse(ResultTransaction result) {
        return new ApproveTransactionOutput(result);
    }

    @Override
    protected void updateTransaction(ResultTransaction resultTransaction, InternalRequest internalRequest) throws LogicException {
        System.out.println("QuyenCv2 GuaranteeApproveService: updateTransaction "+ internalRequest);
        System.out.println("QuyenCv2 GuaranteeApproveService: updateTransaction "+ resultTransaction);
        if(guaranteeHistory == null)
            getOrderTransaction(resultTransaction.getTranSn());
        UserPrincipal userInfo = internalRequest.getUserPrincipal();
        if (TransactionStatusEnum.SUCCESS.getValue().equals(resultTransaction.getStatus())
                && StringUtil.isEmpty(guaranteeHistory.getStatusSync())) {
            if (StringUtil.isNotEmpty(guaranteeHistory.getTransnRefer())) {
                guaranteeHistory.setStatus(AppConstant.STATUS.DPEN);
            } else {
                guaranteeHistory.setStatus(AppConstant.STATUS.PEND);
            }
            guaranteeHistory.setAssigneeCk(null);
            guaranteeHistory.setCustomerSendTime(new Date());
            guaranteeHistory.setStatusSync(AppConstant.STATUS.SUCC);
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
            executor.execute(() -> {
                //delete old file
                guaranteeCommonService.deleteFileMinIO(guaranteeHistory);
                //uploadFileToECM(listFile);
                //upload new file
                uploadFileToMinIO(listFile);
            });
            executor.shutdown();
            //luu thong tin goi nho ben bao lanh va ben duoc bao lanh
            //guaranteeMetaDataService.insertBbGuaranteeMetaData(guaranteeHistory);
        } else if (TransactionStatusEnum.SUBMITTED_TO_CHECKER.getValue().equals(resultTransaction.getStatus())) {
            if (resultTransaction.getAssigneeList().size() > 0) {
                guaranteeHistory.setAssigneeCk(String.join(", ", resultTransaction.getAssigneeList()));
            }
            if (StringUtil.isNotEmpty(guaranteeHistory.getTransnRefer())) {
                guaranteeHistory.setStatus(AppConstant.STATUS.DUPD);
            } else {
                guaranteeHistory.setStatus(resultTransaction.getStatus());
            }
            logger.info("QuyenCv2 GuaranteeApproveService updateTransaction resultTransaction : "+ resultTransaction.getTaskLevel());
            if (StringUtil.isNotEmpty(resultTransaction.getTaskLevel())) {
                guaranteeHistory.setLastCk(resultTransaction.getTaskLevel());
            }
        } else if (TransactionStatusEnum.CHECKER_REJECT.getValue().equals(resultTransaction.getStatus())) {
            if (StringUtil.isNotEmpty(guaranteeHistory.getTransnRefer())) {
                guaranteeHistory.setStatusSync(AppConstant.STATUS.WCAN); //cho huy sang BPM
            }
            guaranteeHistory.setStatus(AppConstant.STATUS.CKRJ);
            guaranteeHistory.setAssigneeCk(null);
        } else if (TransactionStatusEnum.FAIL.getValue().equals(resultTransaction.getStatus())) {
            guaranteeHistory.setStatus(AppConstant.STATUS.FAIL);
        } else if (TransactionStatusEnum.SUCCESS.getValue().equals(resultTransaction.getStatus())
                && AppConstant.STATUS.NEWR.equals(guaranteeHistory.getStatusSync())) {
            if (StringUtil.isNotEmpty(guaranteeHistory.getTransnRefer())) {
                guaranteeHistory.setStatus(AppConstant.STATUS.DPEN);
            } else {
                guaranteeHistory.setStatus(AppConstant.STATUS.PEND);
            }
            guaranteeHistory.setAssigneeCk(null);
            guaranteeHistory.setCustomerSendTime(new Date());
        }
        guaranteeHistory.setUpdateBy(userInfo.getUserId());
        guaranteeHistory.setUpdateTime(new Date());
        guaranteeHistory.setCustomerCkName(internalRequest.getUserPrincipal().getUsername());
        guaranteeHistory.setEmailChecker(internalRequest.getUserPrincipal().getEmail());
        if (StringUtil.isNotEmpty(guaranteeHistory.getListCkApprove())) {
            String listCk = guaranteeHistory.getListCkApprove();
            guaranteeHistory.setListCkApprove(listCk.concat(",").concat(String.valueOf(userInfo.getUserId())));
        } else {
            guaranteeHistory.setListCkApprove(String.valueOf(userInfo.getUserId()));
        }
        historyRepository.save(guaranteeHistory);
    }

    private void getOrderTransaction(String tranSn) throws LogicException {
        guaranteeHistory = historyRepository.selectBytranSn(tranSn);
        System.out.println("QuyenCv2 getOrderTransaction : "+ guaranteeHistory);
        if (!(AppConstant.STATUS.CKNG.equals(guaranteeHistory.getStatus())
                || AppConstant.STATUS.DUPD.equals(guaranteeHistory.getStatus()))) {
            throw new LogicException("ERROR.GUARANTEE.CAN.NOT.APPROVE.THIS.RECORD");
        }
    }

    /*@Async("threadPoolTaskExecutor")*/
    public void uploadFileToECM(List<BbGuaranteeFileAttachment> fileAttachments) {
        try {
            if (!CollectionUtils.isEmpty(fileAttachments)) {
                String folderName = guaranteeHistory.getTransnRefer() != null ? guaranteeHistory.getTransnRefer() : guaranteeHistory.getTranSn();
                for (BbGuaranteeFileAttachment attachment : fileAttachments) {
                    if (StringUtil.isNotEmpty(attachment.getFileName()) && !AttachmentTypeEnum.ECM.value().equalsIgnoreCase(attachment.getAttachmentType())) {
                        Integer fileId = attachment.getId();
                        byte[] content = Files.readAllBytes(Paths.get(attachment.getPathFile()));
                        UploadFileEcmInput uploadFileEcmInput = new UploadFileEcmInput();
                        uploadFileEcmInput.setContent(content);
                        uploadFileEcmInput.setFileName(attachment.getFileName());
                        uploadFileEcmInput.setRootFolder(genFilePath());
                        uploadFileEcmInput.setFolderName(folderName);
                        uploadFileEcmInput.setCifNo(guaranteeHistory.getCifNo());
                        uploadFileEcmInput.setTitle(attachment.getFileName());
                        try {
                            logger.info("Begin Upload File To ECM For CifNo {} By AttachmentId {}", guaranteeHistory.getCifNo(), fileId);
                            EcmResponse response = ecmService.uploadFile(uploadFileEcmInput);
                            if (response != null && response.getUploadFileResponse() != null
                                    && response.getUploadFileResponse().getRespMessage() != null
                                    && "0".equals(response.getUploadFileResponse().getRespMessage().getRespCode())) {
                                String[] object = (String[]) response.getUploadFileResponse().getRespDomain().get("data");
                                if (object != null) {
                                    attachment.setPathFile(object[0].replace("{", "").replace("}", ""));
                                    attachment.setAttachmentType(AttachmentTypeEnum.ECM.value());
                                }
                                attachment.setStatus(TransactionStatusEnum.SUCCESS.getValue());
                                attachment.setAttachmentType(AttachmentTypeEnum.ECM.value());
                                logger.info("Upload File ECM Success For CifNo {} By AttachmentId {}", guaranteeHistory.getCifNo(), fileId);
                            } else {
                                logger.info("Failed To Upload File ECM For CifNo {} By AttachmentId {}", guaranteeHistory.getCifNo(), fileId);
                                attachment.setStatus(TransactionStatusEnum.FAIL.getValue());
                            }
                        } catch (Exception e) {
                            attachment.setStatus(TransactionStatusEnum.FAIL.getValue());
                            logger.info("Failed To Upload File ECM For CifNo {} By AttachmentId {}", guaranteeHistory.getCifNo(), fileId);
                            logger.error("Error", e);
                        }
                        fileAttachmentRepository.save(attachment);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed To DownloadFile From SSH Server ", e);
        }
    }

    private String genFilePath() {
        return MainConstants.UPLOAD_DIRECTORY_PRODUCTION + FileTools.getSubFolderByDate(new Date(), "yyyy/MM/") + "guarantee/";
    }

    public void uploadFileToMinIO(List<BbGuaranteeFileAttachment> fileAttachments) {
        String tranSn = guaranteeHistory.getTransnRefer() != null ? guaranteeHistory.getTransnRefer() : guaranteeHistory.getTranSn();
        guaranteeCommonService.uploadFileToMinIO(fileAttachments, tranSn);
    }

}
