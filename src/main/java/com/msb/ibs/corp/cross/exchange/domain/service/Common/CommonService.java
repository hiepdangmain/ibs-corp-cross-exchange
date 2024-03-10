package com.msb.ibs.corp.cross.exchange.domain.service.Common;

import com.google.common.collect.Lists;
import com.msb.ibs.common.constant.AppConstants;
import com.msb.ibs.common.dto.UserPrincipal;
import com.msb.ibs.common.exception.LogicException;
import com.msb.ibs.common.request.InputSendMailData;
import com.msb.ibs.common.utils.CommonStringUtils;
import com.msb.ibs.common.utils.StringUtil;
import com.msb.ibs.corp.cross.exchange.application.enums.ErrorCode;
import com.msb.ibs.corp.cross.exchange.application.enums.StatusEnum;
import com.msb.ibs.corp.cross.exchange.infrastracture.utils.MapperUtil;
import com.msb.ibs.corp.main.service.domain.dto.AccountInfo;
import com.msb.ibs.corp.main.service.domain.dto.AccountPermissionDto;
import com.msb.ibs.corp.main.service.domain.dto.BaseTxObject;
import com.msb.ibs.corp.main.service.domain.dto.UserInfo;
import com.msb.ibs.corp.main.service.domain.enums.TransactionStatusEnum;
import com.msb.ibs.corp.main.service.domain.input.*;
import com.msb.ibs.corp.main.service.domain.output.GetAccountInfoOutput;
import com.msb.ibs.corp.main.service.domain.output.LimitRemainOutput;
import com.msb.ibs.corp.main.service.domain.output.LimitUserOutput;
import com.msb.ibs.corp.main.service.domain.service.*;
import kong.unirest.ContentType;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class CommonService {
    private static final Logger logger = LoggerFactory.getLogger(CommonService.class);
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private CurrentAccountService currentAccountService;
    @Autowired
    private LimitService limitService;
    @Autowired
    private MailService mailService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AccountPermissionService accountPermissionService;

    public void checkExistWorkflow(Integer corpId, String amount, String workFlowName, String token) throws LogicException {
        WorkflowVerifyInput input = WorkflowVerifyInput.builder()
                .corpId(corpId)
                .processDefinitionKey(workFlowName)
                .amount(amount)
                .status(StatusEnum.ACTIVE.getValue())
                .build();
        boolean isWorkflowExist = workflowService.checkExistWorkflow(input, token);
        if (!isWorkflowExist) {
            throw new LogicException(ErrorCode.WORKFLOW_NOT_EXIST);
        }
    }


    public AuthTransactionCreateInput buildAuthorizedTransactionInput(UserPrincipal userPrincipal, String tranSn, BigDecimal amount,
                                                                      String workflowName, String currencyCode, String classImplStep2) {
        BaseTxObject baseTxObject = BaseTxObject.builder()
                .amount(amount)
                .tranSn(tranSn)
                .securityType(userPrincipal.getSecurityType())
                .workflowName(workflowName)
                .wfProcessId("MAKER TRANSACTION")
                .currencyCode(currencyCode)
                .classImplStep2(classImplStep2).build();
        return AuthTransactionCreateInput.builder()
                .userInfo(MapperUtil.map(userPrincipal, UserInfo.class))
                .baseTxObject(baseTxObject).build();
    }

    public AuthTransactionConfirmInput buildVerifyAuthorizedTransactionInput(TransactionConfirmInput request, UserPrincipal userPrincipal) {
        BaseTxObject baseTxObject = BaseTxObject.builder()
                .tranInfo(request.getTranInfo())
                .tranId(request.getTranId())
                .securityType(userPrincipal.getSecurityType())
                .tokenTransaction(request.getTokenTransaction())
                .build();
        return AuthTransactionConfirmInput.builder()
                .otp(request.getOtp())
                .userInfo(MapperUtil.map(userPrincipal, UserInfo.class))
                .baseTxObject(baseTxObject)
                .build();
    }

    public void validateConfirmInput(TransactionConfirmInput request) throws LogicException {
        if (CommonStringUtils.isNullOrEmpty(request.getOtp())) {
            throw new LogicException(ErrorCode.OTP_NOT_EMPTY);
        }
        if (CommonStringUtils.isNullOrEmpty(request.getTokenTransaction())) {
            throw new LogicException(ErrorCode.TOKEN_TRANSACTION_NOT_EMPTY);
        }
    }

    public void createEmailTransaction(String workflowName, List<String> tranSnList, List<String> notificationTo, List<String> notificationType, String token) throws LogicException {
        TransactionNotifyEmailCreateInput input = TransactionNotifyEmailCreateInput.builder()
                .defName(workflowName)
                .status(TransactionStatusEnum.CREATE_NEW.getValue())
                .tranSnList(tranSnList)
                .notificationTo(notificationTo)
                .notificationType(notificationType)
                .build();
        notificationService.createEmailTransaction(input, token);
    }

    public void updateEmailTransaction(List<String> tranSnList, String token) throws LogicException {
        TransactionNotifyEmailUpdateInput input = TransactionNotifyEmailUpdateInput.builder()
                .tranSnList(tranSnList)
                .status(TransactionStatusEnum.SUCCESS.getValue())
                .build();
        notificationService.updateEmailTransaction(input, token);
    }

    public AccountPermissionDto getAccountPermission(String rolloutAcctNo, Integer corpId, Integer userId,
                                                     Integer roleId, String token) throws LogicException {
        GetAccountPermissionInput input = GetAccountPermissionInput.builder().acctNo(Lists.newArrayList(rolloutAcctNo))
                .corpId(corpId).userId(userId).statusNotInclude(Lists.newArrayList(StatusEnum.DELETE.getValue()))
                .build();
        return accountPermissionService.getAccountPermissionByAccountNo(input, token);
    }

    public AccountInfo getAccountInfo(String acctNo, String token) throws LogicException {
        GetAccountDetailInput input = GetAccountDetailInput.builder()
                .acctNo(Lists.newArrayList(acctNo))
                .build();
        GetAccountInfoOutput account = currentAccountService.getAccountInfo(input, token);
        if (Objects.nonNull(account) && !CollectionUtils.isEmpty(account.getAccountList())) {
            return account.getAccountList().get(0);
        }
        return null;
    }

    public LimitRemainOutput getRemainLimit(String channel, String serviceType, Integer corpId, Integer userId,
                                            BigDecimal tradeAmountCorp, BigDecimal tradeAmountUser) throws LogicException {
        LimitRemainInput input = LimitRemainInput.builder()
                .channel(channel)
                .serviceType(serviceType)
                .corpId(corpId)
                .userId(userId)
                .tradeAmountCorp(tradeAmountCorp)
                .tradeAmountUser(tradeAmountUser)
                .build();
        return limitService.getRemainLimit(input);
    }

    public LimitUserOutput checkLimit(String channel, String serviceType, Integer corpId, Integer userId) throws LogicException {
        LimitUserInput input = LimitUserInput.builder()
                .channel(channel)
                .serviceType(serviceType)
                .corpId(corpId)
                .userId(userId)
                .build();
        return limitService.getCheckLimit(input);
    }

    @Async("threadPoolTaskExecutor")
    public void sendMail(InputSendMailData mailData, String token) {
        try {
            logger.info("Begin Send Mail With Template Code: {}", mailData.getTemplateCode());
            mailService.sendMail(mailData, token);
            logger.info("Send Mail Successfully");
        } catch (Exception e) {
            logger.error("Failed To Send Mail With Template Code: {}", mailData.getTemplateCode());
        }
    }

    public byte[] generatePDFReport(String inputFileName, Map<String, Object> params) {
        byte[] bytes = null;
        JasperReport jasperReport = null;
        try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
            // Check if a compiled report exists
            Resource resource = new ClassPathResource("reports/" + inputFileName + ".jrxml");
            InputStream inputStream = resource.getInputStream();
            if (inputStream != null) {
                jasperReport = JasperCompileManager.compileReport(inputStream);
            } else {
                File file = ResourceUtils.getFile("classpath:reports/" + inputFileName + ".jasper");
                jasperReport = (JasperReport) JRLoader.loadObject(file);
            }
            // jasperReport = (JasperReport) JRLoader.loadObject(file);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params);
            // return the PDF in bytes
            bytes = JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (JRException | IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public Map<String, String> createHeaders(String token) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", ContentType.APPLICATION_JSON.toString());
        header.put("Authorization", AppConstants.tokenPrefix + token);
        header.put(AppConstants.Header.Request_Id, ThreadContext.get(AppConstants.Header.Request_Id));
        return header;
    }

    public Map<String, String> createHeaders(String accessToken, String apiKey, String lang) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", ContentType.APPLICATION_JSON.toString());
        headers.put("Authorization", AppConstants.tokenPrefix + accessToken);
        headers.put(AppConstants.Header.Accept_Language, getLanguage(lang));
        headers.put(AppConstants.Header.Request_Id, ThreadContext.get(AppConstants.Header.Request_Id));
        headers.put(AppConstants.Header.SECRET_KEY_NAME, apiKey);
        return headers;
    }

    public Map<String, String> createHeaders(String accessToken, String apiKey) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", ContentType.APPLICATION_JSON.toString());
        headers.put("Authorization", AppConstants.tokenPrefix + accessToken);
        headers.put(AppConstants.Header.Request_Id, ThreadContext.get(AppConstants.Header.Request_Id));
        headers.put(AppConstants.Header.SECRET_KEY_NAME, apiKey);
        return headers;
    }

    private String getLanguage(String lang) {
        return StringUtil.isEmpty(lang) || "VN".equalsIgnoreCase(lang) ? "vi" : "en";
    }

    public String getToken() {
        String bearerToken = request.getHeader("Authorization");
        return StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : null;
    }
}
