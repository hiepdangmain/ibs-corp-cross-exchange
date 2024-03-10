package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeDeleteRequest;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeDetailResponse;
import com.msb.ibs.corp.cross.exchange.domain.dto.DocumentGuaranteeDto;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeHistoryDetailDto;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuaranteeCopyService {

    @Autowired
    private BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository;

    @Autowired
    private BbGuaranteeHistoryDetailRepository bbGuaranteeHistoryDetailRepository;

    public GuaranteeDetailResponse execute (UserPrincipal principal, GuaranteeDeleteRequest request) throws LogicException {
        if (StringUtil.isEmpty(request.getTranSn())) {
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        }

        BbGuaranteeHistory guaranteeHistory = bbGuaranteeHistoryRepository.selectBytranSn(request.getTranSn());
        List<GuaranteeHistoryDetailDto> details = new ArrayList<>();
        List<DocumentGuaranteeDto> documents = new ArrayList<>();
        if (guaranteeHistory != null) {
            List<BbGuaranteeHistoryDetail> historyDetails = bbGuaranteeHistoryDetailRepository.selectByTranSn(request.getTranSn());
            for (BbGuaranteeHistoryDetail detail : historyDetails) {
                GuaranteeHistoryDetailDto detailDto = new GuaranteeHistoryDetailDto();
                BeanUtils.copyProperties(detail, detailDto);
                details.add(detailDto);
            }
        }
        return GuaranteeDetailResponse.builder()
                .guaranteeHistory(guaranteeHistory)
                .details(details)
                .documents(documents)
                .build();
    }

}
