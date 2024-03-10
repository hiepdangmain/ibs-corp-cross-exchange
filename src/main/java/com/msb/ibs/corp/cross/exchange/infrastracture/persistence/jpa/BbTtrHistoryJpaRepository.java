package com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbTtrHistory;

@Repository
public interface BbTtrHistoryJpaRepository extends JpaRepository<BbTtrHistory, String> {

}
