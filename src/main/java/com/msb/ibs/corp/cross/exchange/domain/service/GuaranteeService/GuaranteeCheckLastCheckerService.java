package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeDeleteRequest;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuanranteeLastChecker;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashSet;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class GuaranteeCheckLastCheckerService {

    private static final Logger logger = LoggerFactory.getLogger(GuaranteeCheckLastCheckerService.class);

    @Autowired
    BbGuaranteeHistoryRepository historyRepository;

    private boolean lastChecker = false;

    OutputGuanranteeLastChecker output = null;

    public OutputGuanranteeLastChecker execute (InternalRequest internalRequest, GuaranteeDeleteRequest guaranteeRequest) throws LogicException {
        if (StringUtil.isEmpty(guaranteeRequest.getTranSn())) {
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        }
        BbGuaranteeHistory guaranteeHistory = historyRepository.selectBytranSn(guaranteeRequest.getTranSn());
        if (guaranteeHistory == null)
            throw new LogicException("ERROR.GUARANTEE.CHECK.WRONG.TRAN.SN");
        if (!guaranteeHistory.getCifNo().equalsIgnoreCase(internalRequest.getUserPrincipal().getCifNo()))
            throw new LogicException("ERROR.GUARANTEE.CHECK.WRONG.TRAN.SN");
        checkLastCk(guaranteeHistory.getLastCk());
        output = new OutputGuanranteeLastChecker();
        output.setLastChecker(lastChecker);
        return output;
    }

    private void checkLastCk(String lastCk) {
        if(StringUtil.isNotEmpty(lastCk)){
            LinkedHashSet<String> s2 = new LinkedHashSet<>(Arrays.asList(lastCk.split("\\/")));
            logger.info("QuyenCV2 GuaranteeCheckLastCheckerService checkLastCk lastChecker s2 : " + s2.size());
            if (s2.size() == 1)
                lastChecker = true;
            logger.info("QuyenCV2 GuaranteeCheckLastCheckerService checkLastCk lastChecker : " + lastChecker);
        }
    }
}
