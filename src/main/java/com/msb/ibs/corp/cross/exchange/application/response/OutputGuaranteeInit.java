package com.msb.ibs.corp.cross.exchange.application.response;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocument;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeDocumentConfig;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeMetaData;
import lombok.Data;

import java.util.List;

@Data
public class OutputGuaranteeInit {

    private String corpName;
    private String cifNo;
    private String certType;
    private String certCode;
    private String address;
    private String telephone;
    private String email;
    private List<BbGuaranteeDocument> listCurrency;
    private List<BbGuaranteeDocument> listForm;
    private List<BbGuaranteeDocument> listTypeGuarantee;
    private List<BbGuaranteeDocument> listValidityPeriodStart;
    private List<BbGuaranteeDocument> listValidityPeriodEnd;
    private List<BbGuaranteeDocument> listCommitGuarantee;
    private List<BbGuaranteeDocumentConfig> listFileType1;
    private List<BbGuaranteeDocumentConfig> listFileType2;
    private List<BbGuaranteeDocumentConfig> listFileType3;
    private List<BbGuaranteeDocumentConfig> listFileType4;
    private List<BbGuaranteeDocumentConfig> listFileType5;
    private List<BbGuaranteeDocumentConfig> listFileType6;
    private List<BbGuaranteeDocumentConfig> listFileType7;
    private List<BbGuaranteeDocumentConfig> listFileType8;
    private List<BbGuaranteeDocumentConfig> listFileType9;
    private List<BbGuaranteeDocument> listOrder;
    private String newTranSn;
    private List<BbGuaranteeMetaData> listGuaranteeMetaData;
}
