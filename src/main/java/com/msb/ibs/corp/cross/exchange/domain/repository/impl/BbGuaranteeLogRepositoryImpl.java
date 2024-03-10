package com.msb.ibs.corp.cross.exchange.domain.repository.impl;

import com.msb.ibs.common.utils.CommonStringUtils;
import com.msb.ibs.common.utils.DateUtil;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeListRequest;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeLog;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeLogRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BbGuaranteeLogJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class BbGuaranteeLogRepositoryImpl implements BbGuaranteeLogRepository {

    @Autowired
    private BbGuaranteeLogJpaRepository bbGuaranteeLogJpaRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Override
    public BbGuaranteeLog save(BbGuaranteeLog bbGuaranteeLog) {
        return bbGuaranteeLogJpaRepository.save(bbGuaranteeLog);
    }

    @Override
    public Page<BbGuaranteeLog> findByConditions(GuaranteeListRequest request, Pageable pageable) {
        return bbGuaranteeLogJpaRepository.findAll(findByConditions(request), pageable);
    }

    private Specification<BbGuaranteeLog> findByConditions(GuaranteeListRequest request) {
        return (root, query, criteriaBuilder) -> {
            //lstAndPredicate
            List<Predicate> lstAndPredicate = new ArrayList<>();
            if (!CollectionUtils.isEmpty(request.getStatus())) {
                lstAndPredicate.add((root.get("newStatus")).in(request.getStatus()));
            }
//            if (!CommonStringUtils.isNullOrEmpty(request.getTranSn())) {
//                lstAndPredicate.add(criteriaBuilder.equal(root.get("tranSn"), request.getTranSn()));
//            }
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
            if (!CommonStringUtils.isNullOrEmpty(request.getTranSn())) {
                 Predicate tranSn = criteriaBuilder.equal(root.get("tranSn"), request.getTranSn());
                 Predicate tranSnRefer = criteriaBuilder.equal(root.get("transnRefer"), request.getTranSn());
                 orPredicate = criteriaBuilder.or(tranSn, tranSnRefer);
            }
            //merge Predicate
            Predicate andPredicate =  criteriaBuilder.and(lstAndPredicate.toArray(new Predicate[0]));
            return orPredicate != null ? criteriaBuilder.and(andPredicate, orPredicate) : andPredicate;
        };
    }
}
