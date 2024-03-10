package com.msb.ibs.corp.cross.exchange.infrastracture.persistence;

import com.msb.ibs.common.utils.CommonStringUtils;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeMetaData;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeMetaDataRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BbGuaranteeMetaDataJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class BbGuaranteeMetaDataRepositoryImpl implements BbGuaranteeMetaDataRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BbGuaranteeMetaDataJpaRepository bbGuaranteeMetaDataJpaRepository;



    @Override
    public BbGuaranteeMetaData findBbGuaranteeMetaDataByCorpIdAndType(String applicant, String beneficiaryName, String type, Integer corpId) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select b from BbGuaranteeMetaData b");
        sql.append(" where b.corpId =:corpId");
        sql.append(" and b.type =:type");
        if (!CommonStringUtils.isNullOrEmpty(applicant)) {
            sql.append(" and b.applicant =:applicant");
        }
        if (!CommonStringUtils.isNullOrEmpty(beneficiaryName)) {
            sql.append(" and b.beneficiary =:beneficiary");
        }
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("type", type);
        query.setParameter("corpId", corpId);
        if (!CommonStringUtils.isNullOrEmpty(applicant)) {
            query.setParameter("applicant", applicant);
        }
        if (!CommonStringUtils.isNullOrEmpty(beneficiaryName)) {
            query.setParameter("beneficiary", beneficiaryName);
        }
        List<BbGuaranteeMetaData> result = query.getResultList();
        if (!CollectionUtils.isEmpty(result)) {
            return result.get(0);
        }
        return null;
    }

    @Override
    public List<BbGuaranteeMetaData> getListBbGuaranteeMetaDataByCorpId(Integer corpId) {
        Query query = entityManager.createQuery("select b from BbGuaranteeMetaData b where b.corpId =:corpId order by b.updatedTime desc");
        query.setParameter("corpId", corpId);
        return query.getResultList();
    }

    @Override
    public BbGuaranteeMetaData save(BbGuaranteeMetaData bbGuaranteeMetaData) {
       return bbGuaranteeMetaDataJpaRepository.save(bbGuaranteeMetaData);
    }
}
