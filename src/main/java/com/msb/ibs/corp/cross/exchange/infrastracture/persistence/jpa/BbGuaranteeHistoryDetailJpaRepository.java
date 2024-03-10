package com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BbGuaranteeHistoryDetailJpaRepository extends JpaRepository<BbGuaranteeHistoryDetail, Integer>, JpaSpecificationExecutor<BbGuaranteeHistoryDetail> {

    List<BbGuaranteeHistoryDetail> findBbGuaranteeHistoryDetailByTranRefer(String tranRefer);

}
