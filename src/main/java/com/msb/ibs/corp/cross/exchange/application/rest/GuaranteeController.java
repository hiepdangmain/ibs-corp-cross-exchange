package com.msb.ibs.corp.cross.exchange.application.rest;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.response.JsonResponseBase;
import com.msb.ibs.common.utils.ResponseUtils;
import com.msb.ibs.core.base.BaseController;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.request.*;
import com.msb.ibs.corp.cross.exchange.domain.integration.input.InputUploadToEcm;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService.*;
import com.msb.ibs.corp.main.service.domain.input.ApproveOrRejectTransactionInput;
import com.msb.ibs.corp.main.service.domain.input.ApproveTransactionValidateInput;
import com.msb.ibs.corp.main.service.domain.input.TransactionConfirmInput;
import com.msb.ibs.corp.main.service.domain.input.WorkflowProcessTaskExpiredInput;
import com.msb.ibs.corp.main.service.domain.service.TransactionConfirmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Tag(name = "GuaranteeController Controller")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1")
public class GuaranteeController extends BaseController {

    private GuaranteeService guaranteeService;
    private GuaranteeCreateStep1Service validateStep1Service;
    private GuaranteeCreateStep2Service validateStep2Service;
    private GuaranteeCreateService createService;
    private GuaranteeFileUploadService uploadService;
    private GuaranteeFileDownloadService downloadService;
    private GuaranteeCreateValidateService guaranteeCreateValidateService;
    private TransactionConfirmService transactionConfirmService;
    private GuaranteeSaveDraftService saveDraftService;
    private GuaranteeApproveValidateService guaranteeApproveValidateService;
    private GuaranteeConvertPdf guaranteeConvertPdf;
    private GuaranteeUploadEcm guaranteeUploadEcm;
    private GuaranteeCountStatusService guaranteeCountStatusService;
    private GuaranteeCheckExpiredService checkExpiredService;
//    private GuaranteeUploadEcmIBSAdmin uploadEcmIBSAdmin;
    private GuaranteeSyncService guaranteeSyncService;

    public GuaranteeController(GuaranteeService guaranteeService, GuaranteeCreateStep1Service validateStep1Service,
                               GuaranteeCreateStep2Service validateStep2Service, GuaranteeCreateService createService,
                               GuaranteeFileUploadService uploadService, GuaranteeFileDownloadService downloadService,
                               GuaranteeCreateValidateService guaranteeCreateValidateService,
                               GuaranteeSaveDraftService saveDraftService, GuaranteeApproveValidateService guaranteeApproveValidateService,
                               GuaranteeConvertPdf guaranteeConvertPdf, GuaranteeUploadEcm guaranteeUploadEcm,
                               GuaranteeCountStatusService guaranteeCountStatusService, GuaranteeCheckExpiredService checkExpiredService/*,
                               GuaranteeUploadEcmIBSAdmin uploadEcmIBSAdmin*/,
                               GuaranteeSyncService guaranteeSyncService) {
        this.guaranteeService = guaranteeService;
        this.validateStep1Service = validateStep1Service;
        this.validateStep2Service = validateStep2Service;
        this.createService = createService;
        this.uploadService = uploadService;
        this.downloadService = downloadService;
        this.guaranteeCreateValidateService = guaranteeCreateValidateService;
        this.saveDraftService = saveDraftService;
        this.guaranteeApproveValidateService = guaranteeApproveValidateService;
        this.guaranteeConvertPdf = guaranteeConvertPdf;
        this.guaranteeUploadEcm = guaranteeUploadEcm;
        this.guaranteeCountStatusService = guaranteeCountStatusService;
        this.checkExpiredService = checkExpiredService;
//        this.uploadEcmIBSAdmin = uploadEcmIBSAdmin;
        this.guaranteeSyncService = guaranteeSyncService;
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-init")
    @Operation(summary = "Khoi tao thong tin doanh nghiep")
    public Object guaranteeInit(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal) throws LogicException {
        return ResponseUtils.success(guaranteeService.initGuarantee(principal));
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-step1")
    @Operation(summary = "Validate thong tin phát hành")
    public Object guaranteeStep1(@RequestBody GuaranteeRequest guaranteeRequest, HttpServletRequest request,
                                 HttpServletResponse response ) throws LogicException {
        InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(validateStep1Service.validateStep1(internalRequest.getUserPrincipal(), guaranteeRequest));
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-step2")
    @Operation(summary = "Validate nội dung đề nghị")
    public Object guaranteeStep2(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal, @RequestBody GuaranteeRequest guaranteeRequest ) throws LogicException {
        return ResponseUtils.success(validateStep2Service.validateStep2(principal, guaranteeRequest));
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-create")
    @Operation(summary = "Validate cam kết")
    public Object guaranteeStep3(@RequestBody GuaranteeRequest guaranteeRequest, HttpServletRequest request,
                                 HttpServletResponse response ) throws LogicException {
        InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeCreateValidateService.createGuarantee(guaranteeRequest, internalRequest));
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-confirm-maker")
    @Operation(summary = "maker confirm")
    public Object confirmMaker(@RequestBody TransactionConfirmInput confirmInput, HttpServletRequest request,
                               HttpServletResponse response ) throws LogicException, IOException {
        InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeCreateValidateService.confirmMaker(confirmInput, internalRequest));
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-upload-file")
    @Operation(summary = "Upload file")
    public Object guaranteeUploadFile(@RequestBody GuaranteeUploadRequest guaranteeUploadRequest, HttpServletRequest request,
                                      HttpServletResponse response ) throws LogicException, IOException {
        InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(uploadService.execute(guaranteeUploadRequest, internalRequest));
    }

    @PostMapping(path = "/guarantee-download-file")
    @Operation(summary = "download file")
    public Object guaranteeDownloadFile(@RequestBody GuaranteeDownloadRequest guaranteeDownloadRequest, HttpServletRequest request,
                                        HttpServletResponse response ) throws LogicException, IOException {
        InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(downloadService.execute(guaranteeDownloadRequest, internalRequest));
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-save-draft")
    @Operation(summary = "luu nhap")
    public Object saveDraft(@RequestBody GuaranteeRequest guaranteeRequest, HttpServletRequest request,
                            HttpServletResponse response ) throws LogicException, IOException {
        InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeCreateValidateService.saveDraft(guaranteeRequest, internalRequest));
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-delete")
    @Operation(summary = "Xoa lenh")
    public Object delete(@RequestBody GuaranteeDeleteRequest guaranteeRequest, HttpServletRequest request,
                         HttpServletResponse response ) throws LogicException, IOException {
        InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeCreateValidateService.delete(guaranteeRequest, internalRequest));
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-copy")
    @Operation(summary = "Tao ban sao")
    public Object copy(@RequestBody GuaranteeDeleteRequest guaranteeRequest, HttpServletRequest request,
                       HttpServletResponse response ) throws LogicException, IOException {
        InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeCreateValidateService.copy(guaranteeRequest, internalRequest));
    }

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-list")
    @Operation(summary = "Danh sach ho so bao lanh")
    public Object guaranteeList(@RequestBody GuaranteeListRequest input,
                                    @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
                                    HttpServletRequest request) {
        return guaranteeService.list(input, principal, request);
    }

    @PostMapping(path = "/guarantee-detail")
    @Operation(summary = "Thong tin chi tiet bao lanh")
    public Object guaranteeDetail(@RequestBody GuaranteeDetailRequest input) {
        return ResponseUtils.success(guaranteeService.detail(input));
    }

    @PostMapping(path = "/guarantee-update-status")
    @Operation(summary = "Tiep nhan, tu choi, yc cap nhat vs hoan thanh xu ly ho so bao lanh")
    public Object guaranteeUpdateStatus(@RequestBody GuaranteeUpdateStatusRequest input,
                                            @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
                                            HttpServletRequest request) {
        return guaranteeService.updateStatus(input, principal, request);
    }

    /*@PostMapping(path = "/guarantee-bpm-update")
    @Operation(summary = "Tiep nhan, tu choi, yc cap nhat vs hoan thanh xu ly ho so bao lanh (Tich hop BPM)")
    public Object guaranteeUpdateStatusV2(@RequestBody GuaranteeUpdateStatusRequest input) {
        return guaranteeService.updateStatusV2(input);
    }*/

    @PreAuthorize("hasAuthority('GUARANTEE_MAKER')")
    @PostMapping(path = "/guarantee-create-pdf")
    @Operation(summary = "sinh file pdf nhap")
    public Object convertPdf(@RequestBody GuaranteeRequest guaranteeRequest, HttpServletRequest request,
                                 HttpServletResponse response ) throws LogicException {
        InternalRequest internalRequest = new InternalRequest(guaranteeRequest, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeCreateValidateService.convertFilePdf(guaranteeRequest, internalRequest));
    }

    @PostMapping(path = "/guarantee-approve")
    @Operation(summary = "Nhap OTP confirm ")
    public Object approveChecker(@RequestBody ApproveOrRejectTransactionInput input, HttpServletRequest request,
                                 HttpServletResponse response ) throws LogicException {
        InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeCreateValidateService.approve(internalRequest));
    }

    @PostMapping(path = "/guarantee-approve-create-validate")
    @Operation(summary = "Validate approve")
    public Object approveValidate(@RequestBody ApproveTransactionValidateInput input, HttpServletRequest request,
                                  HttpServletResponse response ) throws LogicException {
        InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeApproveValidateService.validateApprove( internalRequest.getUserPrincipal(), input));
    }

    @PostMapping(path = "/guarantee-create-pdf-final")
    @Operation(summary = "file de nghi pdf ")
    public Object approvePdf(@RequestBody GuaranteeConverPdfRequest input, HttpServletRequest request,
                                  HttpServletResponse response ) throws LogicException {
        InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeConvertPdf.execute( internalRequest.getUserPrincipal(), input));
    }

    @PostMapping(path = "/guarantee-upload-ecm")
    @Operation(summary = "upload file len Ecm ")
    public Object uploadEcm(@RequestBody InputUploadToEcm input, HttpServletRequest request,
                            HttpServletResponse response ) throws LogicException, IOException {
        InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeUploadEcm.execute(input, internalRequest.getUserPrincipal()));
    }

    @PostMapping(path = "/guarantee-list-status")
    @Operation(summary = "Thống kê trạng thái giao dịch cho IBSAdmin")
    public Object guaranteeListStatus(@Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
                                HttpServletRequest request) {
        return guaranteeService.listStatus(principal, request);
    }

    @PostMapping(value = "/guarantee-check-expired")
    @Operation(summary = "API Cập nhật trạng thái lệnh chờ duyệt hết hiệu lực")
    public ResponseEntity<JsonResponseBase<Object>> beneficiaryExpired(@RequestBody WorkflowProcessTaskExpiredInput input) {
        checkExpiredService.guaranteeTaskExpired(input);
        return ResponseUtils.success(true);
    }

    @PostMapping(path = "/guarantee-report-history")
    @Operation(summary = "Thống kê trạng thái giao dịch cho IBSAdmin")
    public Object guaranteeReportHistory(@RequestBody GuaranteeListRequest input,
                                         @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
                                         HttpServletRequest request) {
        return guaranteeService.listLogHistory(input, principal, request);
    }

    /*@PostMapping(path = "/guarantee-upload-ecm-ibsAdmin")
    @Operation(summary = "đẩy hồ sơ lên ECM từ IBSAdmin")
    public Object uploadECMAdmin(@RequestBody GuaranteeUploadECMRequest input,
                                         @Parameter(hidden = true) @AuthenticationPrincipal UserPrincipal principal,
                                         HttpServletRequest request) {
        return null;
    }*/

    @PostMapping(path = "/guarantee-check-last-checker")
    @Operation(summary = "check last checker cuoi cung")
    public Object checkLastChecker(@RequestBody GuaranteeDeleteRequest guaranteeRequest, HttpServletRequest request,
                       HttpServletResponse response ) throws LogicException, IOException {
        InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken(),
                request.getHeader(AppConstant.CHANNEL_HEADER));
        return ResponseUtils.success(guaranteeCreateValidateService.checkerLastChecker(internalRequest, guaranteeRequest));
    }

    @PostMapping(path = "/guarantee-retry-upload-ecm")
    @Operation(summary = "Day lai file len ECM")
    public Object retryUploadECM(@RequestBody GuaranteeUploadECMRequest input) throws LogicException {
        return ResponseUtils.success(guaranteeCreateValidateService.retryUploadECM(input.getFileId()));
    }

    @PostMapping(path = "/internal/guarantee-sync-bpm")
    @Operation(summary = "Day lai thong tin bao lanh sang BPM (internal)")
    public Object syncGuarantee() throws LogicException {
        return ResponseUtils.success(guaranteeSyncService.syncGuarantee());
    }

    @PostMapping(path = "/internal/guarantee-retry-upload-minio")
    @Operation(summary = "Day file len MinIO (internal)")
    public Object uploadMinIO(@RequestBody GuaranteeUploadMinIORequest input) {
        return ResponseUtils.success(guaranteeSyncService.uploadToMinIO(input));
    }

    @PostMapping(path = "/internal/guarantee-cancel")
    @Operation(summary = "Huy bao lanh bi tu choi/qua han cho duyet tren BPM (internal)")
    public Object cancelGuarantee() {
        return ResponseUtils.success(guaranteeSyncService.cancelGuarantee());
    }

    @PostMapping(path = "/guarantee-get-fee")
    @Operation(summary = "Lay phi du thu bao lanh")
    public Object getFee(@RequestBody GuaranteeFeeRequest input) throws LogicException {
        return ResponseUtils.success(guaranteeService.getFee(input));
    }

}

