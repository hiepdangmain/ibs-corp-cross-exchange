package com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BbGuaranteeDocumentJpaRepository extends JpaRepository<BbGuaranteeDocument, String>, JpaSpecificationExecutor<BbGuaranteeDocument> {

    List<BbGuaranteeDocument> findBbGuaranteeDocumentByType(String type);
}
