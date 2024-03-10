package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.corp.cross.exchange.application.request.*;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeDetailResponse;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuaranteeInit;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class GuaranteeService {
    private final GuaranteeCommonService guaranteeCommonService;
    private final GuaranteeListService guaranteeListService;
    private final GuaranteeDetailService guaranteeDetailService;
    private final GuaranteeUpdateStatusService guaranteeUpdateStatusService;
    private final GuaranteeUpdateStatusV2Service guaranteeUpdateStatusV2Service;
    private final GuaranteeCountStatusService guaranteeCountStatusService;
    private final GuaranteeReportHistoryService reportHistoryService;
    private final GuaranteeUploadEcmIBSAdminService uploadEcmIBSAdmin;

    public GuaranteeService(GuaranteeCommonService guaranteeCommonService,
                            GuaranteeListService guaranteeListService,
                            GuaranteeDetailService guaranteeDetailService,
                            GuaranteeUpdateStatusService guaranteeUpdateStatusService,
                            GuaranteeUpdateStatusV2Service guaranteeUpdateStatusV2Service,
                            GuaranteeCountStatusService guaranteeCountStatusService,
                            GuaranteeCountStatusService guaranteeCountStatusService1,
                            GuaranteeReportHistoryService reportHistoryService,
                            GuaranteeUploadEcmIBSAdminService uploadEcmIBSAdmin) {
        this.guaranteeCommonService = guaranteeCommonService;
        this.guaranteeDetailService = guaranteeDetailService;
        this.guaranteeListService = guaranteeListService;
        this.guaranteeUpdateStatusService = guaranteeUpdateStatusService;
        this.guaranteeUpdateStatusV2Service = guaranteeUpdateStatusV2Service;
        this.guaranteeCountStatusService = guaranteeCountStatusService1;
        this.reportHistoryService = reportHistoryService;
        this.uploadEcmIBSAdmin = uploadEcmIBSAdmin;
    }

    public OutputGuaranteeInit initGuarantee(UserPrincipal principal) throws LogicException {
        return guaranteeCommonService.checkCorpInfo(principal.getCifNo(), principal.getCorpId());
    }

    @Transactional(readOnly = true)
    public Object list(GuaranteeListRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        return guaranteeListService.execute(request, principle, httpRequest);
    }

    @Transactional(readOnly = true)
    public GuaranteeDetailResponse detail(GuaranteeDetailRequest request) {
        return guaranteeDetailService.execute(request);
    }

    @Transactional(rollbackFor = {BadRequestException.class, Exception.class})
    public Object updateStatus(GuaranteeUpdateStatusRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        return guaranteeUpdateStatusService.execute(request, principle, httpRequest);
    }

    @Transactional(rollbackFor = {BadRequestException.class, Exception.class})
    public Object updateStatusV2(GuaranteeUpdateStatusRequest request) {
        return guaranteeUpdateStatusV2Service.execute(request);
    }

    @Transactional(readOnly = true)
    public Object listStatus(UserPrincipal principle, HttpServletRequest httpRequest) {
        return guaranteeCountStatusService.execute(null, principle, httpRequest);
    }

    @Transactional(readOnly = true)
    public Object listLogHistory(GuaranteeListRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        return reportHistoryService.execute(request, principle, httpRequest);
    }

    @Transactional(rollbackFor = {BadRequestException.class, Exception.class, LogicException.class})
    public Object uploadECMAdmin(GuaranteeUploadECMRequest request, UserPrincipal principle, HttpServletRequest httpRequest) throws LogicException, IOException {
        return null;
    }

    @Transactional(readOnly = true)
    public Object getFee(GuaranteeFeeRequest request) throws LogicException {
        return guaranteeCommonService.getFee(request);
    }
}
