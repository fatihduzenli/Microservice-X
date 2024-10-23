package com.microservices.demo.kafka.admin.client;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.config.KafkaAdminConfig;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class KafkaAdminClient {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final KafkaAdminConfig kafkaAdminConfig;
    private final AdminClient adminClient;
    private final RetryConfigData retryConfigData;
    private final RetryTemplate retryTemplate;
    private final KafkaConfigData kafkaConfigData;
    private final WebClient webClient;

    public KafkaAdminClient(KafkaAdminConfig kafkaAdminConfig, AdminClient adminClient, RetryConfigData retryConfigData, RetryTemplate retryTemplate, KafkaConfigData kafkaConfigData, WebClient webClient) {
        this.kafkaAdminConfig = kafkaAdminConfig;
        this.adminClient = adminClient;
        this.retryConfigData = retryConfigData;
        this.retryTemplate = retryTemplate;
        this.kafkaConfigData = kafkaConfigData;
        this.webClient = webClient;
    }

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(this::doCreateTopics); //The retryTemplate object's execute method is called with the doCreateTopics method as an argument. This method creates the Kafka topics on the Kafka cluster.
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max number of retry for creating kafka topic(s)!"); //If the maximum number of retries is reached, a KafkaClientException is thrown.
        }
        checkTopicsCreated(); //The checkTopicsCreated method is called to verify that the topics were created successfully.
    }

    private CreateTopicsResult doCreateTopics(RetryContext retryContext) {

        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate(); //The method begins by retrieving the list of topic names that need to be created from the kafkaConfigData object.
        LOG.info("Creating {} topics(s), attempt {}", topicNames.size(), retryContext.getRetryCount()); //A log message is printed to the console, indicating the number of topics being created and the current retry attempt.
        List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic( //The kafkaConfigData object is used to retrieve the number of partitions and replication factor for the topics.
                topic.trim(), //The trim method is used to remove any leading or trailing whitespace from the topic name.
                kafkaConfigData.getNumOfPartitions(), //The kafkaConfigData object is used to retrieve the number of partitions and replication factor for the topics.
                kafkaConfigData.getReplicationFactor())).toList(); //The kafkaConfigData object is used to retrieve the number of partitions and replication factor for the topics.
        return adminClient.createTopics(kafkaTopics); //The adminClient object's createTopics method is called with the list of NewTopic objects as an argument. This method creates the specified Kafka topics on the Kafka cluster.

    }

    public void checkSchemaRegistry() {
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
    }

    private HttpStatusCode getSchemaRegistryStatus() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchangeToMono(response -> {
                        if (response.statusCode().is2xxSuccessful()) {
                            return Mono.just(response.statusCode());
                        } else {
                            return Mono.just(HttpStatus.SERVICE_UNAVAILABLE);
                        }
                    }).block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }
    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();
        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void checkMaxRetry(int i, Integer maxRetry) {
         if(i>maxRetry) {
             throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!");
         }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topic) {
        if(topics == null) {
            return false;
        }
        return topics.stream().anyMatch(topicListing -> topicListing.name().equals(topic));

    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(this::doGetTopics);
        } catch (RuntimeException e) {
            throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!");
        }
        return topics;
    }

    private Collection<TopicListing> doGetTopics(RetryContext retryContext) {

        Collection<TopicListing> topics;
        try {
            topics = retryTemplate.execute(this::doGetTopics);
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max number of retry for reading kafka topic(s)!", t);
        }
        return topics;
    }
}
/*
1. createTopics():

Bu yöntem, Kafka üzerinde yeni konular (topics) oluşturur. Yöntem şu adımları takip eder:

	•	retryTemplate.execute() kullanılarak, doCreateTopics() metodu yeniden denemelerle çalıştırılır.
	•	Eğer bir hata olursa ve maksimum deneme sayısına ulaşıldıysa, bir KafkaClientException fırlatılır.
	•	Konuların başarıyla oluşturulup oluşturulmadığını kontrol etmek için checkTopicsCreated() çağrılır.

Mantık: Bu yöntem, Kafka’da yeni konular oluşturmak için bir deneme mekanizmasıyla çalışır. Başarısız olursa belirlenen deneme sayısına kadar tekrar dener.

2. doCreateTopics(RetryContext retryContext):

Bu özel bir yardımcı yöntemdir ve yeni konuları oluşturmak için asıl iş burada yapılır.

	•	İlk olarak, Kafka yapılandırma verilerinden (kafkaConfigData) oluşturulması gereken konu isimlerini alır.
	•	Her bir konu için NewTopic nesneleri oluşturulur, bu nesneler konu ismi, bölüm sayısı (partitions) ve çoğaltma faktörü (replicationFactor) içerir.
	•	Son olarak, adminClient.createTopics() ile Kafka’ya bu konuların oluşturulması için bir istek yapılır.

Mantık: Belirtilen konuları Kafka üzerinde oluşturur ve retryContext ile deneme sayısını takip eder.

3. checkSchemaRegistry():

Bu yöntem, Kafka’nın Şema Kayıt Defteri’nin (Schema Registry) durumunu kontrol eder.

	•	İlk olarak bir döngü başlar ve Şema Kayıt Defteri durumu kontrol edilir (getSchemaRegistryStatus()).
	•	Eğer HTTP durumu 2xx (başarılı) değilse, deneme sayısı kontrol edilir ve eğer denemeler tükendiyse bir hata fırlatılır.
	•	Başarısız denemelerde bekleme süresi her seferinde bir çarpan ile artırılır (sleepTimeMs *= multiplier).

Mantık: Şema Kayıt Defteri’nin çalışır durumda olup olmadığını kontrol eder, başarılı olana kadar yeniden denemeler yapar.

4. getSchemaRegistryStatus():

Bu yöntem, Şema Kayıt Defteri’nin HTTP durumunu kontrol eder.

	•	Bir HTTP GET isteği yapılır (webClient.method(HttpMethod.GET)), bu istek belirtilen URL’deki Şema Kayıt Defteri’ne yapılır.
	•	Eğer istek başarılıysa, HTTP durumu (HttpStatus) 2xx olarak döner, aksi halde SERVICE_UNAVAILABLE döndürülür.

Mantık: Şema Kayıt Defteri’ne HTTP isteği yapar ve durumun başarılı olup olmadığını kontrol eder.

5. checkTopicsCreated():

Bu yöntem, belirli Kafka konularının başarıyla oluşturulup oluşturulmadığını kontrol eder.

	•	İlk olarak mevcut konular alınır (getTopics()).
	•	Sonra her bir konu için, bu konunun oluşturulup oluşturulmadığı kontrol edilir (isTopicCreated()).
	•	Eğer bir konu henüz oluşturulmamışsa, deneme sayısı kontrol edilir, beklenir ve tekrar denemeye geçilir.

Mantık: Her bir konunun Kafka’da oluşturulup oluşturulmadığını kontrol eder ve eksikse yeniden dener.

6. isTopicCreated(Collection<TopicListing> topics, String topic):

Bu yöntem, verilen konu isminin Kafka’da mevcut olup olmadığını kontrol eder.

	•	Eğer mevcut konular listesi null ise, konu oluşturulmamış demektir.
	•	Konular arasında belirtilen isimde bir konu olup olmadığını kontrol eder.

Mantık: Belirtilen konu isminin mevcut Kafka konuları arasında olup olmadığını doğrular.

7. getTopics():

Bu yöntem, Kafka’daki mevcut konuları döner.

	•	Yine bir deneme mekanizması kullanılarak doGetTopics() çağrılır ve sonuç alınır.

Mantık: Kafka’dan mevcut konuların listesini almak için birden fazla deneme yaparak çalışır.

8. doGetTopics(RetryContext retryContext):

Bu yöntem, getTopics() yöntemi gibi çalışır, ancak bir deneme bağlamı (RetryContext) ile çalışır ve aslında tekrar eden bir yardımcı işlevdir.

Mantık: Kafka’dan konuların listesini almak için asıl işi yapar, hata olursa maksimum denemeye kadar tekrar dener.

9. sleep(Long sleepTimeMs):

Bu basit yöntem, belirtilen süre boyunca iş parçacığını uyutur (Thread.sleep).

	•	Eğer bir kesinti (interruption) olursa, iş parçacığı yeniden başlatılır (Thread.currentThread().interrupt()).

Mantık: Yöntem, denemeler arası belirli bir süre beklemeyi sağlar.

10. checkMaxRetry(int i, Integer maxRetry):

Bu yöntem, deneme sayısının belirlenen maksimum değeri aşıp aşmadığını kontrol eder.

	•	Eğer deneme sayısı maxRetry değerini geçerse, bir KafkaClientException fırlatılır.

Mantık: Belirtilen maksimum deneme sayısını aşmadığımızı kontrol eder.

Bu yapı, Kafka konularının ve Şema Kayıt Defteri’nin doğru bir şekilde çalışmasını sağlamak için yeniden deneme mekanizmalarıyla donatılmış bir Kafka istemci uygulamasıdır.


 */