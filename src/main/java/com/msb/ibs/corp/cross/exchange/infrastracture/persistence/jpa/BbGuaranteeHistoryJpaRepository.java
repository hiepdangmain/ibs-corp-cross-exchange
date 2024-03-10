package com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface BbGuaranteeHistoryJpaRepository extends JpaRepository<BbGuaranteeHistory, String>, JpaSpecificationExecutor<BbGuaranteeHistory> {
}
