package io.quarkus.it.kafka;

import java.io.File;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.quarkus.it.kafka.ssl.CertificateFormat;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import me.escoffier.certs.Format;
import me.escoffier.certs.junit5.Certificate;
import me.escoffier.certs.junit5.Certificates;

@Certificates(certificates = {
        @Certificate(name = "kafka", formats = { Format.PKCS12, Format.JKS,
                Format.PEM }, alias = "kafka-test-store", password = "Z_pkTh9xgZovK4t34cGB2o6afT4zZg0L")
}, baseDir = "target/certs")
@QuarkusTest
@QuarkusTestResource(KafkaSSLTestResource.class)
public class SslKafkaConsumerTest {

    public static Producer<Integer, String> createProducer(CertificateFormat format) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, System.getProperty("bootstrap.servers"));
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "test-ssl-producer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        String truststore = switch (format) {
            case PKCS12 -> "kafka-truststore.p12";
            case JKS -> "kafka-truststore.jks";
            case PEM -> "kafka-ca.crt";
        };

        File tsFile = new File("target/certs/" + truststore);
        props.setProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, tsFile.getPath());
        if (format != CertificateFormat.PEM) {
            props.setProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "Z_pkTh9xgZovK4t34cGB2o6afT4zZg0L");
        }
        props.setProperty(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, format.name());
        props.setProperty(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");

        return new KafkaProducer<>(props);
    }

    @ParameterizedTest
    @CsvSource({
            "PKCS12",
            "JKS",
            "PEM"
    })
    public void testReception(String format) {
        try (Producer<Integer, String> producer = createProducer(CertificateFormat.valueOf(format))) {
            producer.send(new ProducerRecord<>("test-ssl-consumer", 1, "hi world"));
            String string = RestAssured
                    .given().queryParam("format", format)
                    .when().get("/ssl")
                    .andReturn().asString();
            Assertions.assertEquals("hi world", string);
        }
    }

}
