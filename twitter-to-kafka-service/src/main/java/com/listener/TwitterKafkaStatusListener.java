package com.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import twitter4j.Status;
import twitter4j.StatusAdapter;
@Component
public class TwitterKafkaStatusListener extends StatusAdapter { //This class extends the StatusAdapter class from the Twitter4J library, which provides default implementations for the StatusListener interface methods.

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStatusListener.class);

    @Override
    public void onStatus(Status status) { //This method is called whenever a new status (tweet) is received from Twitter.
        LOG.info("Twitter status with text: {}", status.getText()); //Logs the text of the tweet using SLF4J.
    }
}
/*
The selected code snippet is an overridden method from the StatusAdapter class provided by the Twitter4J library.
This method is specifically designed to handle incoming Twitter status updates.
In this case, the onStatus method is implemented to log the text of each incoming Twitter status.
The status.getText() method is used to retrieve the content of the tweet, and the LOG.info statement
is used to print this information to the console using the SLF4J logging framework.
This code demonstrates how to create a custom listener for Twitter status updates and process the
incoming data by logging the tweet text. It serves as a basic example of how to integrate Twitter4J with a logging framework in a Java application.
 */