package com.msb.ibs.corp.cross.exchange.infrastracture.utils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import com.msb.ibs.common.response.ApiResponseData;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.ApiResponseBpm;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import com.msb.ibs.common.response.JsonResponseBase;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
public class ResponseUtils {
	public static ResponseEntity<Object> success() {
		return new ResponseEntity<>(HttpStatus.OK);
	}

	public static ResponseEntity<Object> success(Object data) {
		JsonResponseBase<Object> response = new JsonResponseBase<Object>(data);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static ResponseEntity<Object> success(String lang) {
		String message = "RESULT.SUCCESS";
		String messageToLocale = TranslateUtils.toLocale(message, lang);
		JsonResponseBase<Object> response = new JsonResponseBase<Object>(messageToLocale);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static ResponseEntity<Object> success(HttpServletRequest request) {
		String message = "RESULT.SUCCESS";
		String messageToLocale = TranslateUtils.toLocale(message, request);
		JsonResponseBase<Object> response = new JsonResponseBase<Object>(messageToLocale);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static ResponseEntity<Object> success(Object data, String lang) {
		String message = "RESULT.SUCCESS";
		String messageToLocale = TranslateUtils.toLocale(message, lang);
		JsonResponseBase<Object> response = new JsonResponseBase<Object>(data, messageToLocale);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static ResponseEntity<Object> success(Object data, HttpServletRequest request) {
		String message = "RESULT.SUCCESS";
		String messageToLocale = TranslateUtils.toLocale(message, request);
		JsonResponseBase<Object> response = new JsonResponseBase<Object>(data, messageToLocale);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static ResponseEntity<Object> success(String msg, Object data, String lang) {
		String message = StringUtils.hasText(msg) ? msg : "RESULT.SUCCESS";
		String messageToLocale = TranslateUtils.toLocale(message, lang);
		JsonResponseBase<Object> response = new JsonResponseBase<Object>(data, messageToLocale);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static ResponseEntity<Object> success(String msg, Object data, HttpServletRequest request) {
		String message = StringUtils.hasText(msg) ? msg : "RESULT.SUCCESS";
		String messageToLocale = TranslateUtils.toLocale(message, request);
		JsonResponseBase<Object> response = new JsonResponseBase<Object>(data, messageToLocale);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	public static boolean isSuccess(ApiResponseBpm<Object, Object> responseData) {
		if (responseData != null && HttpStatus.OK.value() == responseData.getHttpStatus()) {
			return true;
		}
		return false;
	}

	public static boolean isCreated(ApiResponseBpm<Object, Object> responseData) {
		if (responseData != null && HttpStatus.CREATED.value() == responseData.getHttpStatus()) {
			return true;
		}
		return false;
	}

	public static boolean isTimeout(ApiResponseBpm<Object, Object> responseData) {
		if (responseData == null || HttpStatus.GATEWAY_TIMEOUT.value() == responseData.getHttpStatus()) {
			return true;
		}
		return false;
	}

}
