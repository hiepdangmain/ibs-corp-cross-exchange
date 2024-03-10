package com.msb.ibs.corp.cross.exchange.domain.integration.output;

import com.msb.ibs.corp.cross.exchange.domain.dto.AccountTtrHistoryInfo;
import com.msb.ibs.corp.cross.exchange.domain.dto.Metadata;
import com.msb.ibs.corp.cross.exchange.domain.entity.BkAccountHistory;

import java.math.BigDecimal;
import java.util.List;

public class OutputTtrAccountHistory {
    private List<AccountTtrHistoryInfo> accountHistoryList;
    private List<BkAccountHistory> coreHistoryList;
    private BigDecimal openingBalance;
    private BigDecimal endingBalance;
    private Metadata metadata;

    public List<AccountTtrHistoryInfo> getAccountHistoryList() {
        return accountHistoryList;
    }
    public void setAccountHistoryList(List<AccountTtrHistoryInfo> accountHistoryList) {
        this.accountHistoryList = accountHistoryList;
    }
    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }
    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }
    public BigDecimal getEndingBalance() {
        return endingBalance;
    }
    public void setEndingBalance(BigDecimal endingBalance) {
        this.endingBalance = endingBalance;
    }
    public Metadata getMetadata() {
        return metadata;
    }
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
    public List<BkAccountHistory> getCoreHistoryList() {
        return coreHistoryList;
    }
    public void setCoreHistoryList(List<BkAccountHistory> coreHistoryList) {
        this.coreHistoryList = coreHistoryList;
    }
}
