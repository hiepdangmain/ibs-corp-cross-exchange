package com.msb.ibs.corp.cross.exchange.application.response;

import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeFileAttachment;

public class OutputGuaranteeUpload {
    BbGuaranteeFileAttachment guaranteeFileAttachment;

    public BbGuaranteeFileAttachment getGuaranteeFileAttachment() {
        return guaranteeFileAttachment;
    }

    public void setGuaranteeFileAttachment(BbGuaranteeFileAttachment guaranteeFileAttachment) {
        this.guaranteeFileAttachment = guaranteeFileAttachment;
    }
}
