package com.msb.ibs.corp.cross.exchange.domain.service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.corp.cross.exchange.domain.integration.input.*;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.*;
import com.msb.ibs.corp.main.service.domain.input.TransactionConfirmInput;
import com.msb.ibs.corp.main.service.domain.output.StepOneToConfirmOutput;
import com.msb.ibs.corp.main.service.domain.output.TransactionConfirmOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.msb.ibs.common.exception.BadRequestException;
import com.msb.ibs.corp.cross.exchange.application.request.TtrOrderRequest;
import com.msb.ibs.corp.cross.exchange.domain.dispatcher.ITtrCreateService;
import com.msb.ibs.corp.cross.exchange.domain.repository.BbTtrHistoryRepository;
import com.msb.ibs.corp.cross.exchange.infrastracture.utils.ResponseUtils;

import lombok.SneakyThrows;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
@Service
public class TtrCreateService implements ITtrCreateService {
	private static final Logger logger = LoggerFactory.getLogger(TtrCreateService.class);

	@Autowired
	private BbTtrHistoryRepository ttrHistoryRepository;

	@Override
	public void validate(TtrOrderRequest request) {
		logger.info("=============== START VALIDATE TTR CREATE ===============");
		long startTime = System.currentTimeMillis();
		if (Objects.isNull(request)) {
			throw new BadRequestException("ERROR.OBJECT.PARAM.INVALID");
		}

		logger.info("Validated successfully!");
		long endTime = System.currentTimeMillis();
		long millis = endTime - startTime;
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		logger.info("Total processing time: {} (millis) => {} (seconds)", millis, seconds);

		logger.info("=============== END VALIDATE TTR CREATE ===============");
	}

	@Override
	@SneakyThrows
	public ResponseEntity<?> process(TtrOrderRequest request) {
		logger.info("=============== START PROCESS TTR CREATE ===============");
		long startTime = System.currentTimeMillis();


		long endTime = System.currentTimeMillis();
		long millis = endTime - startTime;
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
		logger.info("Total processing time: {} (millis) => {} (seconds)", millis, seconds);

		logger.info("=============== END PROCESS TTR CREATE ===============");
		return ResponseUtils.success(true);
	}

	@Override
	public OutputTransferTtrInit init() {

		return null;
	}

	@Override
	public OutputSysParamTtr getSysParameterTtr() {
		return null;
	}

	@Override
	public TransactionConfirmOutput confirmTransaction(TransactionConfirmInput input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public StepOneToConfirmOutput createRateUpdate(InputTtrUpdateRate input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public RateInfo getRateDeal(InputTtrGetRate input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public RateInfo viewDetailTTR(InputTtrTranSn input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public StepOneToConfirmOutput updateTTR(InputTtrUpdate input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public OutputTtrAccountHistory accountHistoryTTR(InputAccountHistoryTtr input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public StepOneToConfirmOutput cancelTTR(InputTtrTranSn input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public OutputTtrValidateFee approveGetFee(InputAuthTransactionTTR input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public OutputTtrValidateFee approveValidateYet(InputAuthTransactionTTR input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public OutputDownloadFile downloadFileTTR(InputDownloadFile input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public TransactionConfirmOutput approveTTRTransactionPost(InputAuthTransaction input, InternalRequest internalRequest) {
		return null;
	}

	@Override
	public TransactionConfirmOutput approveTTRTransactionPut(TransactionConfirmInput input, InternalRequest internalRequest) {
		return null;
	}

}
