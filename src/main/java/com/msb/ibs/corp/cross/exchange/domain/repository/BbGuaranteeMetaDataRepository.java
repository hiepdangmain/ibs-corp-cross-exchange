package com.msb.ibs.corp.cross.exchange.domain.repository;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeMetaData;

import java.util.List;

public interface BbGuaranteeMetaDataRepository {
    BbGuaranteeMetaData findBbGuaranteeMetaDataByCorpIdAndType(String applicant, String beneficiaryName, String type, Integer corpId);

    List<BbGuaranteeMetaData> getListBbGuaranteeMetaDataByCorpId(Integer corpId);

    BbGuaranteeMetaData save(BbGuaranteeMetaData bbGuaranteeMetaData);


}
