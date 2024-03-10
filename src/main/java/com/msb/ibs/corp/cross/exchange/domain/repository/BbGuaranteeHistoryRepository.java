package com.msb.ibs.corp.cross.exchange.domain.repository;

import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeListRequest;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuaranteeCount;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeCondition;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocument;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Map;

public interface BbGuaranteeHistoryRepository {

    List<BbGuaranteeHistory> findByCondiiton(GuaranteeCondition condition,  Integer corpId);

    BbGuaranteeHistory selectBytranSn (String tranSn);

    BbGuaranteeHistory save(BbGuaranteeHistory bbGuaranteeHistory);

    BbGuaranteeHistory updateByPrimaryKeySelective(BbGuaranteeHistory bbGuaranteeHistory);

    List<BbGuaranteeHistory> saveAllList(List<BbGuaranteeHistory> historyList);

    Page<BbGuaranteeHistory> findByConditions(GuaranteeListRequest request, Pageable pageable);

    void deleteByByTranSn(String tranSn);

    int countStatus(String status);

    List<OutputGuaranteeCount> countAllStatus();

    void updateExpiredPendingCheckerApproved (List<String> lstTranSn, String toStatus);

    List<BbGuaranteeHistory> getGuaranteeSync(Integer maxNumberSync);

    List<BbGuaranteeHistory> getGuaranteeCancel();

    BbGuaranteeHistory getTranSnActive(String tranSn);

    BbGuaranteeHistory getTranUpdateNotActiveLatest(String tranSn);
}
