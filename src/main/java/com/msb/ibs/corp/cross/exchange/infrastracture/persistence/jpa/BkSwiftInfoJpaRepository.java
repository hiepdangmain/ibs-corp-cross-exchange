package com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa;

import com.msb.ibs.corp.cross.exchange.domain.entity.BkSwiftInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BkSwiftInfoJpaRepository extends JpaRepository<BkSwiftInfo, String>, JpaSpecificationExecutor<BkSwiftInfo> {

    BkSwiftInfo findBkSwiftInfoByswiftCode (String swiftCode);
}
