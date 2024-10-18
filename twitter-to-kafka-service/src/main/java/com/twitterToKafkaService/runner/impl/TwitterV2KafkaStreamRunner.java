package com.twitterToKafkaService.runner.impl;

import com.twitterToKafkaService.config.TwitterToKafkaServiceConfigData;
import com.twitterToKafkaService.runner.StreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnExpression("${twitter-to-kafka-service.enable-v2-tweets} && not ${twitter-to-kafka-service.enable-mock-tweets}")
public class TwitterV2KafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterV2KafkaStreamRunner.class);
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData;
    private final TwitterV2StreamHelper twitterV2StreamHelper;

    public TwitterV2KafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, TwitterV2StreamHelper twitterV2StreamHelper) {
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.twitterV2StreamHelper = twitterV2StreamHelper;
    }

    @Override
    public void start() {
      String bearerToken = twitterToKafkaServiceConfigData.getTwitterV2BearerToken();
      if(null != bearerToken){
          try {
              twitterV2StreamHelper.setupRules(bearerToken,getRules());
              twitterV2StreamHelper.connectStream(bearerToken);
          } catch (IOException | URISyntaxException e) {
              LOG.error("Error streaming tweets!",e);
              throw new RuntimeException(e);
          }
      } else {
                LOG.error("There was an error getting the bearer token from the Twitter API. Cannot start stream.");
                throw new RuntimeException("Cannot start stream as bearer token is null");
            }

      }


    private Map<String, String> getRules() { //This method creates a map of rules based on the keywords provided in the configuration.
        List<String> keywords = twitterToKafkaServiceConfigData.getTwitterKeywords(); //Retrieves the keywords from the configuration object.
        Map<String,String>rules =new HashMap<>(); //Creates a new HashMap to store the rules.
        for (String keyword : keywords) { //Iterates over the keywords and creates a rule for each one.
            rules.put(keyword, "keyword: "+keyword); //Adds a rule to the map with the keyword as the key and a value indicating that it's a keyword rule.
        }
        LOG.info("Created filter for twitter stream for keywords: {}",keywords);
        return rules;
    }
}
