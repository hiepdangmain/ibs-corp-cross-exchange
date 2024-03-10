package com.msb.ibs.corp.cross.exchange.domain.repository;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BbGuaranteeDocumentRepository {
    List<BbGuaranteeDocument> findBbGuaranteeDocumentByType(String type);
}
