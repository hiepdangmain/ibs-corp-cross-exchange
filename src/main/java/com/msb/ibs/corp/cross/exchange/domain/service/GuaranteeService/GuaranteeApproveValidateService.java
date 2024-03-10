package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BkSwiftInfoRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.Common.CommonService;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeCommonService;
import com.msb.ibs.corp.main.service.domain.input.ApproveTransactionValidateInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeApproveValidateService {
    private BkSwiftInfoRepository bkSwiftInfoRepository;
    private GuaranteeCommonService guaranteeCommonService;
    private CommonService commonService;
    private String urlCorp ="";
    private boolean lastChecker = false;
    private BbGuaranteeHistory guaranteeHistory ;
    private BbGuaranteeHistoryRepository historyRepository;

    private static final Logger logger = LoggerFactory.getLogger(GuaranteeApproveValidateService.class);

    public GuaranteeApproveValidateService(BkSwiftInfoRepository bkSwiftInfoRepository,
                                           GuaranteeCommonService guaranteeCommonService,
                                           CommonService commonService,
                                           BbGuaranteeHistoryRepository historyRepository,
                                           Environment environment) {
        this.bkSwiftInfoRepository = bkSwiftInfoRepository;
        this.guaranteeCommonService = guaranteeCommonService;
        this.commonService = commonService;
        this.historyRepository = historyRepository;
        this.urlCorp = environment.getProperty("url.corp");
    }

    public boolean validateApprove(UserPrincipal principal, ApproveTransactionValidateInput request) throws LogicException {
        logger.info("=============== START VALIDATE GUARANTEE CREATE ===============");
        long startTime = System.currentTimeMillis();
        if (Objects.isNull(request)) {
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        }
        if (Objects.isNull(request.getTranSnList())) {
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        }
        if(request.getTranSnList().size() > 1) {
            throw new BadRequestException("ERROR.GUARANTEE.APPROVE.REQUEST.INVALID");
        }
        guaranteeHistory = historyRepository.selectBytranSn(request.getTranSnList().get(0));
        if (guaranteeHistory != null) {
            logger.info("QuyenCV2 validateApprove getLastCk : " + guaranteeHistory.getLastCk());
            checkLastCk(guaranteeHistory.getLastCk());
        } else {
            throw new LogicException("ERROR.GUARANTEE.CAN.NOT.APPROVE.THIS.RECORD");
        }
//        if(lastChecker){
//         logger.info("QuyenCV2 validateApprove check CA : " + guaranteeHistory.getLastCk());
//            if(!"CA".equals(principal.getSecurityType()))
//                throw new LogicException("ERROR.GUARANTEE.APPROVE.CA");
//        }
        logger.info("Validated successfully!");
        long endTime = System.currentTimeMillis();
        long millis = endTime - startTime;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        logger.info("Total processing time: {} (millis) => {} (seconds)", millis, seconds);

        logger.info("=============== END VALIDATE GUARANTEE CREATE ===============");
        return true;
    }

    private void checkLastCk(String lastCk) {
        if(StringUtil.isNotEmpty(lastCk)){
            LinkedHashSet<String> s2 = new LinkedHashSet<>(Arrays.asList(lastCk.split("\\/")));
            logger.info("QuyenCV2 validateApprove checkLastCk lastChecker s2 : " + s2.size());
            if (s2.size() == 1)
                lastChecker = true;
            logger.info("QuyenCV2 validateApprove checkLastCk lastChecker : " + lastChecker);
        }
    }
}
