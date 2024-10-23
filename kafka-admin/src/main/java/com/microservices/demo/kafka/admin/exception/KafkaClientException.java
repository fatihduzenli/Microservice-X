package com.microservices.demo.kafka.admin.exception;

/**
 * Custom exception class for Kafka client related exceptions.
 * Extends RuntimeException to allow unchecked exceptions.
 */
public class KafkaClientException extends RuntimeException {

    /**
     * Default constructor.
     * Constructs a new KafkaClientException with no detail message.
     */
    public KafkaClientException() {
    }

    /**
     * Constructs a new KafkaClientException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     */
    public KafkaClientException(String message) {
        super(message);
    }

    /**
     * Constructs a new KafkaClientException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method)
     * @param cause the cause (which is saved for later retrieval by the {@link #getCause()} method)
     */
    public KafkaClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
