package com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BbGuaranteeFileAttachmentJpaRepository extends JpaRepository<BbGuaranteeFileAttachment, Integer>, JpaSpecificationExecutor<BbGuaranteeFileAttachment> {

    List<BbGuaranteeFileAttachment> findBbGuaranteeFileAttachmentsBytransRefer(String transRefer);

}

