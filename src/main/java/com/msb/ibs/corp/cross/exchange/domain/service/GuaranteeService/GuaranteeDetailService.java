package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeDetailRequest;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeDetailResponse;
import com.msb.ibs.corp.cross.exchange.domain.dto.DocumentGuaranteeDto;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeCondition;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeHistoryDetailDto;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocumentConfig;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.OutputDetailReject;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeDocumentConfigRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GuaranteeDetailService {

    @Autowired
    private BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository;
    @Autowired
    private BbGuaranteeHistoryDetailRepository bbGuaranteeHistoryDetailRepository;
    @Autowired
    private BbGuaranteeFileAttachmentRepository bbGuaranteeFileAttachmentRepository;
    @Autowired
    private BbGuaranteeDocumentConfigRepository bbGuaranteeDocumentConfigRepository;

    public GuaranteeDetailResponse execute(GuaranteeDetailRequest request) {
        if (StringUtil.isEmpty(request.getTranSn())) {
            throw new BadRequestException("ERROR.GUARANTEE.TRAN_SN.REQUIRED");
        }

        BbGuaranteeHistory guaranteeHistory = bbGuaranteeHistoryRepository.selectBytranSn(request.getTranSn());
        List<DocumentGuaranteeDto> documents = new ArrayList<>();
        List<GuaranteeHistoryDetailDto> details = new ArrayList<>();
        List<OutputDetailReject> listReject = new ArrayList<>();
        if (guaranteeHistory != null) {
            List<BbGuaranteeDocumentConfig> documentConfigs = null;
            if(StringUtil.isNotEmpty(guaranteeHistory.getTypeGuarantee())){
                documentConfigs = bbGuaranteeDocumentConfigRepository.
                        findListDocumentConfig(List.of("0", guaranteeHistory.getTypeGuarantee()));
            }


            List<BbGuaranteeFileAttachment> fileAttachments = bbGuaranteeFileAttachmentRepository.findByTransRefer(request.getTranSn());
            if(fileAttachments.size() > 0){
                // sắp xep lai list Order theo thu tu seq
                Collections.sort(fileAttachments, (m1, m2) -> m1.compareTo(m1, m2));
                for (BbGuaranteeFileAttachment fileAttachment : fileAttachments) {
                    DocumentGuaranteeDto documentGuarantee = new DocumentGuaranteeDto();
                    BeanUtils.copyProperties(fileAttachment, documentGuarantee);
                    for (BbGuaranteeDocumentConfig documentConfig : documentConfigs) {
                        if (documentConfig.getSeqNo().intValue() == fileAttachment.getDocumentId().intValue()) {
                            documentGuarantee.setDocumentNameVn(documentConfig.getDocNameVn());
                            documentGuarantee.setDocumentNameEn(documentConfig.getDocNameEn());
                        }
                    }
                    documents.add(documentGuarantee);
                }
            }
            List<BbGuaranteeHistoryDetail> historyDetails = bbGuaranteeHistoryDetailRepository.selectByTranSn(request.getTranSn());
            for (BbGuaranteeHistoryDetail detail : historyDetails) {
                GuaranteeHistoryDetailDto detailDto = new GuaranteeHistoryDetailDto();
                BeanUtils.copyProperties(detail, detailDto);
                details.add(detailDto);
            }
            // xem có bn phản hồi
            GuaranteeCondition condition = new GuaranteeCondition();
            condition.setTranSn(guaranteeHistory.getTransnRefer() != null ? guaranteeHistory.getTransnRefer() : request.getTranSn());
            List<BbGuaranteeHistory> listData = bbGuaranteeHistoryRepository.findByCondiiton(condition, guaranteeHistory.getCorpId());
            if (listData.size() > 0) {
                OutputDetailReject outputDetailReject;
                for (BbGuaranteeHistory item : listData) {
                    if (StringUtil.isNotEmpty(item.getRejectContent())) {
                        outputDetailReject = new OutputDetailReject();
                        outputDetailReject.setTimeReject(item.getMsbUpdateTime());
                        outputDetailReject.setContentReject(item.getRejectContent());
                        listReject.add(outputDetailReject);
                    }
                }
            }
        }

        return GuaranteeDetailResponse.builder()
                .guaranteeHistory(guaranteeHistory)
                .details(details)
                .documents(documents)
                .listReject(listReject)
                .build();
    }
}
