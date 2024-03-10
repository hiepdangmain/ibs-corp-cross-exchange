package com.msb.ibs.corp.cross.exchange.domain.repository;

import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeListRequest;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BbGuaranteeLogRepository {
    BbGuaranteeLog save(BbGuaranteeLog bbGuaranteeLog);

    Page<BbGuaranteeLog> findByConditions(GuaranteeListRequest request, Pageable pageable);
}
