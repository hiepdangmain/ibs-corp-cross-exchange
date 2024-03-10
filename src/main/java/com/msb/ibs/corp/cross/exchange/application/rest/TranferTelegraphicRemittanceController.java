package com.msb.ibs.corp.cross.exchange.application.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InternalRequest;
import com.msb.ibs.common.response.JsonResponseBase;
import com.msb.ibs.common.utils.ResponseUtils;
import com.msb.ibs.core.base.BaseController;
import com.msb.ibs.corp.cross.exchange.domain.integration.input.*;
import com.msb.ibs.corp.main.service.domain.input.ApproveOrRejectTransactionInput;
import com.msb.ibs.corp.main.service.domain.input.TransactionConfirmInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msb.ibs.corp.cross.exchange.application.constants.AppConstant;
import com.msb.ibs.corp.cross.exchange.application.request.TtrOrderRequest;
import com.msb.ibs.corp.cross.exchange.domain.dispatcher.ITtrCreateService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = AppConstant.URI_COMMON, produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Tranfer Telegraphic Remittance Controller")
public class TranferTelegraphicRemittanceController extends BaseController {

	@Autowired
	private ITtrCreateService ttrCreateService;

	@PostMapping(path = "/v1/ttr/create")
	@Operation(summary = "Tao lenh chuyen tien quoc te")
	public Object adsWidgetBanner(@RequestBody TtrOrderRequest input, HttpServletRequest request,
			HttpServletResponse response) {
		return ttrCreateService.execute(input);
	}

	@PostMapping(path = "/v1/ttr/transfer-ttr-init")
	@Operation(summary = "Khoi tao cac tham so chuyen tien quoc te")
	public ResponseEntity<JsonResponseBase<Object>> init() throws LogicException {
		return ResponseUtils.success(ttrCreateService.init());
	}

	@PostMapping(path = "/v1/ttr/get-sys-param-ttr")
	@Operation(summary = "Khoi tao cac tham so ngon ngu chuyen tien quoc te")
	public ResponseEntity<JsonResponseBase<Object>> getSysParameterTtr() throws LogicException {
		return ResponseUtils.success(ttrCreateService.getSysParameterTtr());
	}

	@PostMapping(value = "/v1/ttr/confirm")
	@Operation(summary = "API xác nhận giao dịch (maker)")
	public ResponseEntity<JsonResponseBase<Object>> confirmTransaction(@RequestBody TransactionConfirmInput input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(null, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.confirmTransaction(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/transfer-ttr-update-rate")
	@Operation(summary = "API cập nhật nguồn thanh toán")
	public ResponseEntity<JsonResponseBase<Object>> rateUpdate(@RequestBody InputTtrUpdateRate input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.createRateUpdate(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/ttr-get-rate")
	@Operation(summary = "API truy vấn tỉ giá deal")
	public ResponseEntity<JsonResponseBase<Object>> getRate(@RequestBody InputTtrGetRate input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.getRateDeal(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/view-transaction-ttr")
	@Operation(summary = "API truy vấn thông tin chi tiết lệnh TTR")
	public ResponseEntity<JsonResponseBase<Object>> viewDetailTtr(@RequestBody InputTtrTranSn input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.viewDetailTTR(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/transfer-ttr-update")
	@Operation(summary = "API truy cập nhật thêm hồ sơ TTR or hồ sơ bị reject ")
	public ResponseEntity<JsonResponseBase<Object>> ttrUpate(@RequestBody InputTtrUpdate input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.updateTTR(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/history-ttr/{acctNo}")
	@Operation(summary = "API truy tìm kiếm lịch sử tạo lệnh ")
	public ResponseEntity<JsonResponseBase<Object>> accountHistoryTTR(@RequestBody InputAccountHistoryTtr input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.accountHistoryTTR(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/cancel-ttr")
	@Operation(summary = "API hủy lệnh TTR or TF")
	public ResponseEntity<JsonResponseBase<Object>> cancelTTR(@RequestBody InputTtrTranSn input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.cancelTTR(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/validate-fee-approve-ttr")
	@Operation(summary = "API tính lại fee TTR ở bước duyệt lệnh")
	public ResponseEntity<JsonResponseBase<Object>> approveGetFee(@RequestBody InputAuthTransactionTTR input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.approveGetFee(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/validate-approve-ttr")
	@Operation(summary = "API validate tỉ giá YET nếu có thay đổi ở bước duyệt lệnh")
	public ResponseEntity<JsonResponseBase<Object>> approveValidateYet(@RequestBody InputAuthTransactionTTR input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.approveValidateYet(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/download-ttr-file")
	@Operation(summary = "API download file TTR")
	public ResponseEntity<JsonResponseBase<Object>> downloadFileTTR(@RequestBody InputDownloadFile input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.downloadFileTTR(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/create-approve-ttr-transaction")
	@Operation(summary = "API phê duyệt lệnh TTR")
	public ResponseEntity<JsonResponseBase<Object>> approveTTR(@RequestBody InputAuthTransaction input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.approveTTRTransactionPost(input, internalRequest));
	}

	@PostMapping(value = "/v1/ttr/confirm-approve-ttr-transaction")
	@Operation(summary = "API xác nhận giao dịch (checker)")
	public ResponseEntity<JsonResponseBase<Object>> approveConfirmTTR(@RequestBody TransactionConfirmInput input) throws LogicException {
		InternalRequest internalRequest = new InternalRequest(input, this.getCurrentUserPrincipal(), this.getToken());
		return ResponseUtils.success(ttrCreateService.approveTTRTransactionPut(input, internalRequest));
	}
}
