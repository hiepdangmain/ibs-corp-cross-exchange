package com.msb.ibs.corp.cross.exchange.domain.repository;

import com.msb.ibs.corp.cross.exchange.domain.entity.BkSwiftInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BkSwiftInfoRepository {

    BkSwiftInfo findBkSwiftInfoByswiftCode (String swiftCode);
}
