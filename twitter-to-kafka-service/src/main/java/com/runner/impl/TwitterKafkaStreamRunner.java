package com.runner.impl;

import com.config.TwitterToKafkaServiceConfigData;
import com.listener.TwitterKafkaStatusListener;
import com.runner.StreamRunner;
import jakarta.annotation.PreDestroy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.util.Arrays;

@Component
public class TwitterKafkaStreamRunner implements StreamRunner {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class); //This line creates a logger named LOG using Logback or any SLF4J-compatible logging library. This logger will be used to log messages throughout the class.
    private final TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData; //A configuration object that holds the data needed for interacting with the Twitter API (e.g., keywords to filter tweets). It’s marked final, meaning it’s set in the constructor and cannot be changed later.
    private final TwitterKafkaStatusListener twitterKafkaStatusListener;//This is an instance of a listener that will handle the streaming statuses from Twitter and push them to Kafka.
    private TwitterStream twitterStream; //This is a TwitterStream object from the Twitter4J library. It will be used to manage the Twitter stream.


    public TwitterKafkaStreamRunner(TwitterToKafkaServiceConfigData twitterToKafkaServiceConfigData, TwitterKafkaStatusListener twitterKafkaStatusListener) {
        this.twitterToKafkaServiceConfigData = twitterToKafkaServiceConfigData;
        this.twitterKafkaStatusListener = twitterKafkaStatusListener;
    }

    @Override
    public void start() throws TwitterException { //This method is responsible for starting the Twitter stream.
        twitterStream = new TwitterStreamFactory().getInstance(); //Creates an instance of TwitterStream, which is part of the Twitter4J library, used to handle Twitter streaming.
        twitterStream.addListener(twitterKafkaStatusListener); //Adds a listener to the stream. This listener will handle the tweets or events coming from Twitter (in this case, sending them to Kafka).
        addFilter(); //Calls the addFilter() method to add a filter to the Twitter stream based on the keywords provided in the configuration.
    }
    @PreDestroy //this annotation marks the stop() method to be executed just before the Spring bean is destroyed, ensuring that the stream is properly closed when the application shuts down.
    private void stop() { //This method is responsible for stopping the Twitter stream.
        if (twitterStream!= null) {
            LOG.info("Closing twitter stream");
            twitterStream.shutdown();
        }
    }

    private void addFilter() { //This method is responsible for adding a filter to the Twitter stream based on the keywords provided in the configuration.
        String[] keywords = twitterToKafkaServiceConfigData.getTwitterKeywords().toArray(new String[0]);//Retrieves the keywords from the configuration object and converts them into an array of strings.
        FilterQuery filterQuery = new FilterQuery(); //Creates a new FilterQuery object, which will be used to filter the Twitter stream based on certain criteria.
        twitterStream.filter(filterQuery); //Applies the filter to the Twitter stream.
        LOG.info("Started filtering twitter stream for keywords {}",Arrays.toString(keywords)); //Logs a message indicating that the stream has started filtering tweets based on the provided keywords.
    }
}

