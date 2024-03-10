package com.msb.ibs.corp.cross.exchange.domain.integration.input;

import lombok.Data;

import java.util.List;

@Data
public class InputGuaranteeUpdate {
    private String ibCorpId;
    private Long cifNumber;
    private String businessCode;
    private String releaseForm;
    private String swiftCode;
    private String signDate;
    private String formType;
    private String creditContractNumber;
    private String feedBack;
    private InfoRequirement infoRequirement;
    private List<CheckListItem> checklist;
}
