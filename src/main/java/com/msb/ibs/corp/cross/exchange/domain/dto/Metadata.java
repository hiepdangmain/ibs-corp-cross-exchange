package com.msb.ibs.corp.cross.exchange.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metadata {
    private long total;
    private Integer pageNo = 1;
    private Integer pageSize = 5;
}