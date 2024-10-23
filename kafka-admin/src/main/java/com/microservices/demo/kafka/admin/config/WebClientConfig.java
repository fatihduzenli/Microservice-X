package com.microservices.demo.kafka.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
        /**
     * Creates a WebClient instance for making HTTP requests.
     *
     * @return a WebClient instance configured with the default settings.
     */
    @Bean
    WebClient webClient() {
        return WebClient.builder().build();
    }
    
}
