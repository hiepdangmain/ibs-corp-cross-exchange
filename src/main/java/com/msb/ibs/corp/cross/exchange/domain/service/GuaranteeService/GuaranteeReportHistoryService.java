package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.base.QueryPrinciple;
import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.utils.ResponseUtils;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeListRequest;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeLogListResponse;
import com.msb.ibs.corp.cross.exchange.domain.dto.Metadata;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeLog;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeReportHistoryService implements QueryPrinciple<GuaranteeListRequest, ResponseEntity<?>, UserPrincipal> {

    @Autowired
    private BbGuaranteeLogRepository guaranteeLogRepository;

    @Override
    public void validate(GuaranteeListRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        if (Objects.isNull(request.getMetadata())) {
            throw new BadRequestException("ERROR.OBJECT.PARAM.INVALID");
        }
        if (Objects.isNull(request.getMetadata().getPageNo()) || Objects.isNull(request.getMetadata().getPageSize())) {
            throw new BadRequestException("ERROR.OBJECT.PARAM.INVALID");
        }
    }

    @Override
    @Transactional(rollbackFor = {BadRequestException.class, Exception.class})
    public ResponseEntity<?> process(GuaranteeListRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        Pageable pageable = PageRequest.of(request.getMetadata().getPageNo() - 1, request.getMetadata().getPageSize(),
                Sort.by("customerSendTime").ascending());
        if (request.getCorpId() == null) {
            request.setCorpId(principle.getCorpId());
        }
        Page<BbGuaranteeLog> list = guaranteeLogRepository.findByConditions(request, pageable);
        GuaranteeLogListResponse response = new GuaranteeLogListResponse();
        response.setGuaranteeLogList(list.toList());
        Metadata metadata = new Metadata();
        metadata.setPageNo(request.getMetadata().getPageNo());
        metadata.setPageSize(request.getMetadata().getPageSize());
        metadata.setTotal(list.getTotalElements());
        response.setMetadata(metadata);

        return ResponseUtils.success(response);
    }

}
