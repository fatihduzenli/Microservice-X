package com.microservices.demo.kafka.admin.config.client;

import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.config.KafkaAdminConfig;
import org.apache.kafka.clients.admin.AdminClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaAdminClient {
private static final Logger LOG= LoggerFactory.getLogger(KafkaAdminClient.class);

     private final KafkaAdminConfig kafkaAdminConfig;
     private final AdminClient adminClient;
     private final RetryConfigData retryConfigData;
     private final RetryTemplate retryTemplate;

    public KafkaAdminClient(KafkaAdminConfig kafkaAdminConfig, AdminClient adminClient, RetryConfigData retryConfigData, RetryTemplate retryTemplate) {
        this.kafkaAdminConfig = kafkaAdminConfig;
        this.adminClient = adminClient;
        this.retryConfigData = retryConfigData;
        this.retryTemplate = retryTemplate;
    }
}
