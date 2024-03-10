package com.msb.ibs.corp.cross.exchange.domain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.msb.ibs.common.constant.AppConstants;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.response.LoginResponse;
import com.msb.ibs.common.restclient.RestClient;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.enums.BpmErrorCode;
import com.msb.ibs.corp.cross.exchange.application.enums.ErrorCode;
import com.msb.ibs.corp.cross.exchange.application.enums.RedisKey;
import com.msb.ibs.corp.cross.exchange.domain.entity.BkConfigGateway;
import com.msb.ibs.corp.cross.exchange.domain.integration.output.*;
import com.msb.ibs.corp.cross.exchange.domain.repository.BkConfigGatewayRepository;
import com.msb.ibs.corp.cross.exchange.domain.service.Common.CommonService;
import com.msb.ibs.corp.cross.exchange.infrastracture.utils.RedisUtils;
import com.msb.ibs.corp.main.service.domain.service.BaseService;
import com.msb.ibs.logging.aop.LoggingService;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BpmService extends BaseService {

    private final String urlAuth;
    private final String apiKeyAuth;
    private final String urlConvertToken;
    private final String apiKeyConvertToken;
    private final String clientId;
    private final String clientSecret;
    private final String grantType;
    private final CommonService commonService;
    private final BkConfigGatewayRepository configGatewayRepository;
    private final LoggingService loggingService;
    private static final Gson gson = new Gson();

    public BpmService(Environment environment,
                      CommonService commonService,
                      BkConfigGatewayRepository configGatewayRepository,
                      LoggingService loggingService) {
        this.urlAuth = environment.getProperty("bpm.auth.url");
        this.apiKeyAuth = environment.getProperty("bpm.auth.api.key");
        this.clientId = environment.getProperty("bpm.client.id");
        this.clientSecret = environment.getProperty("bpm.client.secret");
        this.grantType = environment.getProperty("bpm.grant.type");
        this.urlConvertToken = environment.getProperty("bpm.convert.token.url");
        this.apiKeyConvertToken = environment.getProperty("bpm.convert.api.key");
        this.commonService = commonService;
        this.configGatewayRepository = configGatewayRepository;
        this.loggingService = loggingService;
    }

    public String getAccessToken() throws LogicException {
        String accessToken = getAccessTokenFromCache(RedisKey.ACCESS_TOKEN_BPM.getKey());
        if (accessToken == null) {
            String token = this.loginKeyCloak();
            if (token == null)
                throw new LogicException("Can not get access token from Keycloak", ErrorCode.MS_INTERNAL_SERVER_ERROR);

            OutputConvertToken response = RestClient.makeGetCall(urlConvertToken, commonService.createHeaders(token, apiKeyConvertToken),
                    new TypeReference<>() {}, 30000);
            if (response != null && response.getData() != null
                    && StringUtil.isNotEmpty(response.getData().getAccessToken())) {
                accessToken = response.getData().getAccessToken();
                RedisUtils.set(RedisKey.ACCESS_TOKEN_BPM.getKey(), accessToken, response.getData().getExpiresIn() - 60);
            } else {
                throw new LogicException("Can not convert access token", ErrorCode.MS_INTERNAL_SERVER_ERROR);
            }
        }
        return accessToken;
    }

    private String loginKeyCloak() throws LogicException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("client_id", clientId);
        parameters.put("client_secret", clientSecret);
        parameters.put("grant_type", grantType);
        Map<String, String> headers = new HashMap<>();
        headers.put(AppConstants.Header.SECRET_KEY_NAME, apiKeyAuth);
        LoginResponse response = RestClient.makePostFormCall(urlAuth, headers, parameters, new TypeReference<>() {}, 30000);
        if (response != null && StringUtil.isNotEmpty(response.getAccessToken())) {
            return response.getAccessToken();
        }
        return null;
    }

    private String getAccessTokenFromCache(String key) {
        try {
            Object obj = RedisUtils.get(key);
            if (obj == null) return null;
            return (String) obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T makePostCall(String gwCode, Object input, TypeReference<T> tTypeReference, String lang,
                              String tranSn, String reqId) throws LogicException {
        T response = makePostCall(gwCode, input, tTypeReference, lang);
        // write log
        loggingService.writeMessageLog(tranSn, reqId, gwCode, input, response);
        return response;
    }

    public <T> T makePutCall(String gwCode, Object input, Map<String, Object> routeParameters, TypeReference<T> tTypeReference, String lang,
                             String tranSn, String reqId) throws LogicException {
        T response = makePutCall(gwCode, input, routeParameters, tTypeReference, lang);
        // write log
        loggingService.writeMessageLog(tranSn, reqId, gwCode, input, response);
        return response;
    }

    public <T> T makePostCall(String gwCode, Object input, TypeReference<T> tTypeReference, String lang) throws LogicException {
        BkConfigGateway bkConfigGateway = configGatewayRepository.findByCode(gwCode);
        String token = this.getAccessToken();
        return makePostCall(bkConfigGateway, input, commonService.createHeaders(token, bkConfigGateway.getToken(), lang), tTypeReference);
    }

    public <T> T makePutCall(String gwCode, Object input, Map<String, Object> routeParameters, TypeReference<T> tTypeReference, String lang) throws LogicException {
        BkConfigGateway bkConfigGateway = configGatewayRepository.findByCode(gwCode);
        String token = this.getAccessToken();
        return makePutCall(bkConfigGateway, input, commonService.createHeaders(token, bkConfigGateway.getToken(), lang),
                routeParameters, tTypeReference);
    }

    public <T> T makePostCall(BkConfigGateway bkConfigGateway, Object input, Map<String, String> headers, TypeReference<T> tTypeReference) throws LogicException {
        return RestClient.makePostCall(bkConfigGateway.getUrl(), input, headers, tTypeReference, Integer.parseInt(bkConfigGateway.getTimeout()));
    }

    public <T> T makePutCall(BkConfigGateway bkConfigGateway, Object input, Map<String, String> headers, Map<String, Object> routeParameters, TypeReference<T> tTypeReference) throws LogicException {
        return RestClient.makePutCall(bkConfigGateway.getUrl(), input, null, routeParameters, headers, tTypeReference, Integer.parseInt(bkConfigGateway.getTimeout()));
    }

    public boolean isCanceled(ApiResponseBpm<Object, Object> response) {
        if (response != null && response.getStatus() != null) {
            OutputStatusBpm outputStatusBpm = parseStatus(response);
            if (outputStatusBpm != null &&
                    BpmErrorCode.IBC425.getCode().equalsIgnoreCase(outputStatusBpm.getCode())) {
                return true;
            }
        }
        return false;
    }

    public boolean isAlreadyExist(ApiResponseBpm<Object, Object> response) {
        if (response != null && response.getStatus() != null) {
            OutputStatusBpm outputStatusBpm = parseStatus(response);
            if (outputStatusBpm != null &&
                    BpmErrorCode.IBC405.getCode().equalsIgnoreCase(outputStatusBpm.getCode())) {
                return true;
            }
        }
        return false;
    }

    public OutputStatusBpm parseStatus(ApiResponseBpm<Object, Object> response) {
        try {
            if (response != null && response.getStatus() != null) {
                return gson.fromJson(gson.toJson(response.getStatus()), OutputStatusBpm.class);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public OutputDataBpm parseData(ApiResponseBpm<Object, Object> response) {
        try {
            if (response != null && response.getData() != null) {
                return gson.fromJson(gson.toJson(response.getData()), OutputDataBpm.class);
            }
        } catch (Exception e) {
        }
        return null;
    }

    public OutputFeeBpm parseDataFee(ApiResponseBpm<Object, Object> response) {
        try {
            if (response != null && response.getData() != null) {
                return gson.fromJson(gson.toJson(response.getData()), OutputFeeBpm.class);
            }
        } catch (Exception e) {
        }
        return null;
    }

}