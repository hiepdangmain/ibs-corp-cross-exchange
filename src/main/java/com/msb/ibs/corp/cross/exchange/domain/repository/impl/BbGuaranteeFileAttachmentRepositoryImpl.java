package com.msb.ibs.corp.cross.exchange.domain.repository.impl;

import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.enums.AttachmentTypeEnum;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.persistence.jpa.BbGuaranteeFileAttachmentJpaRepository;
import com.msb.ibs.corp.main.service.domain.enums.TransactionStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class BbGuaranteeFileAttachmentRepositoryImpl implements BbGuaranteeFileAttachmentRepository {

    @Autowired
    private BbGuaranteeFileAttachmentJpaRepository jpaRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public BbGuaranteeFileAttachment save(BbGuaranteeFileAttachment guaranteeFileAttachment) {
        return jpaRepository.save(guaranteeFileAttachment);
    }

    @Override
    public List<BbGuaranteeFileAttachment> saveAll(List<BbGuaranteeFileAttachment> guaranteeFileAttachment) {
        return jpaRepository.saveAll(guaranteeFileAttachment);
    }

    @Override
    public BbGuaranteeFileAttachment findById(Integer id) {
        Optional<BbGuaranteeFileAttachment> record = jpaRepository.findById(id);
        if (record.isEmpty()) {
            return null;
        }
        return record.get();
    }

    @Override
    public List<BbGuaranteeFileAttachment> findByTransRefer(String tranSn) {
        Query query = entityManager.createQuery("select b from BbGuaranteeFileAttachment b where b.transRefer =:transRefer and b.status not in ('NEWR')  order by b.id asc", BbGuaranteeFileAttachment.class);
        query.setParameter("transRefer", tranSn);
//        query.setParameter("choose", "Y");
        return query.getResultList();
//        return jpaRepository.findBbGuaranteeFileAttachmentsBytransRefer(tranSn);
    }

    @Override
    public void updateAllDocumentById(List<Integer> listId, String status, String transRefer) {
        Query query = entityManager.createQuery("update BbGuaranteeFileAttachment s set s.status =:status, s.transRefer = :transRefer where s.id in (:listId)")
                .setParameter("listId", listId)
                .setParameter("status", status)
//                .setParameter("choose", "Y")
                .setParameter("transRefer", transRefer);

        query.executeUpdate();
    }

    @Override
    public void updateAllDocumentByStatus(String tranSn, String status, String choose) {
        Query query = entityManager.createQuery("update BbGuaranteeFileAttachment s set s.status =:status where s.status = (:statusOld) and s.transRefer =:tranRefer")
                .setParameter("status", status)
                .setParameter("statusOld", AppConstant.STATUS_RECORD.DPEN)
//                .setParameter("choose", choose)
                .setParameter("tranRefer", tranSn);
        query.executeUpdate();

    }

    @Override
    public List<BbGuaranteeFileAttachment> selectBylistId(List<Integer> listId) {
        Query query = entityManager.createQuery("select b from BbGuaranteeFileAttachment b where b.id in (:listId) order by b.id asc", BbGuaranteeFileAttachment.class);
        query.setParameter("listId", listId);
        return query.getResultList();
    }

    @Override
    public List<BbGuaranteeFileAttachment> getListRetry(Integer numUploadMax) {
        StringBuilder sql = new StringBuilder();
        sql.append("From BbGuaranteeFileAttachment where status in :statusIn and attachmentType =:attachmentType ");
        sql.append("and numUpload <= :numUploadMax and nextTime <= :sysDate");
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter("statusIn", List.of(AppConstant.STATUS_RECORD.FAIL, AppConstant.STATUS_RECORD.DPEN));
        query.setParameter("attachmentType", AttachmentTypeEnum.SERVER.value());
        query.setParameter("numUploadMax", numUploadMax);
        query.setParameter("sysDate", new Date());
        return query.getResultList();
    }

    @Override
    public List<BbGuaranteeFileAttachment> findByTranSn(String tranSn) {
        Query query = entityManager.createQuery("select b from BbGuaranteeFileAttachment b where b.transRefer =:transRefer order by b.id asc", BbGuaranteeFileAttachment.class);
        query.setParameter("transRefer", tranSn);
        return query.getResultList();
    }

    @Override
    public BbGuaranteeFileAttachment findByTranSnAndDocumentId(String tranSn, Integer documentId) {
        Query query = entityManager.createQuery("select b from BbGuaranteeFileAttachment b " +
                "where b.transRefer =:transRefer and b.documentId = :documentId order by b.id asc", BbGuaranteeFileAttachment.class);
        query.setParameter("transRefer", tranSn);
        query.setParameter("documentId", documentId);
        List<?> list = query.getResultList();
        if (!CollectionUtils.isEmpty(list)) {
            return (BbGuaranteeFileAttachment) list.get(0);
        }
        return null;
    }

    @Override
    public List<BbGuaranteeFileAttachment> getListDelete(String tranSnOld, String tranSnNew) {
        Query query = entityManager.createQuery("select b from BbGuaranteeFileAttachment b " +
                "where b.transRefer =:tranSnOld and b.attachmentType =:attachmentType and b.documentId not in " +
                " (select c.documentId from BbGuaranteeFileAttachment c where c.transRefer =:tranSnNew " +
                " and c.attachmentType =:attachmentType) ", BbGuaranteeFileAttachment.class);
        query.setParameter("tranSnOld", tranSnOld);
        query.setParameter("attachmentType", AttachmentTypeEnum.MIN_IO.value());
        query.setParameter("tranSnNew", tranSnNew);
        return query.getResultList();
    }
}
