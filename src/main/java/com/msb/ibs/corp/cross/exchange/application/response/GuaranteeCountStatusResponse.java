package com.msb.ibs.corp.cross.exchange.application.response;

import com.msb.ibs.corp.cross.exchange.domain.dto.DocumentGuaranteeDto;
import com.msb.ibs.corp.cross.exchange.domain.dto.GuaranteeHistoryDetailDto;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuaranteeCountStatusResponse {
    private int countPend; // chơ tiếp nhân
    private int countDpend; // MSB đang xử lí
    private int countUpdate; // Yêu cầu chỉnh sửa
    private int countSucc; // Thành công
    private int countFail; // Từ chối
}
