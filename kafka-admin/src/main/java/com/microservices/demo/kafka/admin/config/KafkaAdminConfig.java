package com.microservices.demo.kafka.admin.config;

import com.microservices.demo.config.KafkaConfigData;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import java.util.Map;

@Configuration
@EnableRetry
public class KafkaAdminConfig {
    private final KafkaConfigData kafkaConfigData;

    public KafkaAdminConfig(KafkaConfigData kafkaConfigData) {
        this.kafkaConfigData = kafkaConfigData;
    }

      /**
     * Creates an AdminClient bean that is used to interact with the Kafka cluster.
     *
     * @return the created AdminClient bean, which can be used to interact with the Kafka cluster.
     */
    public AdminClient adminClient() {
        return AdminClient.create(Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
                kafkaConfigData.getBootstrapServers()));
    }
/*
The KafkaAdminConfig class is used to create an AdminClient bean that is used to interact with the Kafka cluster.
- The AdminClient bean is created using the AdminClient.create() method, which takes a map of properties as an argument.
- The properties are passed to the AdminClient.create() method using the Map.of() method.
- The properties are read from the KafkaConfigData class, which is injected into the KafkaAdminConfig class using constructor injection.
- The KafkaConfigData class is a configuration class that contains the Kafka configuration properties.
- The adminClient() method returns the created AdminClient bean, which can be used to interact with the Kafka cluster.

 */

}
