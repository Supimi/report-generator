package com.helthyme.report.service;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helthyme.report.Constants;
import com.helthyme.report.domain.QueueMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportQueueService {
    private final AmazonSQS sqs;
    private final ObjectMapper objectMapper;

    public ReportQueueService(ObjectMapper objectMapper) {
        this.sqs = constructSQS();
        this.objectMapper = objectMapper;
    }

    private AmazonSQS constructSQS() {
        return AmazonSQSClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }

    public void sendMessage(QueueMessage queueMessage) {
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(queueMessage);
            SendMessageRequest sendMessageRequest = new SendMessageRequest()
                    .withQueueUrl(Constants.QUEUE_URL)
                    .withMessageBody(jsonString);
            sqs.sendMessage(sendMessageRequest);
            log.info("Sending message {} to sqs {}", jsonString, Constants.QUEUE_URL);
        } catch (Exception e) {
            log.error("Error occurred", e);
        }


    }

}
