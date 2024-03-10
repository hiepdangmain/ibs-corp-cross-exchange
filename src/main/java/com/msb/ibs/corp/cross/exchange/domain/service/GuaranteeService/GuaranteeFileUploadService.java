package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.enums.AttachmentTypeEnum;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeUploadRequest;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import com.msb.ibs.corp.main.service.domain.file.FileSSHClient;
import com.msb.ibs.corp.main.service.domain.service.EcmService;
import com.msb.ibs.corp.main.service.domain.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;

@Service
public class GuaranteeFileUploadService {

    private static final Logger logger = LoggerFactory.getLogger(GuaranteeFileUploadService.class);

    private final FileSSHClient fileSSHClient;
    private final BbGuaranteeFileAttachmentRepository fileAttachmentRepository;
    private final GuaranteeCommonService guaranteeCommonService;
    private final EcmService ecmService;


    public GuaranteeFileUploadService(FileSSHClient fileSSHClient, BbGuaranteeFileAttachmentRepository fileAttachmentRepository,
                                      GuaranteeCommonService guaranteeCommonService,
                                      EcmService ecmService) {
        this.fileSSHClient = fileSSHClient;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.guaranteeCommonService = guaranteeCommonService;
        this.ecmService = ecmService;
    }

    public BbGuaranteeFileAttachment execute(GuaranteeUploadRequest request, InternalRequest internalRequest) throws LogicException, IOException {
        if (StringUtil.isEmpty(request.getFileName()) || StringUtil.isEmpty(request.getFileContent())
                || request.getDocumentId() == null) {
            throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.INPUT.IS.VALID");
        }

        // check k dc day len file Pdf dc sinh ra
        if (request.getDocumentId() != null) {
            if (request.getDocumentId() == 2) {
                throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.DOCUMENT.ID.IS.VALID");
            }
        }
        if (StringUtil.isNotEmpty(request.getDebt())) {
            if (!(request.getDebt().equalsIgnoreCase("Y") || request.getDebt().equalsIgnoreCase("N")))
                throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.INPUT.IS.VALID");
            if (request.getDebt().equalsIgnoreCase("Y")) {
                throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.INPUT.IS.VALID");
            }
        }
        if (StringUtil.isNotEmpty(request.getDebtDate())) {
            throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.INPUT.IS.VALID");
        }

        //upload to SSH server
        String filename = FileUtils.formatFilename(request.getFileName());
        String filePath = guaranteeCommonService.genFilePath();
        String fileFullPath = filePath + filename;

        /*File directory = new File(filePath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                logger.info("Directory {} is created!", filePath);
            } else {
                logger.info("Failed to create directory {}", filePath);
            }
        } else {
            logger.info("Directory {} is existed!", filePath);
        }*/

        File file = new File(fileFullPath);
        org.apache.commons.io.FileUtils.writeByteArrayToFile(file, Base64.getDecoder().decode(request.getFileContent()));

        BbGuaranteeFileAttachment fileAttachment = new BbGuaranteeFileAttachment();
        fileAttachment.setFileName(filename);
        fileAttachment.setDocumentId(request.getDocumentId());
        fileAttachment.setPathFile(fileFullPath);
        fileAttachment.setAttachmentType(AttachmentTypeEnum.SERVER.value());
        fileAttachment.setCorpId(internalRequest.getUserPrincipal().getCorpId());
        fileAttachment.setStatus(AppConstant.STATUS_RECORD.NEWR);
        fileAttachment.setCreateBy(internalRequest.getUserPrincipal().getUserId());
        fileAttachment.setCreateTime(new Date());
        fileAttachment.setDebt(request.getDebt());
        fileAttachment.setDebtDate(request.getDebtDate());
        fileAttachmentRepository.save(fileAttachment);

        /*//  upload ECM
        BbGuaranteeFileAttachment fileAttachment = null;
        byte[] content = Base64.getDecoder().decode(request.getFileContent().getBytes());
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String folder = MainConstants.UPLOAD_DIRECTORY_PRODUCTION.concat(String.valueOf(year));
        String fileName = guaranteeCommonService.formatFilename(request.getFileName());
        String folderName = request.getNewTranSn();

        UploadFileEcmInput uploadFileEcmInput = new UploadFileEcmInput();
        uploadFileEcmInput.setContent(content);
        uploadFileEcmInput.setFileName(fileName);
        uploadFileEcmInput.setRootFolder(folder);
        uploadFileEcmInput.setFolderName(folderName);
        uploadFileEcmInput.setCifNo(internalRequest.getUserPrincipal().getCifNo());
        uploadFileEcmInput.setTitle(fileName);
        logger.info("Upload File ECM Begin For CifNo {} UserName {} tranSn {}", internalRequest.getUserPrincipal().getCifNo(), internalRequest.getUserPrincipal().getUsername(), request.getNewTranSn());
        EcmResponse response = ecmService.uploadFile(uploadFileEcmInput);
        if (response != null && response.getUploadFileResponse() != null
                && response.getUploadFileResponse().getRespMessage() != null
                && "0".equals(response.getUploadFileResponse().getRespMessage().getRespCode())) {
            String[] object = (String[]) response.getUploadFileResponse().getRespDomain().get("data");
            if (object != null) {
                fileAttachment = new BbGuaranteeFileAttachment();
                fileAttachment.setFileName(fileName);
                fileAttachment.setDocumentId(request.getDocumentId());
                fileAttachment.setPathFile(object[0].replace("{", "").replace("}", ""));
                fileAttachment.setAttachmentType(AttachmentTypeEnum.ECM.value());
                fileAttachment.setCorpId(internalRequest.getUserPrincipal().getCorpId());
                fileAttachment.setStatus(AppConstant.STATUS_RECORD.NEWR);
                fileAttachment.setCreateBy(internalRequest.getUserPrincipal().getUserId());
                fileAttachment.setTransRefer(request.getNewTranSn());
                fileAttachment.setChoose("N");
                fileAttachment.setDebt(request.getDebt());
                fileAttachment.setDebtDate(request.getDebtDate());
                fileAttachment.setCreateTime(new Date());
                fileAttachmentRepository.save(fileAttachment);
                logger.info("Upload File ECM Success For CifNo {} UserName {} tranSn {}", internalRequest.getUserPrincipal().getCifNo(),
                        internalRequest.getUserPrincipal().getUsername(), request.getNewTranSn());
            } else {
                logger.info("Failed To Upload File ECM For CifNo {} UserName {} tranSn {}",
                        internalRequest.getUserPrincipal().getCifNo(), internalRequest.getUserPrincipal().getUsername(), request.getNewTranSn());
                throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.FAIL");
            }
        } else {
            throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.FAIL");
        }*/
        return fileAttachment;
    }

}
