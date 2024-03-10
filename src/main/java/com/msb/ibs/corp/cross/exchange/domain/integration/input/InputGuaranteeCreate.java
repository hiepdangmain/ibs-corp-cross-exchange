package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import lombok.Data;

import java.util.List;

@Data
public class InputGuaranteeCreate {
    private String ibCorpId;
    private Long cifNumber;
    private String businessCode;
    private String releaseForm;
    private String swiftCode;
    private String formType;
    private String typeBusiness;
    private String creditContractNumber;
    private InfoRequirement infoRequirement;
    private List<CheckListItem> checklist;
    private InputGuaranteeMakerInfo ibCorpMakerInfo;
}
