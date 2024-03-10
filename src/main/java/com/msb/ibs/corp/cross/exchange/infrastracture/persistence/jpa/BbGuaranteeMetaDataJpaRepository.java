package com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BbGuaranteeMetaDataJpaRepository extends JpaRepository<BbGuaranteeMetaData, String> {
}
