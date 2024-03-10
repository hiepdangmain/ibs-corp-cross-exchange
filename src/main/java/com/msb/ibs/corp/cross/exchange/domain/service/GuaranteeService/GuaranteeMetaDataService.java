package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistoryDetail;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeMetaData;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeMetaDataRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuaranteeMetaDataService {
    public static final Logger logger = LoggerFactory.getLogger(GuaranteeMetaDataService.class);
    private final BbGuaranteeMetaDataRepository bbGuaranteeMetaDataRepository;
    private final BbGuaranteeHistoryDetailRepository guaranteeHistoryDetailRepository;


    public List<BbGuaranteeMetaData> getListBbGuaranteeMetaDataByCorpId(Integer corpId) {
        return bbGuaranteeMetaDataRepository.getListBbGuaranteeMetaDataByCorpId(corpId);
    }

    @Transactional
    public void insertBbGuaranteeMetaData(BbGuaranteeHistory bbGuaranteeHistory) {
        try {
            List<BbGuaranteeHistoryDetail> guaranteeHistoryDetailList = guaranteeHistoryDetailRepository.selectByTranSn(bbGuaranteeHistory.getTranSn());
            if (!CollectionUtils.isEmpty(guaranteeHistoryDetailList)) {
                Map<String, String> mapParams = guaranteeHistoryDetailList.stream().collect(Collectors.toMap(BbGuaranteeHistoryDetail::getParam, BbGuaranteeHistoryDetail::getValue));
                //luu thong tin goi nho ben bao lanh
                insertGuaranteeApplicant(mapParams, bbGuaranteeHistory);

                //luu thong tin goi nho ben duoc bao lanh
                insertGuaranteeBeneficiary(mapParams, bbGuaranteeHistory);
            }
        } catch (Exception e) {
            logger.info("Failed To insertBbGuaranteeMetaData For CorpId {}", bbGuaranteeHistory.getCorpId());
        }
    }

    private void insertGuaranteeApplicant(Map<String, String> mapParams, BbGuaranteeHistory bbGuaranteeHistory) {
        String applicant = mapParams.get(AppConstant.BB_GUARANTEE_FIELD.applicant);
        String certCode = mapParams.get(AppConstant.BB_GUARANTEE_FIELD.certCode);
        String certCodeDate = mapParams.get(AppConstant.BB_GUARANTEE_FIELD.certCodeDate);
        String certCodePlace = mapParams.get(AppConstant.BB_GUARANTEE_FIELD.certCodePlace);
        String type = "applicant";
        boolean isChange = false;
        BbGuaranteeMetaData bbGuaranteeMetaData = bbGuaranteeMetaDataRepository.findBbGuaranteeMetaDataByCorpIdAndType(applicant, null, type, bbGuaranteeHistory.getCorpId());
        if (bbGuaranteeMetaData == null) {
            bbGuaranteeMetaData = new BbGuaranteeMetaData();
            bbGuaranteeMetaData.setId(UUID.randomUUID().toString());
            bbGuaranteeMetaData.setUserId(bbGuaranteeHistory.getUserId());
            bbGuaranteeMetaData.setCorpId(bbGuaranteeHistory.getCorpId());
            bbGuaranteeMetaData.setType(type);
        } else {
            isChange = applicant.equals(bbGuaranteeMetaData.getApplicant()) && certCode.equals(bbGuaranteeMetaData.getCertCode())
                    && certCodeDate.equals(bbGuaranteeMetaData.getCertCodeDate()) && certCodePlace.equals(bbGuaranteeMetaData.getCertCodePlace());
        }
        if (!isChange) {
            logger.info("InsertGuaranteeMetaDataApplicant");
            bbGuaranteeMetaData.setApplicant(mapParams.get(AppConstant.BB_GUARANTEE_FIELD.applicant));
            bbGuaranteeMetaData.setCertCode(mapParams.get(AppConstant.BB_GUARANTEE_FIELD.certCode));
            bbGuaranteeMetaData.setCertCodeDate(mapParams.get(AppConstant.BB_GUARANTEE_FIELD.certCodeDate));
            bbGuaranteeMetaData.setCertCodePlace(mapParams.get(AppConstant.BB_GUARANTEE_FIELD.certCodePlace));
            bbGuaranteeMetaDataRepository.save(bbGuaranteeMetaData);
        }
    }

    private void insertGuaranteeBeneficiary(Map<String, String> mapParams, BbGuaranteeHistory bbGuaranteeHistory) {
        String beneficiary = mapParams.get(AppConstant.BB_GUARANTEE_FIELD.beneficiary);
        String beneficiaryAddress = mapParams.get(AppConstant.BB_GUARANTEE_FIELD.beneficiaryAddress);
        String type = "beneficiary";
        boolean isChange = false;
        BbGuaranteeMetaData bbGuaranteeMetaData = bbGuaranteeMetaDataRepository.findBbGuaranteeMetaDataByCorpIdAndType(null, beneficiary, type, bbGuaranteeHistory.getCorpId());
        if (bbGuaranteeMetaData == null) {
            bbGuaranteeMetaData = new BbGuaranteeMetaData();
            bbGuaranteeMetaData.setId(UUID.randomUUID().toString());
            bbGuaranteeMetaData.setUserId(bbGuaranteeHistory.getUserId());
            bbGuaranteeMetaData.setCorpId(bbGuaranteeHistory.getCorpId());
            bbGuaranteeMetaData.setType(type);
        } else {
            isChange = beneficiary.equals(bbGuaranteeMetaData.getBeneficiary()) && beneficiaryAddress.equals(bbGuaranteeMetaData.getBeneficiaryAddress());
        }
        if (!isChange) {
            logger.info("InsertGuaranteeMetaDataBeneficiary");
            bbGuaranteeMetaData.setBeneficiary(mapParams.get(AppConstant.BB_GUARANTEE_FIELD.beneficiary));
            bbGuaranteeMetaData.setBeneficiaryAddress(mapParams.get(AppConstant.BB_GUARANTEE_FIELD.beneficiaryAddress));
            bbGuaranteeMetaDataRepository.save(bbGuaranteeMetaData);
        }
    }

}
