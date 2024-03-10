package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.main.service.domain.input.WorkflowProcessTaskExpiredInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuaranteeCheckExpiredService {

    public static final Logger logger = LoggerFactory.getLogger(GuaranteeCheckExpiredService.class);
    private final BbGuaranteeHistoryRepository repository;

    public GuaranteeCheckExpiredService(BbGuaranteeHistoryRepository repository) {
        this.repository = repository;
    }
    @Transactional
    public void guaranteeTaskExpired(WorkflowProcessTaskExpiredInput input) {
        //cập nhật các giao dịch baor lanhx hết hạn chờ checker phê duyệt
        try {
            logger.info("Update BbGuaranteeHistory Expired Pending Checker Approved For TranSN {}", input.getTranSnList());
            repository.updateExpiredPendingCheckerApproved(input.getTranSnList(), input.getStatus());
        } catch (Exception e) {
            logger.error("Failed To update BbDomesticHistory Expired", e);
        }

    }
}
