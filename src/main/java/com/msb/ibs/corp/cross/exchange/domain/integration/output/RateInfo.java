package com.msb.ibs.corp.cross.exchange.domain.integration.output;

import java.util.List;

public class RateInfo {
    private String code;
    private String displayName;
    private String displayNameEng;

    private List<RateDetailInfo> deals;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<RateDetailInfo> getDeals() {
        return deals;
    }

    public void setDeals(List<RateDetailInfo> deals) {
        this.deals = deals;
    }

    public String getDisplayNameEng() {
        return displayNameEng;
    }

    public void setDisplayNameEng(String displayNameEng) {
        this.displayNameEng = displayNameEng;
    }

}
