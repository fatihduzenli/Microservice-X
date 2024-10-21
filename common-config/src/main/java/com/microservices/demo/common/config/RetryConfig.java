package com.microservices.demo.common.config;

import com.microservices.demo.config.RetryConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    private final RetryConfigData retryConfig;

    public RetryConfig(RetryConfigData retryConfig) {
        this.retryConfig = retryConfig;
    }


    @Bean
    public RetryTemplate retryTemplate() { // What is retry policy?

        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setInitialInterval(retryConfig.getInitialIntervalMs());
        exponentialBackOffPolicy.setMaxInterval(retryConfig.getMaxIntervalMs());
        exponentialBackOffPolicy.setMultiplier(retryConfig.getMultiplier());

        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(retryConfig.getMaxAttempts());

        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return retryTemplate;
        /*
        The retryTemplate is used to handle retry logic. It uses an exponential backoff policy and a simple retry policy.
        - The exponential backoff policy is used to increase the interval between retries exponentially, while the simple
         retry policy is used to set the maximum number of retries.
        - The retryTemplate is then used to execute the retry logic.
        - The retryTemplate is returned as a bean.
        -  You can use the retryTemplate in your application to handle retry logic for
        any method that throws an exception.
        - The retryTemplate will automatically retry the method if it throws an exception,
         based on the configuration provided in the RetryConfigData class.
         */
    }
}
