package com.msb.ibs.corp.cross.exchange.domain.dispatcher;

import com.msb.ibs.common.base.Query;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.corp.cross.exchange.application.request.TtrOrderRequest;
import com.msb.ibs.corp.cross.exchange.domain.integration.input.*;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.*;
import com.msb.ibs.corp.main.service.domain.input.TransactionConfirmInput;
import com.msb.ibs.corp.main.service.domain.output.StepOneToConfirmOutput;
import com.msb.ibs.corp.main.service.domain.output.TransactionConfirmOutput;
import org.springframework.http.ResponseEntity;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
public interface ITtrCreateService
		extends Query<TtrOrderRequest, ResponseEntity<?>> {

	public OutputTransferTtrInit init();

	public OutputSysParamTtr getSysParameterTtr();

	public TransactionConfirmOutput confirmTransaction(TransactionConfirmInput input, InternalRequest internalRequest);

	public StepOneToConfirmOutput createRateUpdate(InputTtrUpdateRate input , InternalRequest internalRequest);

	public RateInfo getRateDeal(InputTtrGetRate input , InternalRequest internalRequest);

	public RateInfo viewDetailTTR(InputTtrTranSn input , InternalRequest internalRequest);

	public StepOneToConfirmOutput updateTTR(InputTtrUpdate input , InternalRequest internalRequest);

	public OutputTtrAccountHistory accountHistoryTTR(InputAccountHistoryTtr input , InternalRequest internalRequest);

	public StepOneToConfirmOutput cancelTTR(InputTtrTranSn input , InternalRequest internalRequest);

	public OutputTtrValidateFee approveGetFee(InputAuthTransactionTTR input , InternalRequest internalRequest);

	public OutputTtrValidateFee approveValidateYet(InputAuthTransactionTTR input , InternalRequest internalRequest);

	public OutputDownloadFile downloadFileTTR(InputDownloadFile input , InternalRequest internalRequest);

	public TransactionConfirmOutput approveTTRTransactionPost(InputAuthTransaction input , InternalRequest internalRequest);

	public TransactionConfirmOutput approveTTRTransactionPut(TransactionConfirmInput input, InternalRequest internalRequest);
}
