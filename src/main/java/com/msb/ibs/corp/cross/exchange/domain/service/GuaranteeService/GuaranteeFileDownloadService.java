package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.integration.ecm.EcmResponse;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.enums.AttachmentTypeEnum;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeDownloadRequest;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuaranteeDownload;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import com.msb.ibs.corp.main.service.domain.file.FileSSHClient;
import com.msb.ibs.corp.main.service.domain.service.EcmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class GuaranteeFileDownloadService {
    private final FileSSHClient fileSSHClient;
    private final BbGuaranteeFileAttachmentRepository fileAttachmentRepository;
    private final EcmService ecmService;
    private final GuaranteeCommonService guaranteeCommonService;
    private static final Logger logger = LoggerFactory.getLogger(GuaranteeFileDownloadService.class);


    public GuaranteeFileDownloadService(FileSSHClient fileSSHClient,
                                        BbGuaranteeFileAttachmentRepository fileAttachmentRepository,
                                        EcmService ecmService,
                                        GuaranteeCommonService guaranteeCommonService) {
        this.fileSSHClient = fileSSHClient;
        this.fileAttachmentRepository = fileAttachmentRepository;
        this.ecmService = ecmService;
        this.guaranteeCommonService = guaranteeCommonService;
    }

    public OutputGuaranteeDownload execute(GuaranteeDownloadRequest request, InternalRequest internalRequest) throws LogicException, IOException {
        BbGuaranteeFileAttachment fileAttachment = fileAttachmentRepository.findById(request.getAttachmentId());
        if (fileAttachment == null) {
            throw new LogicException("ERROR.GUARANTEE.DOWNLOAD.FILE");
        }
        OutputGuaranteeDownload response = new OutputGuaranteeDownload();
        response.setFileName(fileAttachment.getFileName());
        response.setFileContent(download(fileAttachment));
        return response;
    }
    private byte[] download(BbGuaranteeFileAttachment fileAttachment) throws LogicException, IOException {
        logger.info("Begin Download File For AttachmentId {}, FileName {}", fileAttachment.getId(), fileAttachment.getFileName());
        logger.info("Download File From SYSTEM {}", fileAttachment.getAttachmentType());
        if (AttachmentTypeEnum.SERVER.value().equals(fileAttachment.getAttachmentType())) {
            return Files.readAllBytes(Paths.get(fileAttachment.getPathFile()));
        } else if (AttachmentTypeEnum.ECM.value().equals(fileAttachment.getAttachmentType())) {
            EcmResponse response = ecmService.downloadFile(fileAttachment.getPathFile());
            if (response != null && response.getDownloadFileResponse() != null
                    && response.getDownloadFileResponse().getRespMessage() != null) {
                if ("0".equals(response.getDownloadFileResponse().getRespMessage().getRespCode())) {
                    return (byte[]) response.getDownloadFileResponse().getRespDomain().get("fileByte");
                } else {
                    logger.info("Failed To Download File From ECM {}", response.getDownloadFileResponse().getRespMessage().getRespDesc());
                }
            }
        } else if (AttachmentTypeEnum.MIN_IO.value().equals(fileAttachment.getAttachmentType())
                        && StringUtil.isNotEmpty(fileAttachment.getPathFileLocal())) {
            return Files.readAllBytes(Paths.get(fileAttachment.getPathFileLocal()));
        }
        return new byte[0];
    }

}
