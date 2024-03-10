package com.msb.ibs.corp.cross.exchange.infrastracture.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbTtrHistory;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbTtrHistoryRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BbTtrHistoryJpaRepository;

@Component
public class BbTtrHistoryRepositoryImpl implements BbTtrHistoryRepository {


	@Autowired
	private BbTtrHistoryJpaRepository bbTtrHistoryJpaRepository;
	
	@PersistenceContext
	EntityManager entityManager;

	@Override
	public BbTtrHistory save(BbTtrHistory record) {
		return bbTtrHistoryJpaRepository.save(record);
	}

}
