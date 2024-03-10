package com.msb.ibs.corp.cross.exchange.domain.repository;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocumentConfig;

import java.util.List;

public interface BbGuaranteeDocumentConfigRepository {

    List<BbGuaranteeDocumentConfig> findListDocumentConfig(List<String> type);
}
