package com.msb.ibs.corp.cross.exchange.domain.repository;

import com.msb.ibs.corp.cross.exchange.domain.entity.BkConfigGateway;
import org.springframework.data.repository.CrudRepository;

public interface BkConfigGatewayRepository extends CrudRepository<BkConfigGateway, Integer> {
    BkConfigGateway findByCode(String code);
}
