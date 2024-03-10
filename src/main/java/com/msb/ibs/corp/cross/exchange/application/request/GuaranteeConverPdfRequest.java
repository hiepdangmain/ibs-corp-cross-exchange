package com.msb.ibs.corp.cross.exchange.application.request;

import lombok.Data;

@Data
public class GuaranteeConverPdfRequest {
    String tranSn;
    String type;// 1 : xem ban nhap;2: gen don de nghi
}
