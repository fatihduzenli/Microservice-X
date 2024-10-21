package com.microservices.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "retry-config")
public class RetryConfigData {
    protected Integer maxAttempts;
    protected Double multiplier;
    protected Long maxIntervalMs;
    protected Long initialIntervalMs;
    protected Long sleepTimeMs;
}
