package com.msb.ibs.corp.cross.exchange.domain.repository;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocumentConfig;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;

import java.util.List;

public interface BbGuaranteeHistoryDetailRepository {

    List<BbGuaranteeHistoryDetail> selectByTranSn(String tranSn);

    List<BbGuaranteeHistoryDetail> insert(List<BbGuaranteeHistoryDetail> detail);

    void deleteByTranSn(List<BbGuaranteeHistoryDetail> detailList);

    int deleteByTranRefer(String tranRefer);

    BbGuaranteeHistoryDetail selectByTranSnAndParam(String tranSn, String paramName);
}
