package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.integration.ecm.EcmResponse;
import com.msb.ibs.corp.cross.exchange.application.enums.AttachmentTypeEnum;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import com.msb.ibs.corp.main.service.domain.contants.MainConstants;
import com.msb.ibs.corp.main.service.domain.enums.TransactionStatusEnum;
import com.msb.ibs.corp.main.service.domain.file.FileSSHClient;
import com.msb.ibs.corp.main.service.domain.input.UploadFileEcmInput;
import com.msb.ibs.corp.main.service.domain.service.EcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeUploadEcmIBSAdminService {

    private static final Logger logger = LoggerFactory.getLogger(GuaranteeUploadEcmIBSAdminService.class);

    @Autowired
    private EcmService ecmService;

    @Autowired
    private BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository;

    @Autowired
    private BbGuaranteeFileAttachmentRepository fileAttachmentRepository;

    @Autowired
    private GuaranteeCommonService guaranteeCommonService;

    private BbGuaranteeFileAttachment attachment;

    private boolean uploadEcm = false;

    public Boolean uploadFileEcm(Integer fileId) throws LogicException {
        attachment = fileAttachmentRepository.findById(fileId);
        if (attachment == null) {
            throw new LogicException("ERROR.FILE.NOT.EXIST");
        }
        if (!AttachmentTypeEnum.SERVER.value().equals(attachment.getAttachmentType())) {
            throw new LogicException("ERROR.FILE.STATUS.INVALID");
        }
        BbGuaranteeHistory bbGuaranteeHistory = bbGuaranteeHistoryRepository.selectBytranSn(attachment.getTransRefer());
        if (bbGuaranteeHistory == null) {
            throw new BadRequestException("ERROR.GUARANTEE.CONVERT.REPORT.WRONG.TRAN.SN");
        }
        /*try {
            byte[] content = Files.readAllBytes(Paths.get(attachment.getPathFile()));
            int year = Calendar.getInstance().get(Calendar.YEAR);
            String folder = MainConstants.UPLOAD_DIRECTORY_PRODUCTION.concat(String.valueOf(year));
            String titleFile = guaranteeCommonService.formatFilename(attachment.getFileName());
            String fileName = guaranteeCommonService.formatFilename(attachment.getFileName());
            String folderName = bbGuaranteeHistory.getTransnRefer() != null ? bbGuaranteeHistory.getTransnRefer() : bbGuaranteeHistory.getTranSn();

            UploadFileEcmInput uploadFileEcmInput = new UploadFileEcmInput();
            uploadFileEcmInput.setContent(content);
            uploadFileEcmInput.setFileName(fileName);
            uploadFileEcmInput.setRootFolder(folder);
            uploadFileEcmInput.setFolderName(folderName);
            uploadFileEcmInput.setCifNo(bbGuaranteeHistory.getCifNo());
            uploadFileEcmInput.setTitle(titleFile);
            logger.info("Upload File ECM Begin For CifNo {} UserName {} tranSn {}", bbGuaranteeHistory.getCifNo(), "admin", folderName);
            EcmResponse response = ecmService.uploadFile(uploadFileEcmInput);
            if (response != null && response.getUploadFileResponse() != null
                    && response.getUploadFileResponse().getRespMessage() != null
                    && "0".equals(response.getUploadFileResponse().getRespMessage().getRespCode())) {
                String[] object = (String[]) response.getUploadFileResponse().getRespDomain().get("data");
                if (object != null) {
                    attachment.setAttachmentType(AttachmentTypeEnum.ECM.value());
                    attachment.setPathFile(object[0].replace("{", "").replace("}", ""));
                    fileAttachmentRepository.save(attachment);
                    logger.info("Upload File ECM Success For CifNo {} UserName {} tranSn {}", bbGuaranteeHistory.getCifNo(), "admin", folderName);
                    uploadEcm = true;
                } else { // insert vào DB local
                    logger.info("Upload File ECM Error For CifNo {} UserName {} tranSn {}", bbGuaranteeHistory.getCifNo(), "admin", folderName);
                    uploadEcm = false;
                }
            }
        } catch (Exception e) {
            logger.error("Failed To DownloadFile From SSH Server ", e);
            uploadEcm = false;
        }*/

        //hainq3 thay doi sang upload MinIO
        List<BbGuaranteeFileAttachment> fileAttachments = new ArrayList<>();
        fileAttachments.add(attachment);
        String tranSn = bbGuaranteeHistory.getTransnRefer() != null ? bbGuaranteeHistory.getTransnRefer() : bbGuaranteeHistory.getTranSn();
        uploadEcm = guaranteeCommonService.uploadFileToMinIO(fileAttachments, tranSn);
        return uploadEcm;
    }
}
