package com.twitterToKafkaService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "twitter-to-kafka-service")
@Configuration
@Data
@Component
public class TwitterToKafkaServiceConfigData {

    private List<String> twitterKeywords;
    private String welcomeMessage;
    private String twitterV2baseUrl;
    private String twitterV2RulesBaseUrl;
    private String twitterV2BearerToken;


}