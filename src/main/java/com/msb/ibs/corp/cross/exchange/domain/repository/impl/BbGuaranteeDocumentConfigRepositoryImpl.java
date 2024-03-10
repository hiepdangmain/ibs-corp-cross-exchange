package com.msb.ibs.corp.cross.exchange.domain.repository.impl;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocumentConfig;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeDocumentConfigRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class BbGuaranteeDocumentConfigRepositoryImpl implements BbGuaranteeDocumentConfigRepository {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<BbGuaranteeDocumentConfig> findListDocumentConfig(List<String> type) {
        Query query = entityManager.createQuery("select b from BbGuaranteeDocumentConfig b  where b.type in (:type) order by seqNo asc", BbGuaranteeDocumentConfig.class);
        query.setParameter("type", type);
        return query.getResultList();
    }
}
