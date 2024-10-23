package com.microservices.demo.kafka.producer.service.impl;

import com.google.common.util.concurrent.ListenableFuture;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Service
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaProducer.class);

    private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> template) {
        this.kafkaTemplate = template;
    }


    /**
     The send method takes three parameters: topicName, key, and message. The topicName parameter specifies the name of the Kafka topic to which the message will be sent. The key parameter is an optional parameter that can be used to assign a key to the message. The message parameter is the actual data that will be sent to the Kafka topic.

     In the selected code, the send method first logs an informational message indicating that a message is being sent to a specific topic. Then, it creates a CompletableFuture object called kafkaResultFuture and uses the kafkaTemplate to send the message to the specified topic. The kafkaTemplate is an instance of the KafkaTemplate class, which is a Spring Kafka component that provides a convenient way to send messages to Kafka topics.

     Finally, the kafkaResultFuture is passed to the whenComplete method, which takes two parameters: a BiConsumer function and a Throwable object. The BiConsumer function is a callback function that will be executed after the message is sent to the Kafka topic. If the send operation is successful, the callback function will log the metadata of the sent message. If the send operation fails, the callback function will log an error message.

     In summary, the selected code is responsible for sending messages to a Kafka topic using a Kafka producer. It logs informational messages, handles send operation results, and provides a callback function to handle the metadata of the sent message or any errors that may occur during the send operation.
     */
    @Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        LOG.info("Sending message='{}' to topic='{}'", message, topicName);
        CompletableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
        kafkaResultFuture.whenComplete(getCallback(topicName, message));
    }


/**
 *The selected code is part of the TwitterKafkaProducer class, which is a Kafka producer implementation for sending Twitter data to a Kafka topic. The @PreDestroy annotation indicates that this method will be called when the Spring application context is being closed, typically when the application is shutting down.

 In this method, the code checks if the kafkaTemplate is not null. If it's not null, it logs an informational message indicating that the Kafka producer is being closed and then calls the destroy() method on the kafkaTemplate. The destroy() method is a method provided by Spring Kafka that is used to clean up resources associated with the Kafka producer.

 This code ensures that the Kafka producer is properly closed and its resources are released when the application is shutting down, preventing memory leaks and other potential issues.
 */

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            LOG.info("Closing kafka producer!");
            kafkaTemplate.destroy();
        }
    }

        /**
     * Creates a callback function that will be executed after sending a message to a Kafka topic.
     * This callback function will log the metadata of the sent message if the send operation is successful,
     * or it will log an error if the send operation fails.
     *
     * @param topicName the name of the Kafka topic to which the message is sent
     * @param message the message being sent to the Kafka topic
     * @return a callback function that will be executed after sending the message
     */
    private BiConsumer<SendResult<Long, TwitterAvroModel>, Throwable> getCallback(String topicName, TwitterAvroModel message) {
        return (result, ex) -> {
            if (ex == null) {
                RecordMetadata metadata = result.getRecordMetadata();
                LOG.info("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp(),
                        System.nanoTime());
            } else {
                LOG.error("Error while sending message {} to topic {}", message.toString(), topicName, ex);
            }
        };
    }
}
