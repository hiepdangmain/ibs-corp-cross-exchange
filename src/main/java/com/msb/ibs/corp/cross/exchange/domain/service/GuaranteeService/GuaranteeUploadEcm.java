package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.integration.ecm.EcmResponse;
import com.msb.ibs.common.utils.FileTools;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.enums.AttachmentTypeEnum;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.integration.input.InputUploadToEcm;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import com.msb.ibs.corp.main.service.domain.contants.MainConstants;
import com.msb.ibs.corp.main.service.domain.file.FileSSHClient;
import com.msb.ibs.corp.main.service.domain.file.FileUploadInfo;
import com.msb.ibs.corp.main.service.domain.input.UploadFileEcmInput;
import com.msb.ibs.corp.main.service.domain.service.EcmService;
import com.msb.ibs.corp.main.service.domain.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

@Service
public class GuaranteeUploadEcm {

    private static final Logger logger = LoggerFactory.getLogger(GuaranteeUploadEcm.class);

    @Autowired
    private EcmService ecmService;

    @Autowired
    private BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository;

    @Autowired
    private BbGuaranteeHistoryDetailRepository bbGuaranteeHistoryDetailRepository;

    @Autowired
    private BbGuaranteeFileAttachmentRepository fileAttachmentRepository;

    @Autowired
    private FileSSHClient fileSSHClient;

    @Autowired
    private GuaranteeCommonService guaranteeCommonService;

    public boolean execute(InputUploadToEcm input, UserPrincipal userPrincipal) throws LogicException, IOException {
        BbGuaranteeHistory bbGuaranteeHistory = null;
        if (StringUtil.isEmpty(input.getFolderName()) || StringUtil.isEmpty(input.getFileName())
                || StringUtil.isEmpty(input.getData()))
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        try {
            bbGuaranteeHistory = bbGuaranteeHistoryRepository.selectBytranSn(input.getFolderName());
            if (bbGuaranteeHistory == null) {
                throw new BadRequestException("ERROR.GUARANTEE.CONVERT.REPORT.WRONG.TRAN.SN");
            } else {
                if (userPrincipal.getCorpId().compareTo(bbGuaranteeHistory.getCorpId()) != 0)
                    throw new BadRequestException("ERROR.GUARANTEE.CONVERT.REPORT.WRONG.CORP.ID");
                if (!"CA".equals(userPrincipal.getSecurityType()))
                    throw new LogicException("ERROR.GUARANTEE.APPROVE.CA");
            }

            /*byte[] content = Base64.getDecoder().decode(input.getData().getBytes());

            int year = Calendar.getInstance().get(Calendar.YEAR);
            String folder = MainConstants.UPLOAD_DIRECTORY_PRODUCTION.concat(String.valueOf(year));
            String titleFile = guaranteeCommonService.formatFilename(input.getFileName());
            String fileName = guaranteeCommonService.formatFilename(input.getFileName());
            String folderName = bbGuaranteeHistory.getTransnRefer() != null ? bbGuaranteeHistory.getTransnRefer() : bbGuaranteeHistory.getTranSn();

            UploadFileEcmInput uploadFileEcmInput = new UploadFileEcmInput();
            uploadFileEcmInput.setContent(content);
            uploadFileEcmInput.setFileName(fileName);
            uploadFileEcmInput.setRootFolder(folder);
            uploadFileEcmInput.setFolderName(folderName);
            uploadFileEcmInput.setCifNo(userPrincipal.getCifNo());
            uploadFileEcmInput.setTitle(titleFile);
            logger.info("Upload File ECM Begin For CifNo {} UserName {} tranSn {}", userPrincipal.getCifNo(), userPrincipal.getUsername(), input.getFolderName());
            EcmResponse response = ecmService.uploadFile(uploadFileEcmInput);
            if (response != null && response.getUploadFileResponse() != null
                    && response.getUploadFileResponse().getRespMessage() != null
                    && "0".equals(response.getUploadFileResponse().getRespMessage().getRespCode())) {
                String[] object = (String[]) response.getUploadFileResponse().getRespDomain().get("data");
                if (object != null) {
                    BbGuaranteeFileAttachment fileAttachment = new BbGuaranteeFileAttachment();
                    fileAttachment.setFileName(fileName);
                    fileAttachment.setDocumentId(2); // 2 la don de nghi bao lanh
                    fileAttachment.setPathFile(object[0].replace("{", "").replace("}", ""));
                    fileAttachment.setAttachmentType(AttachmentTypeEnum.ECM.value());
                    fileAttachment.setCorpId(userPrincipal.getCorpId());
                    fileAttachment.setStatus(AppConstant.STATUS_RECORD.DPEN);
                    fileAttachment.setCreateBy(userPrincipal.getUserId());
                    fileAttachment.setTransRefer(bbGuaranteeHistory.getTranSn());
                    fileAttachment.setCreateTime(new Date());
//                    fileAttachment.setChoose("Y");
                    fileAttachmentRepository.save(fileAttachment);
                    logger.info("Upload File ECM Success For CifNo {} UserName {} tranSn {}", userPrincipal.getCifNo(), userPrincipal.getUsername(), input.getFolderName());
                } else { // insert vào DB local
                    //upload to SSH server
                    uploadLocal(input, fileName, userPrincipal, bbGuaranteeHistory);
                }
            } else {
                uploadLocal(input, fileName, userPrincipal, bbGuaranteeHistory);
                logger.info("Failed To Upload File ECM For CifNo {} UserName {} tranSn {}", userPrincipal.getCifNo(), userPrincipal.getUsername(), input.getFolderName());
                throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.FAIL");
            }*/

            uploadLocal(input, userPrincipal, bbGuaranteeHistory);
        } catch (Exception e) {
            logger.error("Failed Upload File ", e);
            throw new LogicException("ERROR.GUARANTEE.FILE.UPLOAD.FAIL");
        }
        return true;
    }

    private void uploadLocal(InputUploadToEcm input, UserPrincipal userPrincipal, BbGuaranteeHistory bbGuaranteeHistory) throws Exception {
        try {
            String filePath = guaranteeCommonService.genFilePath();
            String fileFullPath = filePath + FileUtils.formatFilename(input.getFileName());

            File file = new File(fileFullPath);
            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, Base64.getDecoder().decode(input.getData()));

            BbGuaranteeFileAttachment fileAttachment = fileAttachmentRepository.findByTranSnAndDocumentId(bbGuaranteeHistory.getTranSn(), 2);
            if (fileAttachment != null) {
                fileAttachment.setPathFile(fileFullPath);
                fileAttachment.setCorpId(userPrincipal.getCorpId());
                fileAttachment.setStatus(AppConstant.STATUS_RECORD.DPEN);
                fileAttachment.setCreateBy(userPrincipal.getUserId());
                fileAttachment.setNumUpload(0);
                fileAttachment.setNextTime(new Date());
                fileAttachmentRepository.save(fileAttachment);
            } else {
                fileAttachment = new BbGuaranteeFileAttachment();
                fileAttachment.setFileName(guaranteeCommonService.formatFilename(input.getFileName()));
                fileAttachment.setDocumentId(2);
                fileAttachment.setPathFile(fileFullPath);
                fileAttachment.setAttachmentType(AttachmentTypeEnum.SERVER.value());
                fileAttachment.setCorpId(userPrincipal.getCorpId());
                fileAttachment.setStatus(AppConstant.STATUS_RECORD.DPEN);
                fileAttachment.setCreateBy(userPrincipal.getUserId());
                fileAttachment.setCreateTime(new Date());
                fileAttachment.setTransRefer(bbGuaranteeHistory.getTranSn());
                fileAttachment.setNumUpload(0);
                fileAttachment.setNextTime(new Date());
                fileAttachmentRepository.save(fileAttachment);
            }
        } catch (Exception e) {
            logger.error("uploadLocal Failed ", e);
            throw e;
        }
    }

}
