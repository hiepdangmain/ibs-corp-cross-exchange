package com.msb.ibs.corp.cross.exchange.domain.repository.impl;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocumentConfig;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BbGuaranteeHistoryDetailJpaRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BbGuaranteeHistoryJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class BbGuaranteeHistoryDetailRepositoryImpl implements BbGuaranteeHistoryDetailRepository {

    @Autowired
    private BbGuaranteeHistoryDetailJpaRepository historyDetailJpaRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<BbGuaranteeHistoryDetail> selectByTranSn(String tranSn) {
        List<BbGuaranteeHistoryDetail> list = null;
        /*StringBuilder sql = new StringBuilder();
        sql.append(" select b from BbGuaranteeHistoryDetail where b.tranRefer = :tranSn order by b.seqNo asc");
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("tranSn", tranSn);*/

        Query query = entityManager.createQuery("select b from BbGuaranteeHistoryDetail b where b.tranRefer = :tranSn order by b.seqNo asc", BbGuaranteeHistoryDetail.class);
        query.setParameter("tranSn", tranSn);
        return query.getResultList();
    }

    @Override
    public List<BbGuaranteeHistoryDetail> insert(List<BbGuaranteeHistoryDetail> detail) {
        return historyDetailJpaRepository.saveAll(detail);
    }

    @Override
    public void deleteByTranSn(List<BbGuaranteeHistoryDetail> detailList) {
        historyDetailJpaRepository.deleteAllInBatch(detailList);
    }

    @Override
    public int deleteByTranRefer(String tranRefer) {
        Query query = entityManager.createQuery("delete  from BbGuaranteeHistoryDetail  where tranRefer = :tranSn");
        query.setParameter("tranSn", tranRefer);
        return query.executeUpdate();
    }

    @Override
    public BbGuaranteeHistoryDetail selectByTranSnAndParam(String tranSn, String paramName) {
        Query query = entityManager.createQuery("select b from BbGuaranteeHistoryDetail b where b.tranRefer = :tranSn and b.param = :paramName", BbGuaranteeHistoryDetail.class);
        query.setParameter("tranSn", tranSn);
        query.setParameter("paramName", paramName);
        List<?> results = query.getResultList();
        if (!CollectionUtils.isEmpty(results)) {
            return (BbGuaranteeHistoryDetail) results.get(0);
        }
        return null;
    }
}
