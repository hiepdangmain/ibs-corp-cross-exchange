package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeDeleteRequest;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeRequest;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeDetailResponse;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuanranteeLastChecker;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuaranteeDownload;
import com.msb.ibs.corp.main.service.domain.input.TransactionConfirmInput;
import com.msb.ibs.corp.main.service.domain.output.ApproveTransactionOutput;
import com.msb.ibs.corp.main.service.domain.output.TransactionConfirmOutput;
import com.msb.ibs.corp.main.service.domain.service.TransactionConfirmService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuaranteeCreateValidateService {

    private GuaranteeCreateService createService;
    private GuaranteeConfirmService confirmService;
    private TransactionConfirmService transactionConfirmService;
    private GuaranteeSaveDraftService guaranteeSaveDraftService;
    private GuaranteeDeleteService guaranteeDeleteService;
    private GuaranteePdfService pdfService;
    private GuaranteeCopyService copyService;
    private GuaranteeApproveService approveService;
    private GuaranteeCheckLastCheckerService guaranteeCheckLastCheckerService;
    private GuaranteeUploadEcmIBSAdminService uploadEcmIBSAdmin;

    public GuaranteeCreateValidateService(GuaranteeCreateService createService,
                                          GuaranteeConfirmService confirmService,
                                          TransactionConfirmService transactionConfirmService,
                                          GuaranteeSaveDraftService guaranteeSaveDraftService,
                                          GuaranteeDeleteService guaranteeDeleteService,
                                          GuaranteePdfService pdfService,
                                          GuaranteeCopyService copyService,
                                          GuaranteeApproveService approveService,
                                          GuaranteeCheckLastCheckerService guaranteeCheckLastCheckerService,
                                          GuaranteeUploadEcmIBSAdminService uploadEcmIBSAdmin) {
        this.createService = createService;
        this.confirmService = confirmService;
        this.transactionConfirmService = transactionConfirmService;
        this.guaranteeSaveDraftService = guaranteeSaveDraftService;
        this.guaranteeDeleteService = guaranteeDeleteService;
        this.pdfService = pdfService;
        this.copyService = copyService;
        this.approveService = approveService;
        this.guaranteeCheckLastCheckerService = guaranteeCheckLastCheckerService;
        this.uploadEcmIBSAdmin = uploadEcmIBSAdmin;
    }

    @Transactional(rollbackFor = LogicException.class)
    public Object createGuarantee(GuaranteeRequest input, InternalRequest internalRequest) throws LogicException {
        return createService.execute(input, internalRequest);
    }

    @Transactional(rollbackFor = LogicException.class)
    public TransactionConfirmOutput confirmMaker(TransactionConfirmInput input, InternalRequest internalRequest)
            throws LogicException {
        return transactionConfirmService.execute(input, internalRequest);
    }

    @Transactional(rollbackFor = LogicException.class)
    public GuaranteeDetailResponse saveDraft(GuaranteeRequest input, InternalRequest internalRequest)
            throws LogicException {
        return guaranteeSaveDraftService.execute(input, internalRequest.getUserPrincipal());
    }

    @Transactional(rollbackFor = LogicException.class)
    public boolean delete(GuaranteeDeleteRequest input, InternalRequest internalRequest)
            throws LogicException {
        return guaranteeDeleteService.execute(internalRequest, input);
    }

    @Transactional(rollbackFor = LogicException.class)
    public GuaranteeDetailResponse copy(GuaranteeDeleteRequest input, InternalRequest internalRequest)
            throws LogicException {
        return copyService.execute(internalRequest.getUserPrincipal(), input);
    }

    @Transactional(rollbackFor = LogicException.class)
    public OutputGuaranteeDownload convertFilePdf(GuaranteeRequest input, InternalRequest internalRequest)
            throws LogicException {
        return pdfService.execute(internalRequest.getUserPrincipal(), input);
    }

    @Transactional(rollbackFor = LogicException.class)
    public ApproveTransactionOutput approve(InternalRequest internalRequest)
            throws LogicException {
        return approveService.execute(internalRequest);
    }

    @Transactional(rollbackFor = LogicException.class)
    public OutputGuanranteeLastChecker checkerLastChecker(InternalRequest internalRequest, GuaranteeDeleteRequest input)
            throws LogicException {
        return guaranteeCheckLastCheckerService.execute(internalRequest, input);
    }

    @Transactional(rollbackFor = LogicException.class)
    public Boolean retryUploadECM(Integer fileId) throws LogicException {
        return uploadEcmIBSAdmin.uploadFileEcm(fileId);
    }
}
