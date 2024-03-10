package com.msb.ibs.corp.cross.exchange.domain.repository.impl;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocument;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocumentConfig;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeDocumentRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BbGuaranteeDocumentJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class BbGuaranteeDocumentRepositoryImpl implements BbGuaranteeDocumentRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<BbGuaranteeDocument> findBbGuaranteeDocumentByType(String type) {
        Query query = entityManager.createQuery("select b from BbGuaranteeDocument b  where b.type in (:type) order by id asc", BbGuaranteeDocument.class);
        query.setParameter("type", type);
        return query.getResultList();
    }
}
