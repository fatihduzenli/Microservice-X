package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfigData {

    private String keySerializerClass;
    private String valueSerializerClass;
    private String acks;
    private Integer retryCount;
    private Integer batchSize;
    private Integer lingerMs;
    private String compressionType;
    private Integer batchSizeBoostFactor;
    private Integer requestTimeoutMs;


}