package com.msb.ibs.corp.cross.exchange.domain.repository.impl;

import com.msb.ibs.common.utils.CommonStringUtils;
import com.msb.ibs.common.utils.DateUtil;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeListRequest;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuaranteeCount;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeCondition;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BbGuaranteeHistoryJpaRepository;
import com.msb.ibs.corp.main.service.domain.enums.TransactionStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import java.util.*;

@Component
public class BbGuaranteeHistoryRepositoryImpl implements BbGuaranteeHistoryRepository {

    @Autowired
    private BbGuaranteeHistoryJpaRepository jpaRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<BbGuaranteeHistory> findByCondiiton(GuaranteeCondition condition, Integer corpId) {
        List<BbGuaranteeHistory> list = null;
        StringBuilder sql = new StringBuilder();
        sql.append(" select b from BbGuaranteeHistory b where 1 = 1");
        if(StringUtil.isNotEmpty(condition.getTranSn())){
            sql.append(" AND b.tranSn = :tranSn OR b.transnRefer = :tranSnRefer ");
            sql.append(" AND b.corpId = :corpId order by b.msbUpdateTime desc");
        }
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("tranSn", condition.getTranSn());
        query.setParameter("tranSnRefer", condition.getTranSn());
        query.setParameter("corpId", corpId);
        List<?> results = query.getResultList();
        if (!CollectionUtils.isEmpty(results)) {
            list = (List<BbGuaranteeHistory>) results;
        }
        return list;
    }

    @Override
    public BbGuaranteeHistory selectBytranSn(String tranSn) {
        Optional<BbGuaranteeHistory> record = jpaRepository.findById(tranSn);
        if (record.isEmpty()) {
            return null;
        }
        return record.get();
    }

    @Override
    public BbGuaranteeHistory save(BbGuaranteeHistory bbGuaranteeHistory) {
       return jpaRepository.save(bbGuaranteeHistory);
    }

    @Override
    public BbGuaranteeHistory updateByPrimaryKeySelective(BbGuaranteeHistory bbGuaranteeHistory) {
        return jpaRepository.save(bbGuaranteeHistory);
    }

    @Override
    public List<BbGuaranteeHistory> saveAllList(List<BbGuaranteeHistory> historyList) {
        return jpaRepository.saveAll(historyList);
    }

    @Override
    public Page<BbGuaranteeHistory> findByConditions(GuaranteeListRequest request, Pageable pageable) {
        return jpaRepository.findAll(findByConditions(request), pageable);
    }

    private Specification<BbGuaranteeHistory> findByConditions(GuaranteeListRequest request) {
        return (root, query, criteriaBuilder) -> {
            //lstAndPredicate
            List<Predicate> lstAndPredicate = new ArrayList<>();
            lstAndPredicate.add(criteriaBuilder.equal(root.get("transnActive"), "1"));
            lstAndPredicate.add(criteriaBuilder.notEqual(root.get("status"), AppConstant.STATUS.NEWR));
            if (request.getCorpId() != null) {
                lstAndPredicate.add(criteriaBuilder.equal(root.get("corpId"), request.getCorpId()));
            }
            if (!CollectionUtils.isEmpty(request.getStatus())) {
                lstAndPredicate.add((root.get("status")).in(request.getStatus()));
            }
            if (!CommonStringUtils.isNullOrEmpty(request.getBpmId())) {
                lstAndPredicate.add(criteriaBuilder.equal(root.get("bpmCode"), request.getBpmId()));
            }
            if (!CommonStringUtils.isNullOrEmpty(request.getCifNo())) {
                lstAndPredicate.add(criteriaBuilder.equal(root.get("cifNo"), request.getCifNo()));
            }
            if (!CommonStringUtils.isNullOrEmpty(request.getGuaranteeType())) {
                lstAndPredicate.add(criteriaBuilder.equal(root.get("typeGuarantee"), request.getGuaranteeType()));
            }

            Date fromDate = DateUtil.getStartTimeOfDate(request.getFromDate(), DateUtil.FORMAT_DATE_ddMMyyyy);
            Date toDate = DateUtil.getEndTimeOfDate(request.getToDate(), DateUtil.FORMAT_DATE_ddMMyyyy);
            if (fromDate != null && toDate != null) {
                if ("1".equals(request.getDateBy())) {
                    lstAndPredicate.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), fromDate));
                } else if ("2".equals(request.getDateBy())) {
                    lstAndPredicate.add(criteriaBuilder.greaterThanOrEqualTo(root.get("customerSendTime"), fromDate));
                } else {
                    lstAndPredicate.add(criteriaBuilder.greaterThanOrEqualTo(root.get("msbUpdateTime"), fromDate));
                }
            }
            if (toDate != null) {
                if ("1".equals(request.getDateBy())) {
                    lstAndPredicate.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), toDate));
                } else if ("2".equals(request.getDateBy())) {
                    lstAndPredicate.add(criteriaBuilder.lessThanOrEqualTo(root.get("customerSendTime"), toDate));
                } else {
                    lstAndPredicate.add(criteriaBuilder.lessThanOrEqualTo(root.get("msbUpdateTime"), toDate));
                }
            }

            //orPredicate
            Predicate orPredicate = null;
            if (!CommonStringUtils.isNullOrEmpty(request.getKeyWord())) {
                Predicate tranSn = criteriaBuilder.like(criteriaBuilder.lower(root.get("tranSn")), "%" + request.getKeyWord().toLowerCase() + "%");
                Predicate receiveGuarantee = criteriaBuilder.like(criteriaBuilder.lower(root.get("receiveGuarantee")), "%" + request.getKeyWord().toLowerCase() + "%");
                Predicate codeGuarantee = criteriaBuilder.like(criteriaBuilder.lower(root.get("bnfcBankName")), "%" + request.getKeyWord().toLowerCase() + "%");
                Predicate amount = criteriaBuilder.like(root.get("amount"), "%" + request.getKeyWord().toLowerCase() + "%");
                orPredicate = criteriaBuilder.or(tranSn, receiveGuarantee, codeGuarantee, amount);
            }
            Predicate orPredicateTranSn = null;
            if (!CommonStringUtils.isNullOrEmpty(request.getTranSn())) {
                Predicate tranSn = criteriaBuilder.equal(root.get("tranSn"), request.getTranSn());
                Predicate tranRefer = criteriaBuilder.equal(root.get("transnRefer"), request.getTranSn());
                orPredicateTranSn = criteriaBuilder.or(tranSn, tranRefer);
            }
            //merge Predicate
            Predicate andPredicate =  criteriaBuilder.and(lstAndPredicate.toArray(new Predicate[0]));
            Predicate predicate = orPredicate != null ? criteriaBuilder.and(andPredicate, orPredicate) : andPredicate;
            return orPredicateTranSn != null ? criteriaBuilder.and(predicate, orPredicateTranSn) : predicate;
        };
    }

    @Override
    public void deleteByByTranSn(String tranSn) {
        jpaRepository.deleteById(tranSn);
    }

    @Override
    public int countStatus(String status) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(1) from BbGuaranteeHistory b where transn_active = '1' ");
        if(StringUtil.isNotEmpty(status)){
            sql.append(" AND b.status = :status ");
        }
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("status", status);
        Long result = (Long) query.getSingleResult();
        if (Objects.isNull(result)) {
            return 0;
        }
        return result.intValue();
    }

    @Override
    public List<OutputGuaranteeCount> countAllStatus() {
        List<OutputGuaranteeCount> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT status, COUNT(*) as countStatus \n" +
                "FROM BbGuaranteeHistory \n" +
                "where transn_active = '1' and status in ('DSUC','PEND','DPEN','UPDA','DUPD','REJE')\n" +
                "GROUP BY status  ");

        Query query = entityManager.createQuery(sql.toString());
        List<Object[]> results = query.getResultList();
        if (!CollectionUtils.isEmpty(results)) {
            for (Object[] obj : results) {
                OutputGuaranteeCount outputGuaranteeCount = new OutputGuaranteeCount();
                outputGuaranteeCount.setStatus(obj[0].toString());
                outputGuaranteeCount.setCountStatus( Integer.parseInt(obj[1].toString()));
                list.add(outputGuaranteeCount);
            }
        }
        return list;
    }

    @Override
    public void updateExpiredPendingCheckerApproved(List<String> lstTranSn, String toStatus) {
        Query query = entityManager.createQuery("UPDATE BbGuaranteeHistory s set s.status =:toStatus, s.statusSync =:statusSync where s.tranSn in :lstTranSn")
                .setParameter("lstTranSn", lstTranSn)
                .setParameter("toStatus", toStatus)
                .setParameter("statusSync", AppConstant.STATUS.WCAN);
        query.executeUpdate();
    }

    @Override
    public List<BbGuaranteeHistory> getGuaranteeSync(Integer maxNumberSync) {
        StringBuilder sql = new StringBuilder();
        sql.append("From BbGuaranteeHistory b where status in :statusIn and statusSync=:statusSync and nextSyncTime <= :nextSyncTime ");
        sql.append("and numSync < :numSync order by createTime desc");
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("statusIn", List.of(AppConstant.STATUS.DPEN, AppConstant.STATUS.PEND));
        query.setParameter("statusSync", AppConstant.STATUS.NEWR);
        query.setParameter("nextSyncTime", new Date());
        query.setParameter("numSync", maxNumberSync);
        return query.getResultList();
    }

    @Override
    public List<BbGuaranteeHistory> getGuaranteeCancel() {
        StringBuilder sql = new StringBuilder();
        sql.append("From BbGuaranteeHistory b where transnRefer is not null and statusSync = :statusSync ");
        sql.append(" and status in :statusIn and expiredDate > :expiredDate");
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("statusIn", List.of(AppConstant.STATUS.CKTM, AppConstant.STATUS.CKRJ));
        query.setParameter("statusSync", AppConstant.STATUS.WCAN);
        query.setParameter("expiredDate", DateUtil.addDate(new Date(), -1));
        return query.getResultList();
    }

    @Override
    public BbGuaranteeHistory getTranSnActive(String tranSn) {
        StringBuilder sql = new StringBuilder();
        sql.append("From BbGuaranteeHistory b where (transnRefer =:transnRefer or tranSn =:tranSn) and transnActive=:transnActive");
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("transnRefer", tranSn);
        query.setParameter("tranSn", tranSn);
        query.setParameter("transnActive", "1");
        List<?> results = query.getResultList();
        if (!CollectionUtils.isEmpty(results)) {
            return (BbGuaranteeHistory) results.get(0);
        }
        return null;
    }

    @Override
    public BbGuaranteeHistory getTranUpdateNotActiveLatest(String tranSn) {
        StringBuilder sql = new StringBuilder();
        sql.append("From BbGuaranteeHistory b where transnRefer =:transnRefer and transnActive=:transnActive order by tranSn desc");
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("transnRefer", tranSn);
        query.setParameter("transnActive", "0");
        List<?> results = query.getResultList();
        if (!CollectionUtils.isEmpty(results)) {
            return (BbGuaranteeHistory) results.get(0);
        }
        return null;
    }

}
