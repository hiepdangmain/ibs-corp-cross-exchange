package com.msb.ibs.corp.cross.exchange.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.msb.ibs.common.constant.AppConstants;
import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.enums.MailType;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.integration.minio.FileInput;
import com.msb.ibs.common.request.InputSendMailData;
import com.msb.ibs.common.utils.DateUtil;
import com.msb.ibs.common.utils.FileTools;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.core.utils.MessageUtils;
import com.msb.ibs.corp.cross.exchange.application.constants.ApiGwConstants;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.enums.AttachmentTypeEnum;
import com.msb.ibs.corp.cross.exchange.application.enums.ProcessTypeEnum;
import com.msb.ibs.corp.cross.exchange.application.model.DetailGuarantee;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeFeeRequest;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeFeeResponse;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuaranteeInit;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeHistoryDetailDto;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeLog;
import com.msb.ibs.corp.cross.exchange.domain.integration.input.*;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.ApiResponseBpm;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.OutputFeeBpm;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.OutputStatusBpm;
import com.msb.ibs.corp.cross.exchange.domain.repository.*;
import com.msb.ibs.corp.cross.exchange.domain.service.Common.CommonService;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService.GuaranteeMetaDataService;
import com.msb.ibs.corp.cross.exchange.infrastracture.utils.MimeTypes;
import com.msb.ibs.corp.cross.exchange.infrastracture.utils.ResponseUtils;
import com.msb.ibs.corp.main.service.domain.contants.MainConstants;
import com.msb.ibs.corp.main.service.domain.enums.TransactionStatusEnum;
import com.msb.ibs.corp.main.service.domain.input.CorpInfoInput;
import com.msb.ibs.corp.main.service.domain.input.GetListEmailInput;
import com.msb.ibs.corp.main.service.domain.output.CorpInfoOutput;
import com.msb.ibs.corp.main.service.domain.output.GetUserInternalResponse;
import com.msb.ibs.corp.main.service.domain.service.CustomerService;
import com.msb.ibs.corp.main.service.domain.service.DocumentCertService;
import com.msb.ibs.corp.main.service.domain.service.MinIOService;
import com.msb.ibs.corp.main.service.domain.service.UserService;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GuaranteeCommonService {

    public static final Logger logger = LoggerFactory.getLogger(GuaranteeCommonService.class);
    private final CustomerService customerService;
    private BbGuaranteeHistoryRepository guaranteeHistoryRepository;
    private BbGuaranteeDocumentRepository documentRepository;
    private BbGuaranteeDocumentConfigRepository guaranteeDocumentConfigRepository;
    private final CommonService commonService;
    private final MessageUtils messageUtils;
    private final GuaranteeMetaDataService guaranteeMetaDataService;
    private final DocumentCertService documentCertService;
    private final MinIOService minIOService;
    private final String minIOBucket;
    private final String minIODefaultFolder;
    private final BbGuaranteeFileAttachmentRepository fileAttachmentRepository;
    private final Integer minIOUploadInterval;
    private final BbGuaranteeLogRepository bbGuaranteeLogRepository;
    private BbGuaranteeHistoryDetailRepository guaranteeHistoryDetailRepository;
    private final BpmService bpmService;
    private final UserService userService;
    private final MessageUtils messageUtil;
    private final boolean blockFee;

    public GuaranteeCommonService(CustomerService customerService, BbGuaranteeHistoryRepository guaranteeHistoryRepository,
                                  BbGuaranteeDocumentRepository documentRepository,
                                  BbGuaranteeDocumentConfigRepository guaranteeDocumentConfigRepository,
                                  BbGuaranteeHistoryDetailRepository guaranteeHistoryDetailRepository,
                                  CommonService commonService,
                                  MessageUtils messageUtils,
                                  GuaranteeMetaDataService guaranteeMetaDataService,
                                  DocumentCertService documentCertService,
                                  MinIOService minIOService,
                                  Environment environment,
                                  BbGuaranteeFileAttachmentRepository fileAttachmentRepository,
                                  BbGuaranteeLogRepository bbGuaranteeLogRepository,
                                  BpmService bpmService,
                                  UserService userService,
                                  MessageUtils messageUtil) {
        this.customerService = customerService;
        this.guaranteeHistoryRepository = guaranteeHistoryRepository;
        this.documentRepository = documentRepository;
        this.guaranteeDocumentConfigRepository = guaranteeDocumentConfigRepository;
        this.guaranteeHistoryDetailRepository =guaranteeHistoryDetailRepository;
        this.commonService = commonService;
        this.messageUtils = messageUtils;
        this.guaranteeMetaDataService = guaranteeMetaDataService;
        this.documentCertService = documentCertService;
        this.minIOBucket = environment.getProperty("minio.bucket.name");
        this.minIODefaultFolder = environment.getProperty("minio.default.folder");
        this.minIOUploadInterval = Integer.parseInt(environment.getProperty("minio.upload.interval"));
        this.blockFee = Boolean.parseBoolean(environment.getProperty("guarantee.block.fee"));
        this.minIOService = minIOService;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.bbGuaranteeLogRepository = bbGuaranteeLogRepository;
        this.bpmService = bpmService;
        this.userService = userService;
        this.messageUtil = messageUtil;
    }

    public OutputGuaranteeInit checkCorpInfo(String cifNo, Integer corpId) throws LogicException {
        OutputGuaranteeInit output = new OutputGuaranteeInit();
        CorpInfoInput input = new CorpInfoInput();
        input.setCifNo(cifNo);
        CorpInfoOutput corpInfo = customerService.getCustomerInfo(input);
        if (corpInfo == null) {
            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.IS.NULL");
        }

        BeanUtils.copyProperties(corpInfo, output);
        output.setListForm(documentRepository.findBbGuaranteeDocumentByType("FORM"));
        output.setListTypeGuarantee(documentRepository.findBbGuaranteeDocumentByType("TYPE_GUARANTEE"));
        output.setListCurrency(documentRepository.findBbGuaranteeDocumentByType("CURRENCY"));
        output.setListValidityPeriodStart(documentRepository.findBbGuaranteeDocumentByType("VALIDITY_PERIOD_START"));
        output.setListValidityPeriodEnd(documentRepository.findBbGuaranteeDocumentByType("VALIDITY_PERIOD_END"));
        output.setListCommitGuarantee(documentRepository.findBbGuaranteeDocumentByType("COMMIT_FORM"));
        output.setListOrder(documentRepository.findBbGuaranteeDocumentByType("PARAM"));

        // lay list danh sach file dinh kem chung
        List<String> listFile1 = new ArrayList<>();
        listFile1.add("0");
        listFile1.add("1");
        List<String> listFile2 = new ArrayList<>();
        listFile2.add("0");
        listFile2.add("2");
        List<String> listFile3 = new ArrayList<>();
        listFile3.add("0");
        listFile3.add("3");
        List<String> listFile4 = new ArrayList<>();
        listFile4.add("0");
        listFile4.add("4");
        List<String> listFile5 = new ArrayList<>();
        listFile5.add("0");
        listFile5.add("5");
        List<String> listFile6 = new ArrayList<>();
        listFile6.add("0");
        listFile6.add("6");
        List<String> listFile7 = new ArrayList<>();
        listFile7.add("0");
        listFile7.add("7");
        List<String> listFile8 = new ArrayList<>();
        listFile8.add("0");
        listFile8.add("8");
        output.setListFileType1(guaranteeDocumentConfigRepository.findListDocumentConfig(listFile1));
        output.setListFileType2(guaranteeDocumentConfigRepository.findListDocumentConfig(listFile2));
        output.setListFileType3(guaranteeDocumentConfigRepository.findListDocumentConfig(listFile3));
        output.setListFileType4(guaranteeDocumentConfigRepository.findListDocumentConfig(listFile4));
        output.setListFileType5(guaranteeDocumentConfigRepository.findListDocumentConfig(listFile5));
        output.setListFileType6(guaranteeDocumentConfigRepository.findListDocumentConfig(listFile6));
        output.setListFileType7(guaranteeDocumentConfigRepository.findListDocumentConfig(listFile7));
        output.setListFileType8(guaranteeDocumentConfigRepository.findListDocumentConfig(listFile8));
        output.setListFileType9(guaranteeDocumentConfigRepository.findListDocumentConfig(List.of("0", "9")));
        output.setNewTranSn(null);
        //thong tin goi nho
        output.setListGuaranteeMetaData(guaranteeMetaDataService.getListBbGuaranteeMetaDataByCorpId(corpId));
        return output;
    }

    public boolean checkCertCode(String cifNo, String certCode) throws LogicException {
        CorpInfoInput input = new CorpInfoInput();
        input.setCifNo(cifNo);
        CorpInfoOutput corpInfo = customerService.getCustomerInfo(input);
        if (corpInfo == null) {
            throw new LogicException("ERROR.GUARANTEE.CORP.INFO.IS.NULL");
        }
        System.out.println("certCode: " +certCode );
        System.out.println("certCode Corp : " +corpInfo.getCertCode() );
        System.out.println("certCode validate : " +!certCode.equalsIgnoreCase(corpInfo.getCertCode()) );
        if (!certCode.equalsIgnoreCase(corpInfo.getCertCode()))
            return false;
        return true;
    }

    public void sendMail(BbGuaranteeHistory history, Map<String, GuaranteeHistoryDetailDto> details,
                         String templateCode, String mailType, String mailRM, String lang) {

        Map<String, Object> params = new HashMap<>();
        params.put("cifNo", history.getCifNo());
        params.put("corpName", history.getCorpName());
        params.put("tranSn", history.getTransnRefer() != null ? history.getTransnRefer() : history.getTranSn());
        params.put("createTime", DateUtil.formatDate(history.getCreateTime(),
                DateUtil.FORMAT_DATE_ddMMyyyyHHmmss));
        params.put("typeGuarantee", StringUtil.isEmpty(lang) ? getTypeGuaranteeName(history.getTypeGuarantee())
                : getTypeGuaranteeName(history.getTypeGuarantee(), lang));
        // format tien te
        if ("VND".equalsIgnoreCase(history.getCurrency())
                || "JPY".equalsIgnoreCase(history.getCurrency())) {
            DecimalFormat df = new DecimalFormat("#,##0");
            params.put("amount", df.format(history.getAmount()).toString());
        } else {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            params.put("amount", df.format(history.getAmount()).toString());
        }
        params.put("currency", history.getCurrency());

        params.put("receiveGuarantee", details.get("beneficiary").getValue());
        params.put("status", StringUtil.isEmpty(lang) ? getStatusName(history.getStatus()) : getStatusName(history.getStatus(), lang));
        params.put("updateContent", history.getUpdateContent());
        params.put("rejectContent", history.getRejectContent());
        params.put("startDate", "1".equals(details.get("guaranteeStart").getValue())
                ? details.get("startDate").getValue() : details.get("startEventDate").getValue());
        params.put("guaranteeStart", "1".equals(details.get("guaranteeStart").getValue())
                ? details.get("startDate").getValue() : details.get("startEventDate").getValue());
        params.put("guaranteeEnd", "1".equals(details.get("guaranteeEnd").getValue())
                ? details.get("expiredDate").getValue() : details.get("expectEndDate").getValue());
        params.put("guaranteeCode", history.getCodeGuarantee());
        params.put("makerMsb", history.getMakerMsb());
        params.put("emailUser", history.getEmailUser());
        params.put("phoneUser", history.getPhoneUser());
        // send mail to
        List<Integer> userIds = new ArrayList<>();
        List<String> listNotify = new ArrayList<>(Arrays.asList(details.get("notificationTo").getValue().split("##")));
        if (listNotify.size() > 0) {
            for (String item : listNotify) {
                if (AppConstant.NOTIFY_MAKER.equalsIgnoreCase(item)) {
                    userIds.add(history.getCreateBy());
                } else {
                    if (StringUtil.isNotEmpty(history.getListCkApprove())) {
                        List<String> checkerIds = new ArrayList<>(Arrays.asList(history.getListCkApprove().split(",")));
                        for (String s : checkerIds) {
                            userIds.add(Integer.valueOf(s));
                        }
                    }
                    if (history.getUpdateBy() != null && !userIds.contains(history.getUpdateBy())) {
                        userIds.add(Integer.valueOf(history.getUpdateBy()));
                    }
                }
            }
        } else {
             userIds = List.of(history.getCreateBy(), history.getUpdateBy());//maker,checker cuoi cung
        }
        InputSendMailData sendMailData = new InputSendMailData();
        sendMailData.setParams(params);
        sendMailData.setMailType(mailType);
        sendMailData.setTemplateCode(templateCode);
        sendMailData.setListUserId(userIds);
        sendMailData.setTranSn(history.getTranSn());
        sendMailData.setCcAddress(mailRM);
        sendMailData.setServiceType(ProcessTypeEnum.GUARANTEE.getKey());
        commonService.sendMail(sendMailData, null);
    }

    public String getTypeGuaranteeName(String type) {
        switch (type) {
            case AppConstant.TYPE_GUARANTEE_1:
                return messageUtils.getMessage("TYPE_GUARANTEE_1");
            case AppConstant.TYPE_GUARANTEE_2:
                return messageUtils.getMessage("TYPE_GUARANTEE_2");
            case AppConstant.TYPE_GUARANTEE_3:
                return messageUtils.getMessage("TYPE_GUARANTEE_3");
            case AppConstant.TYPE_GUARANTEE_4:
                return messageUtils.getMessage("TYPE_GUARANTEE_4");
            case AppConstant.TYPE_GUARANTEE_5:
                return messageUtils.getMessage("TYPE_GUARANTEE_5");
            case AppConstant.TYPE_GUARANTEE_6:
                return messageUtils.getMessage("TYPE_GUARANTEE_6");
            case AppConstant.TYPE_GUARANTEE_7:
                return messageUtils.getMessage("TYPE_GUARANTEE_7");
            case AppConstant.TYPE_GUARANTEE_8:
                return messageUtils.getMessage("TYPE_GUARANTEE_8");
            case AppConstant.TYPE_GUARANTEE_9:
                return messageUtils.getMessage("TYPE_GUARANTEE_9");
            default:
                return "";
        }
    }

    public String getStatusName(String status) {
        switch (status) {
            case AppConstant.STATUS.UPDA:
                return messageUtils.getMessage("STATUS_UPDA");
            case AppConstant.STATUS.REJE:
                return messageUtils.getMessage("STATUS_REJE");
            case AppConstant.STATUS.DSUC:
                return messageUtils.getMessage("STATUS_SUCC");
            case AppConstant.STATUS.CKNG:
                return messageUtils.getMessage("STATUS_CKNG");
            case AppConstant.STATUS.DUPD:
                return messageUtils.getMessage("STATUS_DUPD");
            case AppConstant.STATUS.DPEN:
                return messageUtils.getMessage("STATUS_DPEN");
            case AppConstant.STATUS.FAIL:
                return messageUtils.getMessage("STATUS_FAIL");
            default:
                return "";
        }
    }

    public String getTypeGuaranteeName(String type, String lang) {
        switch (type) {
            case AppConstant.TYPE_GUARANTEE_1:
                return messageUtils.getMessage("TYPE_GUARANTEE_1", null, new Locale(lang));
            case AppConstant.TYPE_GUARANTEE_2:
                return messageUtils.getMessage("TYPE_GUARANTEE_2", null, new Locale(lang));
            case AppConstant.TYPE_GUARANTEE_3:
                return messageUtils.getMessage("TYPE_GUARANTEE_3", null, new Locale(lang));
            case AppConstant.TYPE_GUARANTEE_4:
                return messageUtils.getMessage("TYPE_GUARANTEE_4", null, new Locale(lang));
            case AppConstant.TYPE_GUARANTEE_5:
                return messageUtils.getMessage("TYPE_GUARANTEE_5", null, new Locale(lang));
            case AppConstant.TYPE_GUARANTEE_6:
                return messageUtils.getMessage("TYPE_GUARANTEE_6", null, new Locale(lang));
            case AppConstant.TYPE_GUARANTEE_7:
                return messageUtils.getMessage("TYPE_GUARANTEE_7", null, new Locale(lang));
            case AppConstant.TYPE_GUARANTEE_8:
                return messageUtils.getMessage("TYPE_GUARANTEE_8", null, new Locale(lang));
            case AppConstant.TYPE_GUARANTEE_9:
                return messageUtils.getMessage("TYPE_GUARANTEE_9", null, new Locale(lang));
            default:
                return "";
        }
    }

    public String getStatusName(String status, String lang) {
        switch (status) {
            case AppConstant.STATUS.UPDA:
                return messageUtils.getMessage("STATUS_UPDA", null, new Locale(lang));
            case AppConstant.STATUS.REJE:
                return messageUtils.getMessage("STATUS_REJE", null, new Locale(lang));
            case AppConstant.STATUS.DSUC:
                return messageUtils.getMessage("STATUS_SUCC", null, new Locale(lang));
            case AppConstant.STATUS.CKNG:
                return messageUtils.getMessage("STATUS_CKNG", null, new Locale(lang));
            case AppConstant.STATUS.DUPD:
                return messageUtils.getMessage("STATUS_DUPD", null, new Locale(lang));
            case AppConstant.STATUS.DPEN:
                return messageUtils.getMessage("STATUS_DPEN", null, new Locale(lang));
            case AppConstant.STATUS.FAIL:
                return messageUtils.getMessage("STATUS_FAIL", null, new Locale(lang));
            default:
                return "";
        }
    }

    public void sendMailToTTV(BbGuaranteeHistory history, String templateCode, UserPrincipal userChecker, GetUserInternalResponse userMaker, String toAddress) {
        Map<String, Object> params = new HashMap<>();

        params.put("tranSn", history.getTransnRefer() != null ? history.getTransnRefer() : history.getTranSn());
        params.put("cifNo", history.getCifNo());
        params.put("corpName", history.getCorpName());
        // format tien te
        if ("VND".equalsIgnoreCase(history.getCurrency())
                || "JPY".equalsIgnoreCase(history.getCurrency())) {
            DecimalFormat df = new DecimalFormat("#,##0");
            params.put("amount", df.format(history.getAmount()).toString());
        } else {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            params.put("amount", df.format(history.getAmount()).toString());
        }
        params.put("currency", history.getCurrency());
        params.put("typeGuarantee", getTypeGuaranteeName(history.getTypeGuarantee()));
        params.put("createTime", DateUtil.formatDate(history.getCreateTime(),
                DateUtil.FORMAT_DATE_ddMMyyyyHHmmss));
        params.put("customerSendTime", DateUtil.formatDate(new Date(), DateUtil.FORMAT_DATE_ddMMyyyyHHmmss));
        params.put("userMaker", userMaker.getUserName());
        params.put("userChecker", userChecker.getUsername());
        params.put("emailChecker", userChecker.getEmail());
        params.put("emailMaker", userMaker.getEmail());

        InputSendMailData sendMailData = new InputSendMailData();
        sendMailData.setParams(params);
        sendMailData.setTemplateCode(templateCode);
        sendMailData.setToAddress(toAddress);
        sendMailData.setTranSn(history.getTranSn());
        sendMailData.setServiceType(ProcessTypeEnum.GUARANTEE.getKey());
        commonService.sendMail(sendMailData, null);
    }

    public String formatFilename(String fileName) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssss");
        String titleFile = formatter.format(new Date()).replace("-", "").replace(":", "").replace(" ", "").substring(2) + "_"
                + fileName;
        return titleFile;
    }

    public String genFilePath() {
        return MainConstants.UPLOAD_DIRECTORY_PRODUCTION + FileTools.getSubFolderByDate(new Date(), "yyyy/MM/") + "guarantee/";
    }

    public String getEmailRM(String cifNo) throws LogicException {
        GetListEmailInput input = new GetListEmailInput();
        input.setCifNo(cifNo);
        input.setDocumentIds(Arrays.asList(new Long[]{9L, 15L, 16L}));
        String listEmail = documentCertService.getListEmail(input);
        if (StringUtil.isEmpty(listEmail)) {
            return "gpkpvl@msb.com.vn";
        }
        return listEmail;
    }

    public boolean uploadFileToMinIO(List<BbGuaranteeFileAttachment> fileAttachments, String tranSn, boolean checkAttachType) {
        try {
            if (!CollectionUtils.isEmpty(fileAttachments)) {
                for (BbGuaranteeFileAttachment attachment : fileAttachments) {
                    if (StringUtil.isNotEmpty(attachment.getFileName())
                            && (StringUtil.isNotEmpty(attachment.getTransRefer()) || StringUtil.isNotEmpty(tranSn))
                            && (!checkAttachType || AttachmentTypeEnum.SERVER.value().equalsIgnoreCase(attachment.getAttachmentType()))) {
                        String pathFile = null;
                        if (AttachmentTypeEnum.SERVER.value().equalsIgnoreCase(attachment.getAttachmentType())) {
                            pathFile = attachment.getPathFile();
                        } else if (!checkAttachType) {
                            pathFile = attachment.getPathFileLocal();
                        }
                        if (pathFile == null) return false;

                        byte[] content = Files.readAllBytes(Paths.get(pathFile));
                        String mimeType = MimeTypes.getMimeType(FilenameUtils.getExtension(attachment.getFileName()));
                        FileInput fileInput = new FileInput();
                        fileInput.setFilePath(pathFile);
                        fileInput.setContent(content);
                        fileInput.setMimeType(mimeType);
                        fileInput.setBucket(minIOBucket);
                        fileInput.setDefaultBaseFolder(minIODefaultFolder);
                        fileInput.setName(StringUtil.isNotEmpty(tranSn) ? tranSn + "/" + attachment.getFileName()
                                : attachment.getTransRefer() + "/" + attachment.getFileName());
                        //add number upload
                        attachment.setNumUpload(attachment.getNumUpload() != null ? (attachment.getNumUpload() + 1) : 1);
                        //next time retry upload
                        attachment.setNextTime(DateUtil.addMinutes(new Date(), minIOUploadInterval));
                        try {
                            logger.info("Begin Upload File To MinIO For TranSn {} By AttachmentId {}", attachment.getTransRefer(), attachment.getId());
                            boolean result = minIOService.uploadFile(fileInput);
                            if (result) {
                                attachment.setStatus(TransactionStatusEnum.SUCCESS.getValue());
                                attachment.setAttachmentType(AttachmentTypeEnum.MIN_IO.value());
                                attachment.setBucket(minIOBucket);
                                if (attachment.getPathFileLocal() == null)
                                    attachment.setPathFileLocal(attachment.getPathFile());
                                attachment.setPathFile(minIODefaultFolder + fileInput.getName());
                                logger.info("Upload File MinIO Success For TranSn {} By AttachmentId {}", attachment.getTransRefer(), attachment.getId());
                            } else {
                                logger.info("Failed To Upload File MinIO For TranSn {} By AttachmentId {}", attachment.getTransRefer(), attachment.getId());
                                attachment.setStatus(TransactionStatusEnum.FAIL.getValue());
                            }
                        } catch (Exception e) {
                            attachment.setStatus(TransactionStatusEnum.FAIL.getValue());
                            logger.info("Failed To Upload File MinIO For TranSn {} By AttachmentId {}", attachment.getTransRefer(), attachment.getId());
                            logger.error("Error ", e);
                            return false;
                        }
                        fileAttachmentRepository.save(attachment);
                    }
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed To DownloadFile From Server ", e);
        }
        return false;
    }

    public boolean uploadFileToMinIO(List<BbGuaranteeFileAttachment> fileAttachments, String tranSn) {
        return uploadFileToMinIO(fileAttachments, tranSn, true);
    }

    public String getFilePathMinIO (String tranSn, String fileName) {
        return minIODefaultFolder + tranSn + "/" + fileName;
    }

    public byte[] downloadFileMinIO(BbGuaranteeFileAttachment attachment) {
        try {
            FileInput fileInput = new FileInput();
            fileInput.setBucket(StringUtil.isNotEmpty(attachment.getBucket()) ? attachment.getBucket() : minIOBucket);
            fileInput.setObject(attachment.getPathFile());
            return minIOService.downloadFile(fileInput);
        } catch (Exception e) {
            logger.error("Failed To DownloadFile From MinIO", e);
        }
        return null;
    }

    public Map<String, Object> getParamEmail(BbGuaranteeHistory history, Map<String, GuaranteeHistoryDetailDto> details) {
        Map<String, Object> params = new HashMap<>();
        params.put("cifNo", history.getCifNo());
        params.put("corpName", history.getCorpName());
        params.put("tranSn", history.getTransnRefer() != null ? history.getTransnRefer() : history.getTranSn());
        params.put("createTime", DateUtil.formatDate(history.getCreateTime(),
                DateUtil.FORMAT_DATE_ddMMyyyyHHmmss));
        params.put("typeGuarantee", getTypeGuaranteeName(history.getTypeGuarantee()));
        // format tien te
        if ("VND".equalsIgnoreCase(history.getCurrency())
                || "JPY".equalsIgnoreCase(history.getCurrency())) {
            DecimalFormat df = new DecimalFormat("#,##0");
            params.put("amount", df.format(history.getAmount()));
        } else {
            DecimalFormat df = new DecimalFormat("#,##0.00");
            params.put("amount", df.format(history.getAmount()));
        }
        params.put("currency", history.getCurrency());

        params.put("receiveGuarantee", details.get(AppConstant.BB_GUARANTEE_FIELD.beneficiary).getValue());
        params.put("status", getStatusName(history.getStatus()));
        params.put("updateContent", history.getUpdateContent());
        params.put("rejectContent", history.getRejectContent());
        params.put("guaranteeStart", "1".equals(details.get(AppConstant.BB_GUARANTEE_FIELD.guaranteeStart).getValue())
                ? details.get(AppConstant.BB_GUARANTEE_FIELD.startDate).getValue()
                : details.get(AppConstant.BB_GUARANTEE_FIELD.startEventDate).getValue());
        params.put("guaranteeEnd", "1".equals(details.get(AppConstant.BB_GUARANTEE_FIELD.guaranteeEnd).getValue())
                ? details.get(AppConstant.BB_GUARANTEE_FIELD.expiredDate).getValue()
                : details.get(AppConstant.BB_GUARANTEE_FIELD.expectEndDate).getValue());
        params.put("guaranteeCode", history.getCodeGuarantee());
        params.put("makerMsb", history.getMakerMsb());
        params.put("emailUser", history.getEmailUser());
        params.put("phoneUser", history.getPhoneUser());
        return params;
    }

    public InputGuaranteeCreate buildGuaranteeCreate(BbGuaranteeHistory bbGuaranteeHistory) {
        String tranSn = bbGuaranteeHistory.getTranSn();
        List<BbGuaranteeHistoryDetail> historyDetails = guaranteeHistoryDetailRepository.selectByTranSn(tranSn);
        Map<String, String> details = historyDetails.stream().collect(
                Collectors.toMap(BbGuaranteeHistoryDetail::getParam, BbGuaranteeHistoryDetail::getValue, (r1, r2) -> r1));

        InputGuaranteeCreate input = new InputGuaranteeCreate();
        input.setIbCorpId(tranSn);
        input.setCifNumber(Long.parseLong(bbGuaranteeHistory.getCifNo()));
        input.setBusinessCode(details.get(AppConstant.BB_GUARANTEE_FIELD.certCode));
        input.setReleaseForm(details.get(AppConstant.BB_GUARANTEE_FIELD.formGuarantee));
        input.setFormType(details.get(AppConstant.BB_GUARANTEE_FIELD.commitGuarantee));
        input.setCreditContractNumber(details.get(AppConstant.BB_GUARANTEE_FIELD.contractNo));
        if ("3".equals(input.getReleaseForm())) {
            input.setSwiftCode(details.get(AppConstant.BB_GUARANTEE_FIELD.swiftCode));
        }

        input.setInfoRequirement(this.buildInfoRequirement(bbGuaranteeHistory, details));
        input.setChecklist(this.buildCheckList(bbGuaranteeHistory, details.get(AppConstant.BB_GUARANTEE_FIELD.commit2Date),
                details.get(AppConstant.BB_GUARANTEE_FIELD.commit2)));
        input.setIbCorpMakerInfo(this.buildMakerInfo(bbGuaranteeHistory.getUserId()));
        return input;
    }

    public InputGuaranteeUpdate buildGuaranteeUpdate(BbGuaranteeHistory bbGuaranteeHistory) {
        List<BbGuaranteeHistoryDetail> historyDetails = guaranteeHistoryDetailRepository.selectByTranSn(bbGuaranteeHistory.getTranSn());
        Map<String, String> details = historyDetails.stream().collect(
                Collectors.toMap(BbGuaranteeHistoryDetail::getParam, BbGuaranteeHistoryDetail::getValue, (r1, r2) -> r1));

        InputGuaranteeUpdate input = new InputGuaranteeUpdate();
        input.setIbCorpId(bbGuaranteeHistory.getTransnRefer());
        input.setCifNumber(Long.parseLong(bbGuaranteeHistory.getCifNo()));
        input.setBusinessCode(details.get(AppConstant.BB_GUARANTEE_FIELD.certCode));
        input.setReleaseForm(details.get(AppConstant.BB_GUARANTEE_FIELD.formGuarantee));
        input.setFormType(details.get(AppConstant.BB_GUARANTEE_FIELD.commitGuarantee));
        input.setCreditContractNumber(details.get(AppConstant.BB_GUARANTEE_FIELD.contractNo));
        if ("3".equals(input.getReleaseForm())) {
            input.setSwiftCode(details.get(AppConstant.BB_GUARANTEE_FIELD.swiftCode));
        }
        input.setInfoRequirement(this.buildInfoRequirement(bbGuaranteeHistory, details));
        input.setChecklist(this.buildCheckList(bbGuaranteeHistory, details.get(AppConstant.BB_GUARANTEE_FIELD.commit2Date),
                details.get(AppConstant.BB_GUARANTEE_FIELD.commit2)));
        return input;
    }

    private InfoRequirement buildInfoRequirement(BbGuaranteeHistory bbGuaranteeHistory, Map<String, String> details) {
        InfoRequirement infoRequirement = new InfoRequirement();
        infoRequirement.setGuaranteeType(details.get(AppConstant.BB_GUARANTEE_FIELD.typeGuarantee));
        infoRequirement.setCurrencyCode(details.get(AppConstant.BB_GUARANTEE_FIELD.currency));
        infoRequirement.setIssueAmount(details.get(AppConstant.BB_GUARANTEE_FIELD.amount));
        if (bbGuaranteeHistory.getUpdateTime() != null) {
            infoRequirement.setIssueDate(DateUtil.formatDate(bbGuaranteeHistory.getUpdateTime(), DateUtil.FORMAT_DATE_ddMMyyyy));
        } else {
            infoRequirement.setIssueDate(DateUtil.formatDate(new Date(), DateUtil.FORMAT_DATE_ddMMyyyy));
        }
        infoRequirement.setEffectDate(details.get(AppConstant.BB_GUARANTEE_FIELD.startDate));
        infoRequirement.setExpireDate(details.get(AppConstant.BB_GUARANTEE_FIELD.expiredDate));
        infoRequirement.setStartedEvent(details.get(AppConstant.BB_GUARANTEE_FIELD.startEventDate));
        infoRequirement.setExpireEvent(details.get(AppConstant.BB_GUARANTEE_FIELD.endEventDate));
        infoRequirement.setExpireDateExpect(details.get(AppConstant.BB_GUARANTEE_FIELD.expectEndDate));
        infoRequirement.setFeeAccount(details.get(AppConstant.BB_GUARANTEE_FIELD.accountPayment));
        infoRequirement.setGuaranteedGetName(details.get(AppConstant.BB_GUARANTEE_FIELD.beneficiary));
        infoRequirement.setGuaranteedGetAddress(details.get(AppConstant.BB_GUARANTEE_FIELD.beneficiaryAddress));
        infoRequirement.setGuaranteedPurpose(details.get(AppConstant.BB_GUARANTEE_FIELD.formatTextGuarantee));
        infoRequirement.setOtherCommitments(details.get(AppConstant.BB_GUARANTEE_FIELD.commitOtherContent));
        return infoRequirement;
    }

    private List<CheckListItem> buildCheckList(BbGuaranteeHistory bbGuaranteeHistory, String commit2Date, String commit2) {
        String tranSn = bbGuaranteeHistory.getTransnRefer() != null ? bbGuaranteeHistory.getTransnRefer() : bbGuaranteeHistory.getTranSn();
        List<BbGuaranteeFileAttachment> listFile = fileAttachmentRepository.findByTranSn(bbGuaranteeHistory.getTranSn());
        List<CheckListItem> checkListItems = new ArrayList<>();
        boolean hasFile = false;
        for (BbGuaranteeFileAttachment fileAttachment: listFile) {
            FileItem fileItem = new FileItem();
            fileItem.setPath(AttachmentTypeEnum.MIN_IO.value().equals(fileAttachment.getAttachmentType()) ? fileAttachment.getPathFile()
                    : this.getFilePathMinIO(tranSn, fileAttachment.getFileName()));
            fileItem.setName(fileAttachment.getId() + "_" + fileAttachment.getFileName());
            fileItem.setFileId(fileAttachment.getId() + "");
            if ("Y".equals(commit2) && (fileAttachment.getDocumentId().intValue() == 6
                    || fileAttachment.getDocumentId().intValue() == 11)) {
                CheckListItemExt checkListItem = new CheckListItemExt();
                checkListItem.setCode(fileAttachment.getDocumentId() + "");
                checkListItem.setDatePay(commit2Date);
                checkListItem.setFiles(List.of(fileItem));
                checkListItems.add(checkListItem);
                hasFile = true;
            } else {
                CheckListItem checkListItem = new CheckListItem();
                checkListItem.setCode(fileAttachment.getDocumentId() + "");
                checkListItem.setFiles(List.of(fileItem));
                checkListItems.add(checkListItem);
            }
        }
        if ("Y".equals(commit2) && !hasFile) {
            if (AppConstant.TYPE_GUARANTEE_2.equals(bbGuaranteeHistory.getTypeGuarantee())) {
                //bao lanh thuc hien hop dong
                CheckListItemExt checkListItem = new CheckListItemExt();
                checkListItem.setCode("6");
                checkListItem.setDatePay(commit2Date);
                checkListItem.setFiles(new ArrayList<>());
                checkListItems.add(checkListItem);
            } else if (AppConstant.TYPE_GUARANTEE_4.equals(bbGuaranteeHistory.getTypeGuarantee())) {
                //bao lanh tam ung
                CheckListItemExt checkListItem = new CheckListItemExt();
                checkListItem.setCode("11");
                checkListItem.setDatePay(commit2Date);
                checkListItem.setFiles(new ArrayList<>());
                checkListItems.add(checkListItem);
            }
        }
        //mau de nghi
        this.createProposalForm(bbGuaranteeHistory, checkListItems);
        return checkListItems;
    }

    private void createProposalForm (BbGuaranteeHistory bbGuaranteeHistory, List<CheckListItem> checkListItems) {
        BbGuaranteeFileAttachment fileAttachment = fileAttachmentRepository.findByTranSnAndDocumentId(bbGuaranteeHistory.getTranSn(), 2);
        if (fileAttachment == null) {
            fileAttachment = new BbGuaranteeFileAttachment();
            fileAttachment.setFileName(this.formatFilename(AppConstant.GUARANTEE_FILE_PROPOSAL_FORM));
            fileAttachment.setDocumentId(2);
            fileAttachment.setPathFile(null);
            fileAttachment.setAttachmentType(AttachmentTypeEnum.SERVER.value());
            fileAttachment.setCorpId(-1);
            fileAttachment.setStatus(AppConstant.STATUS_RECORD.NEWR);
            fileAttachment.setCreateBy(-1);
            fileAttachment.setCreateTime(new Date());
            fileAttachment.setTransRefer(bbGuaranteeHistory.getTranSn());
            fileAttachmentRepository.save(fileAttachment);

            FileItem fileItem = new FileItem();
            String tranSn = bbGuaranteeHistory.getTransnRefer() != null ? bbGuaranteeHistory.getTransnRefer() : bbGuaranteeHistory.getTranSn();
            fileItem.setPath(this.getFilePathMinIO(tranSn, fileAttachment.getFileName()));
            fileItem.setName(fileAttachment.getId() + "_" + fileAttachment.getFileName());
            fileItem.setFileId(fileAttachment.getId() + "");
            CheckListItem checkListItem = new CheckListItem();
            checkListItem.setCode(fileAttachment.getDocumentId() + "");
            checkListItem.setFiles(List.of(fileItem));
            checkListItems.add(checkListItem);
        }
    }

    public InputGuaranteeCancel builGuaranteeCancel(BbGuaranteeHistory bbGuaranteeHistory, String reason) {
        InputGuaranteeCancel input = new InputGuaranteeCancel();
        input.setIbCorpId(bbGuaranteeHistory.getTransnRefer() != null
                ? bbGuaranteeHistory.getTransnRefer() : bbGuaranteeHistory.getTranSn());
        input.setCifNumber(Long.parseLong(bbGuaranteeHistory.getCifNo()));
        input.setFeedBack(reason);
        return input;
    }

    public InputGuaranteeMakerInfo buildMakerInfo(Integer userId) {
        InputGuaranteeMakerInfo input = new InputGuaranteeMakerInfo();
        try {
            List<GetUserInternalResponse> listUser = userService.getUserInternalResponse(List.of(userId), null);
            if (!CollectionUtils.isEmpty(listUser)) {
                GetUserInternalResponse userInfo = listUser.get(0);
                input.setMakerName(userInfo.getNick());
                input.setMakerEmail(userInfo.getEmail());
                input.setMakerPhoneNumber(userInfo.getMobile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }

    public void uploadFileToMinIO(BbGuaranteeHistory guaranteeHistory) {
        String tranSn = guaranteeHistory.getTransnRefer() != null ? guaranteeHistory.getTransnRefer() : guaranteeHistory.getTranSn();
        List<BbGuaranteeFileAttachment> fileAttachments = fileAttachmentRepository.findByTransRefer(guaranteeHistory.getTranSn());
        this.uploadFileToMinIO(fileAttachments, tranSn);
    }

    //gui mail cho RM vao BPM tiep nhan yeu cau bao lanh
    public void sendEmail(BbGuaranteeHistory guaranteeHistory) {
        try {
            GetListEmailInput emailInput = new GetListEmailInput();
            emailInput.setCifNo(guaranteeHistory.getCifNo());
            emailInput.setDocumentIds(List.of(9L, 15L, 16L));
            String listMail = documentCertService.getListEmail(emailInput);
            if (StringUtil.isEmpty(listMail)) {
                listMail = "gpkpvl@msb.com.vn";
            }
            String mailTemplate = "";
            List<Integer> userMaker = new ArrayList<>();
            userMaker.add(guaranteeHistory.getUserId());
            List<GetUserInternalResponse> data = userService.getUserInternalResponse(userMaker, null);
            UserPrincipal userChecker = new UserPrincipal();
            userChecker.setUserId(guaranteeHistory.getUpdateBy());
            userChecker.setUsername(guaranteeHistory.getCustomerCkName());
            userChecker.setEmail(guaranteeHistory.getEmailChecker());
            if (StringUtil.isEmpty(guaranteeHistory.getTransnRefer())) {
                this.saveLog(AppConstant.IBS_ADMIN_ACTION.SEND_TO_MSB, AppConstant.STATUS.PEND,
                        userChecker, guaranteeHistory);
                mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_TO_MSB;
            } else {
                this.saveLog(AppConstant.IBS_ADMIN_ACTION.SEND_TO_MSB, AppConstant.STATUS.DPEN,
                        userChecker, guaranteeHistory);
                mailTemplate = AppConstant.MAIL_TEMPLATE.GUARANTEE_TO_MSB_EDIT;
            }
            this.sendMailToTTV(guaranteeHistory, mailTemplate, userChecker, data.get(0), listMail);
        } catch (Exception ex) {
            logger.error("ERROR Send mail to MSB: {}", ex);
        }
    }

    public void saveLog(String action, String newStatus, UserPrincipal principal, BbGuaranteeHistory guaranteeHistory) {
        BbGuaranteeLog log = new BbGuaranteeLog();
        BeanUtils.copyProperties(guaranteeHistory, log);
        log.setTranSn(guaranteeHistory.getTranSn());
        log.setOldStatus(guaranteeHistory.getStatus());
        log.setNewStatus(newStatus);
        log.setAction(action);
        log.setCreateBy(principal.getUserId());
        log.setCustomerSendTime(new Date());
        bbGuaranteeLogRepository.save(log);
    }

    public void checkLimit(String cifNo, String tranSn) throws LogicException {
        try {
            ApiResponseBpm<Object, Object> responseBpm = bpmService.makePostCall(ApiGwConstants.BPM_CONFIG_GATEWAY.GUARANTEE_CHECK_LIMIT,
                    new InputGuaranteeCheckLimit(Long.parseLong(cifNo)), new TypeReference<>() {}, null,
                    tranSn, ThreadContext.get(AppConstants.Header.Request_Id));
            if (ResponseUtils.isSuccess(responseBpm)) {
                OutputStatusBpm outputStatusBpm = bpmService.parseStatus(responseBpm);
                if (outputStatusBpm != null) {
                    String errorCode = outputStatusBpm.getCode();
                    if ("N".equalsIgnoreCase(errorCode)) {
                        throw new LogicException("ERROR.GUARANTEE.CUSTOMER.NOT.LIMIT");
                    } else if ("E".equalsIgnoreCase(errorCode)) {
                        throw new LogicException("ERROR.GUARANTEE.CHECK.LIMIT.ERROR");
                    }
                } else {
                    throw new LogicException("ERROR.GUARANTEE.CHECK.LIMIT.ERROR");
                }
            } else {
                throw new LogicException("ERROR.GUARANTEE.CHECK.LIMIT.ERROR");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new LogicException("ERROR.GUARANTEE.CHECK.LIMIT.ERROR");
        }
    }

    public GuaranteeFeeResponse getFee(GuaranteeFeeRequest request) throws LogicException {
        Map<String, String> details;
        try {
            if (StringUtil.isNotEmpty(request.getTranSn())) {
                List<BbGuaranteeHistoryDetail> historyDetails = guaranteeHistoryDetailRepository.selectByTranSn(request.getTranSn());
                details = historyDetails.stream().collect(
                        Collectors.toMap(BbGuaranteeHistoryDetail::getParam, BbGuaranteeHistoryDetail::getValue, (r1, r2) -> r1));
            } else {
                details = request.getListOrder().stream().collect(
                        Collectors.toMap(DetailGuarantee::getParams, DetailGuarantee::getValue, (r1, r2) -> r1));
            }
            InputGuaranteeFee inputGuaranteeFee = new InputGuaranteeFee();
            inputGuaranteeFee.setFeeAccount(details.get(AppConstant.BB_GUARANTEE_FIELD.accountPayment));
            inputGuaranteeFee.setIssueAmount(new BigDecimal(details.get(AppConstant.BB_GUARANTEE_FIELD.amount)));
            inputGuaranteeFee.setCurrencyCode(details.get(AppConstant.BB_GUARANTEE_FIELD.currency));
            inputGuaranteeFee.setGuaranteeType(details.get(AppConstant.BB_GUARANTEE_FIELD.typeGuarantee));
            inputGuaranteeFee.setFormType(details.get(AppConstant.BB_GUARANTEE_FIELD.commitGuarantee));
            inputGuaranteeFee.setTypeBusiness("PH");
//            inputGuaranteeFee.setEffectDate(details.get(AppConstant.BB_GUARANTEE_FIELD.startDate));
            inputGuaranteeFee.setIssueDate(DateUtil.formatDate(new Date(), DateUtil.FORMAT_DATE_ddMMyyyy));
            String expireDate = "1".equals(details.get(AppConstant.BB_GUARANTEE_FIELD.guaranteeEnd))
                    ? details.get(AppConstant.BB_GUARANTEE_FIELD.expiredDate)
                    : details.get(AppConstant.BB_GUARANTEE_FIELD.expectEndDate);
            inputGuaranteeFee.setExpireDate(expireDate);
            String effectDate = "1".equals(details.get(AppConstant.BB_GUARANTEE_FIELD.startDate))
                    ? details.get(AppConstant.BB_GUARANTEE_FIELD.startDate)
                    : DateUtil.formatDate(new Date(), DateUtil.FORMAT_DATE_ddMMyyyy);
            inputGuaranteeFee.setEffectDate(effectDate);


            ApiResponseBpm<Object, Object> responseBpm = bpmService.makePostCall(ApiGwConstants.BPM_CONFIG_GATEWAY.GUARANTEE_FEE_PLUTUS,
                    inputGuaranteeFee, new TypeReference<>() {}, null,
                    request.getTranSn(), ThreadContext.get(AppConstants.Header.Request_Id));
            if (ResponseUtils.isSuccess(responseBpm)) {
                OutputFeeBpm outputFeeBpm = bpmService.parseDataFee(responseBpm);
                if (outputFeeBpm != null) {
                    return new GuaranteeFeeResponse(outputFeeBpm.getRootPrice(), inputGuaranteeFee.getCurrencyCode());
                } else {
                    throw new LogicException("ERROR.GUARANTEE.GET.FEE.ERROR");
                }
            } else {
                throw new LogicException("ERROR.GUARANTEE.GET.FEE.ERROR");
            }
        } catch (Exception e) {
            logger.error("ERROR get fee guarantee {}", e);
            throw new LogicException("ERROR.GUARANTEE.GET.FEE.ERROR");
        }
    }

    public void sendMailFail(BbGuaranteeHistory guaranteeHistory) {
        Map<String, GuaranteeHistoryDetailDto> details = new HashMap<>();
        List<BbGuaranteeHistoryDetail> historyDetails = guaranteeHistoryDetailRepository.selectByTranSn(guaranteeHistory.getTranSn());
        for (BbGuaranteeHistoryDetail detail : historyDetails) {
            GuaranteeHistoryDetailDto detailGuarantee = new GuaranteeHistoryDetailDto();
            BeanUtils.copyProperties(detail, detailGuarantee);
            details.put(detailGuarantee.getParam(), detailGuarantee);
        }
        InputSendMailData sendMailData = new InputSendMailData();
        sendMailData.setParams(this.getParamEmail(guaranteeHistory, details));
        sendMailData.setTemplateCode(AppConstant.MAIL_TEMPLATE.GUARANTEE_SYNC_FAIL);
        sendMailData.setMailType(MailType.TO_MAKER_CHECKER.getValue());
        sendMailData.setTranSn(guaranteeHistory.getTranSn());
        sendMailData.setServiceType(ProcessTypeEnum.GUARANTEE.getKey());
        sendMailData.setListUserId(List.of(guaranteeHistory.getCreateBy(), guaranteeHistory.getUpdateBy()));
        commonService.sendMail(sendMailData, null);
    }

    public void sendMailCancel(BbGuaranteeHistory history, String templateCode) {
        Map<String, Object> params = new HashMap<>();
        params.put("cifNo", history.getCifNo());
        params.put("corpName", history.getCorpName());
        params.put("bpmCode", history.getBpmCode());
        params.put("createTime", DateUtil.formatDate(history.getCreateTime(), DateUtil.FORMAT_DATE_ddMMyyyyHHmmss));
        params.put("updateTime", DateUtil.formatDate(history.getUpdateTime(), DateUtil.FORMAT_DATE_ddMMyyyyHHmmss));

        InputSendMailData sendMailData = new InputSendMailData();
        sendMailData.setParams(params);
        sendMailData.setTemplateCode(templateCode);
        sendMailData.setTranSn(history.getTranSn());
        sendMailData.setServiceType(ProcessTypeEnum.GUARANTEE.getKey());
        sendMailData.setToAddress(history.getEmailUser());
        commonService.sendMail(sendMailData, null);
    }

    public void deleteFileMinIO(BbGuaranteeHistory guaranteeHistory) {
        try {
            String tranSnOld = guaranteeHistory.getTransnRefer(); //cac file cua gd can xoa
            BbGuaranteeHistory bbGuaranteeHistoryUpdate = guaranteeHistoryRepository.getTranUpdateNotActiveLatest(guaranteeHistory.getTransnRefer());
            if (bbGuaranteeHistoryUpdate != null) {
                tranSnOld = bbGuaranteeHistoryUpdate.getTranSn();
            }

            List<BbGuaranteeFileAttachment> listFileRemove = fileAttachmentRepository.getListDelete(tranSnOld, guaranteeHistory.getTranSn());
            if (!CollectionUtils.isEmpty(listFileRemove)) {
                List<String> objects = listFileRemove.stream().filter(o -> !Objects.isNull(o.getPathFile()))
                        .map(BbGuaranteeFileAttachment::getPathFile).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(objects)) {
                    minIOService.deleteFile(minIOBucket, objects);
                }
                for (BbGuaranteeFileAttachment fileAttachment: listFileRemove) {
                    fileAttachment.setStatus(AppConstant.STATUS_RECORD.DLTD);
                    fileAttachmentRepository.save(fileAttachment);
                }
            }
        } catch (Exception e) {
            logger.error("Failed To deleteFileMinIO: {}", e);
        }
    }

}
