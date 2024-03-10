package com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService;

import com.msb.ibs.common.base.QueryPrinciple;
import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.utils.ResponseUtils;
import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeListRequest;
import com.msb.ibs.corp.cross.exchange.application.response.GuaranteeCountStatusResponse;
import com.msb.ibs.corp.cross.exchange.application.response.OutputGuaranteeCount;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeDocumentConfigRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeFileAttachmentRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryDetailRepository;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbGuaranteeHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class GuaranteeCountStatusService implements QueryPrinciple<GuaranteeListRequest, ResponseEntity<?>, UserPrincipal> {

    @Autowired
    private BbGuaranteeHistoryRepository bbGuaranteeHistoryRepository;

    @Override
    public void validate(GuaranteeListRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {

    }

    @Override
    public ResponseEntity<?> process(GuaranteeListRequest request, UserPrincipal principle, HttpServletRequest httpRequest) {
        GuaranteeCountStatusResponse outPut = new GuaranteeCountStatusResponse();
        /*outPut.setCountSucc(bbGuaranteeHistoryRepository.countStatus(AppConstant.STATUS.DSUC)); // Thanh cong
        outPut.setCountPend(bbGuaranteeHistoryRepository.countStatus(AppConstant.STATUS.PEND));// cho tiep nhan
        outPut.setCountDpend(bbGuaranteeHistoryRepository.countStatus(AppConstant.STATUS.DPEN)); // MSB dang xu li
        *//*int upda= bbGuaranteeHistoryRepository.countStatus(AppConstant.STATUS.UPDA);
        int dupd = bbGuaranteeHistoryRepository.countStatus(AppConstant.STATUS.DUPD);*//*
        outPut.setCountUpdate(upda + dupd);// Yeu cau chinh sua
        outPut.setCountFail(bbGuaranteeHistoryRepository.countStatus(AppConstant.STATUS.REJE)); // TU choi*/

        List<OutputGuaranteeCount> outputGuaranteeCounts = bbGuaranteeHistoryRepository.countAllStatus();
        if (outputGuaranteeCounts.size() > 0) {
            int upda = 0;
            int dupd = 0;
            for (OutputGuaranteeCount item : outputGuaranteeCounts) {
                if(AppConstant.STATUS.DSUC.equalsIgnoreCase(item.getStatus())) {
                    outPut.setCountSucc(item.getCountStatus());
                } else if (AppConstant.STATUS.PEND.equalsIgnoreCase(item.getStatus())){
                    outPut.setCountPend(item.getCountStatus());
                } else if (AppConstant.STATUS.DPEN.equalsIgnoreCase(item.getStatus())){
                    outPut.setCountDpend(item.getCountStatus());
                } else if (AppConstant.STATUS.UPDA.equalsIgnoreCase(item.getStatus())){
                    upda = item.getCountStatus();
                } else if (AppConstant.STATUS.DUPD.equalsIgnoreCase(item.getStatus())){
                    dupd = item.getCountStatus();
                } else if (AppConstant.STATUS.REJE.equalsIgnoreCase(item.getStatus())){
                    outPut.setCountFail(item.getCountStatus());
                }
                outPut.setCountUpdate(upda + dupd);
            }
        }
        return ResponseUtils.success(outPut);
    }
}
