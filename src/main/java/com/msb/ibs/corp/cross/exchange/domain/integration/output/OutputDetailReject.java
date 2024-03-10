package com.msb.ibs.corp.cross.exchange.domain.integration.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputDetailReject {
    private  Date timeReject;
    private String contentReject;
}
