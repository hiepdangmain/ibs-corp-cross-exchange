package com.msb.ibs.corp.cross.exchange.application.enums;

import com.msb.ibs.common.base.BaseErrorCode;
import org.springframework.http.HttpStatus;

/**
 * TODO: Class description here.
 *
 * @author QuangBD3
 */
public enum ErrorCode implements BaseErrorCode {
    MS_INTERNAL_SERVER_ERROR("500", "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),
    MS_ERROR("111", "", HttpStatus.UNPROCESSABLE_ENTITY),
    REQUEST_TIMEOUT("999", "Request Timeout", HttpStatus.REQUEST_TIMEOUT),
    BAD_REQUEST("400", "Bad Request", HttpStatus.BAD_REQUEST),
    NUMBER_FORMAT_INVALID("401", "NUMBER_FORMAT_INVALID", HttpStatus.BAD_REQUEST),
    AMOUNT_NOT_EMPTY("402", "AMOUNT_NOT_EMPTY", HttpStatus.BAD_REQUEST),
    AMOUNT_MUST_BIGGER_ZERO("403", "AMOUNT_MUST_BIGGER_ZERO", HttpStatus.BAD_REQUEST),
    AMOUNT_INVALID("404", "AMOUNT_INVALID", HttpStatus.BAD_REQUEST),
    WORKFLOW_NOT_EXIST("405", "WORKFLOW_NOT_EXIST", HttpStatus.UNPROCESSABLE_ENTITY),
    OTP_NOT_EMPTY("406", "OTP_NOT_EMPTY", HttpStatus.UNPROCESSABLE_ENTITY),
    TOKEN_TRANSACTION_NOT_EMPTY("407", "TOKEN_TRANSACTION_NOT_EMPTY", HttpStatus.UNPROCESSABLE_ENTITY),
    TRAN_SN_NOT_EMPTY("408", "TRAN_SN_NOT_EMPTY", HttpStatus.UNPROCESSABLE_ENTITY),
    BB_SAVING_SETTLEMENT_NOT_EXIST("409", "BB_SAVING_SETTLEMENT_NOT_EXIST", HttpStatus.UNPROCESSABLE_ENTITY),
    BB_SAVING_OPEN_NOT_EXIST("410", "BB_SAVING_OPEN_NOT_EXIST", HttpStatus.UNPROCESSABLE_ENTITY),
    MARK_WORKFLOW_ERROR("411", "MARK_WORKFLOW_ERROR", HttpStatus.UNPROCESSABLE_ENTITY),
    AUTHORIZED_TRANSACTION_ERROR("411", "AUTHORIZED_TRANSACTION_ERROR", HttpStatus.UNPROCESSABLE_ENTITY);

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;


    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
