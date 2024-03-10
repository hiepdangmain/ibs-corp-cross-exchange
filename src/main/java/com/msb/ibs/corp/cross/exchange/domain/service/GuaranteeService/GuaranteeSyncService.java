package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.msb.ibs.common.constant.AppConstants;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.utils.DateUtil;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.ApiGwConstants;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.enums.ErrorCode;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeUploadMinIORequest;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.ApiResponseBpm;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.OutputDataBpm;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.BpmService;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import com.msb.ibs.corp.cross.exchange.infrastracture.utils.ResponseUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class GuaranteeSyncService {

    public static final Logger logger = LoggerFactory.getLogger(GuaranteeSyncService.class);
    private final BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository;
    private final BbGuaranteeFileAttachmentRepository fileAttachmentRepository;
    private final BbGuaranteeHistoryDetailRepository bbGuaranteeHistoryDetailRepository;
    private Integer maxNumberSync; //so lan toi da dong bo
    private Integer intervalSync; //khong thoi gian cho retry dong bo
    private final GuaranteeCommonService guaranteeCommonService;
    private final Integer numUploadMax; //so lan toi da retry upload to minio
    private final BpmService bpmService;
    private final GuaranteeMetaDataService guaranteeMetaDataService;

    public GuaranteeSyncService(BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository,
                                BbGuaranteeFileAttachmentRepository fileAttachmentRepository,
                                BbGuaranteeHistoryDetailRepository bbGuaranteeHistoryDetailRepository,
                                Environment environment,
                                GuaranteeCommonService guaranteeCommonService,
                                BpmService bpmService,
                                GuaranteeMetaDataService guaranteeMetaDataService) {
        this.bbGuaranteeHistoryRepository = bbGuaranteeHistoryRepository;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.maxNumberSync = Integer.parseInt(environment.getProperty("guarantee.max.number.syn.bpm", "5"));
        this.intervalSync = Integer.parseInt(environment.getProperty("guarantee.interval.syn.bpm", "5"));
        this.guaranteeCommonService = guaranteeCommonService;
        this.numUploadMax = Integer.parseInt(environment.getProperty("minio.num.upload.max"));
        this.bpmService = bpmService;
        this.bbGuaranteeHistoryDetailRepository = bbGuaranteeHistoryDetailRepository;
        this.guaranteeMetaDataService = guaranteeMetaDataService;
    }

    public boolean syncGuarantee() throws LogicException {
        long startTime = System.currentTimeMillis();
        try {
            logger.info(" ******* Start Sync Guarantee To BPM *******");
            List<BbGuaranteeHistory> guaranteeHistoryList = bbGuaranteeHistoryRepository.getGuaranteeSync(maxNumberSync);
            if (!CollectionUtils.isEmpty(guaranteeHistoryList)) {
                for (BbGuaranteeHistory guaranteeHistory : guaranteeHistoryList) {
                    guaranteeHistory.setNumSync(guaranteeHistory.getNumSync() + 1);

                    //dong bo thong tin bao lanh sang BPM
                    ApiResponseBpm<Object, Object> responseBpm = null;
                    try {
                        if (StringUtil.isEmpty(guaranteeHistory.getTransnRefer())) {
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

                    if (ResponseUtils.isSuccess(responseBpm) || ResponseUtils.isCreated(responseBpm)
                            || bpmService.isAlreadyExist(responseBpm)) { //thanh cong
                        logger.info("Create/update guarantee info to BPM success");
                        OutputDataBpm outputDataBpm = bpmService.parseData(responseBpm);
                        if (outputDataBpm != null)
                            guaranteeHistory.setBpmCode(outputDataBpm.getRequestCode());
                        guaranteeHistory.setStatusSync(AppConstant.STATUS.SUCC);
                        if (StringUtil.isNotEmpty(guaranteeHistory.getTransnRefer())) {
                            guaranteeHistory.setStatus(AppConstant.STATUS.DPEN);
                        } else {
                            guaranteeHistory.setStatus(AppConstant.STATUS.PEND);
                        }
                        guaranteeHistory.setAssigneeCk(null);
                        guaranteeHistory.setCustomerSendTime(new Date());
                        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
                        executor.execute(() -> {
                            //delete old file
                            guaranteeCommonService.deleteFileMinIO(guaranteeHistory);
                            //upload new file
                            guaranteeCommonService.uploadFileToMinIO(guaranteeHistory);
                        });
                        executor.shutdown();
                        //luu thong tin goi nho ben bao lanh va ben duoc bao lanh
                        //guaranteeMetaDataService.insertBbGuaranteeMetaData(guaranteeHistory);

                        //send email cho RM vao BPM xu ly
                        guaranteeCommonService.sendEmail(guaranteeHistory);
                    } else if (ResponseUtils.isTimeout(responseBpm)) {
                        logger.info("Create/update guarantee info to BPM Timeout => Continue retry");
                        guaranteeHistory.setNextSyncTime(DateUtil.addMinutes(new Date(), intervalSync));
                        if (guaranteeHistory.getNumSync().intValue() == maxNumberSync.intValue()) {
                            guaranteeHistory.setStatus(AppConstant.STATUS.FAIL);
                            guaranteeHistory.setStatusSync(AppConstant.STATUS.FAIL);
                        }
                    } else { //fail
                        logger.info("Create/update guarantee info to BPM Fail");
                        guaranteeHistory.setStatus(AppConstant.STATUS.FAIL);
                        guaranteeHistory.setStatusSync(AppConstant.STATUS.FAIL);
                    }
                    bbGuaranteeHistoryRepository.save(guaranteeHistory);
                    if (AppConstant.STATUS.FAIL.equals(guaranteeHistory.getStatus())) {
                        //fail => send mail cho KH
                        guaranteeCommonService.sendMailFail(guaranteeHistory);
                        //send mail cho CCO1 vao BPM huy ho so
                        if (StringUtil.isNotEmpty(guaranteeHistory.getTransnRefer())
                                && StringUtil.isNotEmpty(guaranteeHistory.getBpmCode())
                                && StringUtil.isNotEmpty(guaranteeHistory.getEmailUser())) {
                            guaranteeCommonService.sendMailCancel(guaranteeHistory, AppConstant.MAIL_TEMPLATE.GUARANTEE_CANCEL);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("Failed To Sync Guarantee To BPM", ex);
            throw new LogicException("GUARANTEE.SYNC.BPM.ERROR", ErrorCode.MS_INTERNAL_SERVER_ERROR);
        }
        logger.info(" ******* End Sync Guarantee To BPM TotalTime: {} ms", System.currentTimeMillis() - startTime);
        return true;
    }

    public boolean uploadToMinIO(GuaranteeUploadMinIORequest input) {
        boolean result = false;
        long startTime = System.currentTimeMillis();
        try {
            logger.info(" ******* Start Retry Upload File To MinIO *******");
            List<BbGuaranteeFileAttachment> listFile = new ArrayList<>();
            boolean checkAttachType = true;
            if (input == null || input.getFileId() == null) {
                listFile = fileAttachmentRepository.getListRetry(numUploadMax);
            } else {
                BbGuaranteeFileAttachment fileAttachment = fileAttachmentRepository.findById(Integer.parseInt(input.getFileId()));
                if (fileAttachment != null) {
                    listFile.add(fileAttachment);
                }
                checkAttachType = false;
            }
            result = guaranteeCommonService.uploadFileToMinIO(listFile, null, checkAttachType);
        } catch (Exception ex) {
            logger.error("Failed Retry Upload File To MinIO", ex);
        }
        logger.info(" ******* End Retry Upload File To MinIO TotalTime: {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    public boolean cancelGuarantee() {
        boolean result = false;
        long startTime = System.currentTimeMillis();
        try {
            logger.info(" ******* Start Cancel Guarantee Expired To BPM *******");
            List<BbGuaranteeHistory> guaranteeHistoryList = bbGuaranteeHistoryRepository.getGuaranteeCancel();
            if (!CollectionUtils.isEmpty(guaranteeHistoryList)) {
                for (BbGuaranteeHistory guaranteeHistory : guaranteeHistoryList) {
                    //huy thong tin bao lanh tren BPM
                    String reason = AppConstant.STATUS.CKRJ.equals(guaranteeHistory.getStatus()) ? AppConstant.REASON_CANCEL_CHECKER_REJECT
                            : AppConstant.REASON_CANCEL_CHECKER_EXPIRED;
                    ApiResponseBpm<Object, Object> responseBpm = bpmService.makePostCall(ApiGwConstants.BPM_CONFIG_GATEWAY.GUARANTEE_CANCEL,
                            guaranteeCommonService.builGuaranteeCancel(guaranteeHistory, reason),
                            new TypeReference<>() {}, null, guaranteeHistory.getTransnRefer() != null
                                    ? guaranteeHistory.getTransnRefer() : guaranteeHistory.getTranSn(), null);
                    if (ResponseUtils.isSuccess(responseBpm) || bpmService.isCanceled(responseBpm)) {
                        result = true;
                        guaranteeHistory.setStatusSync(AppConstant.STATUS.CANC);
                        logger.info("Cancel Guarantee Expired To BPM success, tranSn={}", guaranteeHistory.getTransnRefer());
                    } else {
                        logger.info("Cancel Guarantee Expired To BPM fail, tranSn={}", guaranteeHistory.getTransnRefer());
                        guaranteeHistory.setStatusSync(AppConstant.STATUS.FAIL);
                        if (StringUtil.isNotEmpty(guaranteeHistory.getBpmCode())
                                && StringUtil.isNotEmpty(guaranteeHistory.getEmailUser())) {
                            //send mail cho CCO1 vao BPM huy ho so
                            guaranteeCommonService.sendMailCancel(guaranteeHistory,
                                    AppConstant.MAIL_TEMPLATE.GUARANTEE_CANCEL_FAIL);
                        }
                    }
                    bbGuaranteeHistoryRepository.save(guaranteeHistory);
                }
            }
        } catch (Exception ex) {
            logger.error("Failed Cancel Guarantee Expired To BPM: {}", ex);
        }
        logger.info(" ******* End Cancel Guarantee Expired To BPM TotalTime: {} ms", System.currentTimeMillis() - startTime + " *******");
        return result;
    }

}
