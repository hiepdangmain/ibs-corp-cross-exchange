package com.msb.ibs.corp.cross.exchange.domain.repository;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;

import java.util.List;

public interface BbGuaranteeFileAttachmentRepository {

    BbGuaranteeFileAttachment save(BbGuaranteeFileAttachment guaranteeFileAttachment);

    List<BbGuaranteeFileAttachment> saveAll(List<BbGuaranteeFileAttachment> guaranteeFileAttachment);

    BbGuaranteeFileAttachment findById(Integer id);

    List<BbGuaranteeFileAttachment> findByTransRefer(String tranSn);

    void updateAllDocumentById (List<Integer> listId, String status, String transRefer);

    void updateAllDocumentByStatus(String tranSn, String status, String choose);

    List<BbGuaranteeFileAttachment> selectBylistId(List<Integer> listId);

    List<BbGuaranteeFileAttachment> getListRetry(Integer numUploadMax);

    List<BbGuaranteeFileAttachment> findByTranSn(String tranSn);

    BbGuaranteeFileAttachment findByTranSnAndDocumentId(String tranSn, Integer documentId);

    List<BbGuaranteeFileAttachment> getListDelete(String tranSnOld, String tranSnNew);
}
