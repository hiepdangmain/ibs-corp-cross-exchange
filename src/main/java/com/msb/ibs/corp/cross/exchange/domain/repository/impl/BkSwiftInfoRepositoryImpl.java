package com.msb.ibs.corp.cross.exchange.domain.repository.impl;

import com.msb.ibs.corp.cross.exchange.domain.entity.BkSwiftInfo;
import com.msb.ibs.corp.cross.exchange.domain.repository.BkSwiftInfoRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BbGuaranteeFileAttachmentJpaRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BkSwiftInfoJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class BkSwiftInfoRepositoryImpl implements BkSwiftInfoRepository {

    @Autowired
    private BkSwiftInfoJpaRepository jpaRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public BkSwiftInfo findBkSwiftInfoByswiftCode(String swiftCode) {
        return jpaRepository.findBkSwiftInfoByswiftCode(swiftCode);
    }
}
