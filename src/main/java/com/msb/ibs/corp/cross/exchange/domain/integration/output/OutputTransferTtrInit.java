package com.msb.ibs.corp.cross.exchange.domain.integration.output;

public class OutputTransferTtrInit {
    private String tranSn;
    private String certCode;
    private String taxCode;
    private String rolloutAddress;

    public String getTranSn() {
        return tranSn;
    }

    public void setTranSn(String tranSn) {
        this.tranSn = tranSn;
    }

    public String getCertCode() {
        return certCode;
    }

    public void setCertCode(String certCode) {
        this.certCode = certCode;
    }

    public String getTaxCode() {
        return taxCode;
    }

    public void setTaxCode(String taxCode) {
        this.taxCode = taxCode;
    }

    public String getRolloutAddress() {
        return rolloutAddress;
    }

    public void setRolloutAddress(String rolloutAddress) {
        this.rolloutAddress = rolloutAddress;
    }
}
