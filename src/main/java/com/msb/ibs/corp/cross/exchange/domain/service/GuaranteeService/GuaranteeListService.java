package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.base.QueryPrinciple;
import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.utils.ResponseUtils;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeListRequest;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeListResponse;
import com.msb.ibs.corp.cross.exchange.domain.dto.Metadata;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Service
public class GuaranteeListService implements QueryPrinciple<GuaranteeListRequest, ResponseEntity<?>, UserPrincipal> {

    @Autowired
    private BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository;

    @Override
    public void validate(GuaranteeListRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        if (Objects.isNull(request.getMetadata())) {
            throw new BadRequestException("ERROR.OBJECT.PARAM.INVALID");
        }
        if (Objects.isNull(request.getMetadata().getPageNo()) || Objects.isNull(request.getMetadata().getPageSize())) {
            throw new BadRequestException("ERROR.OBJECT.PARAM.INVALID");
        }
        if(StringUtil.isNotEmpty(request.getDateBy())) {
            if (!("1".equalsIgnoreCase(request.getDateBy()) || "2".equalsIgnoreCase(request.getDateBy())
                    || "3".equalsIgnoreCase(request.getDateBy()))) {
                throw new BadRequestException("ERROR.GUARANTEE.LIST.REQUEST.INVALID");
            }
        }
    }

    @Override
    public ResponseEntity<?> process(GuaranteeListRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        Pageable pageable = PageRequest.of(request.getMetadata().getPageNo() - 1, request.getMetadata().getPageSize(),
                Sort.by("createTime").descending());
        if (request.getCorpId() == null) {
            request.setCorpId(principle.getCorpId());
        }
        Page<BbGuaranteeHistory> list = bbGuaranteeHistoryRepository.findByConditions(request, pageable);
        GuaranteeListResponse response = new GuaranteeListResponse();
        response.setGuaranteeHistoryList(list.toList());
        Metadata metadata = new Metadata();
        metadata.setPageNo(request.getMetadata().getPageNo());
        metadata.setPageSize(request.getMetadata().getPageSize());
        metadata.setTotal(list.getTotalElements());
        response.setMetadata(metadata);

        return ResponseUtils.success(response);
    }
}
