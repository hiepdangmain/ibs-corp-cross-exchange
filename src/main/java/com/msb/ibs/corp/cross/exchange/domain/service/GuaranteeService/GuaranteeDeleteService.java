package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeDeleteRequest;
import com.msb.ibs.corp.cross.exchange.domain.entity.BbGuaranteeHistory;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import com.msb.ibs.corp.main.service.domain.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuaranteeDeleteService {

    @Autowired
    BbGuaranteeHistoryRepository historyRepository;
    @Autowired
    BbGuaranteeHistoryDetailRepository detailRepository;
    @Autowired
    WorkflowService workflowService;

    public boolean execute (InternalRequest internalRequest, GuaranteeDeleteRequest guaranteeRequest) throws LogicException {
        if (StringUtil.isEmpty(guaranteeRequest.getTranSn())) {
            throw new BadRequestException("ERROR.GUARANTEE.REQUEST.INVALID");
        }
        BbGuaranteeHistory guaranteeHistory = historyRepository.selectBytranSn(guaranteeRequest.getTranSn());
        if (guaranteeHistory == null)
            throw new LogicException("ERROR.GUARANTEE.DELETE.WRONG.TRAN.SN");
        if( guaranteeHistory.getCreateBy().compareTo(internalRequest.getUserPrincipal().getUserId()) != 0)
            throw new LogicException("ERROR.GUARANTEE.DELETE.USER.ID.AND.STATUS.WRONG");
        if(!(guaranteeHistory.getStatus().equalsIgnoreCase(AppConstant.STATUS.DRAF)
                || guaranteeHistory.getStatus().equalsIgnoreCase(AppConstant.STATUS.CKNG)))
            throw new LogicException("ERROR.GUARANTEE.DELETE.USER.ID.AND.STATUS.WRONG");

        try {
            // xoa o bang history
            historyRepository.deleteByByTranSn(guaranteeHistory.getTranSn());
            // xoa o bang detail
            // List<BbGuaranteeHistoryDetail> listDetail =  detailRepository.selectByTranSn(guaranteeHistory.getTranSn());
            int a = detailRepository.deleteByTranRefer(guaranteeHistory.getTranSn());
            // xóa lệnh chờ duyệt trong quy trình duyệt
            if (AppConstant.STATUS.CKNG.equalsIgnoreCase(guaranteeHistory.getStatus())
                /*|| AppConstant.STATUS.DUPD.equalsIgnoreCase(guaranteeHistory.getStatus())*/) {
                List<String> wfProcessId = new ArrayList<>();
                wfProcessId.add(guaranteeHistory.getWfProcessId());
                workflowService.cancelTaskWorkflow(wfProcessId, internalRequest.getToken());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

}
