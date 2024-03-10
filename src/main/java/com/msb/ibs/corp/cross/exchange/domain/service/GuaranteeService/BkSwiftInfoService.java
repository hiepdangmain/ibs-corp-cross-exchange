package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.corp.cross.exchange.application.request.SwiftInfoRequest;
import com.msb.ibs.corp.cross.exchange.domain.entity.BkSwiftInfo;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BkSwiftInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class BkSwiftInfoService {

    private BkSwiftInfoRepository bkSwiftInfoRepository;

    public BkSwiftInfoService(BkSwiftInfoRepository bkSwiftInfoRepository) {
        this.bkSwiftInfoRepository = bkSwiftInfoRepository;
    }

    public BkSwiftInfo getSwiftInfo (UserPrincipal principal, SwiftInfoRequest input){
        return bkSwiftInfoRepository.findBkSwiftInfoByswiftCode(input.getSwiftInfo());
    }
}
