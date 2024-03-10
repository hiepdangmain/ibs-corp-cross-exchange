package com.msb.ibs.corp.cross.exchange.domain.consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.msb.ibs.common.utils.CommonUtils;
import com.msb.ibs.corp.cross.exchange.application.request.GuaranteeUpdateStatusRequest;
import com.msb.ibs.corp.cross.exchange.domain.service.GuaranteeService.GuaranteeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class GuaranteeUpdateStatusConsumer {

    private static final Logger logger = LoggerFactory.getLogger(GuaranteeUpdateStatusConsumer.class);
    private final GuaranteeService guaranteeService;

    public GuaranteeUpdateStatusConsumer(GuaranteeService guaranteeService) {
        this.guaranteeService = guaranteeService;
    }

    @KafkaListener(topics = "${msb.ibs.corp.kafka.bpm.topic.guarantee}", containerFactory = "bpmKafkaListenerContainerFactory")
    public void onMessage(String message, Acknowledgment acknowledgment) {
        logger.info("Begin Consumer Message Update status guarantee from BPM");
        try {
            logger.info("Message: {}", message);
            GuaranteeUpdateStatusRequest request = CommonUtils.stringToBean(message, new TypeReference<>() {});
            if (request == null) {
                logger.info("Can't convert message to GuaranteeUpdateStatusRequest");
                return;
            }
            if (request.getLang() == null) {
                request.setLang("vi");
            }
            guaranteeService.updateStatusV2(request);
        } catch (Exception e) {
            logger.info("Failed to save message log with exception {}", e.getMessage());
        } finally {
            acknowledgment.acknowledge();
        }
    }

}
