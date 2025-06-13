package com.example.realtimeproject.springproducer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SpringProducer {
    private final KafkaProducer<String, String> producer;

    public SpringProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "172.22.57.210:9092");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    // Example main method to test sending a message
    public static void main(String[] args) {
        SpringProducer producer = new SpringProducer();
        try {
            producer.sendMessage("Hello from com.example.realtimeproject.telegrambot.KafkaBotProducer!");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            producer.close();
        }
    }

    public void sendMessage(String message) throws ExecutionException, InterruptedException {
        producer.send(new ProducerRecord<>("telegram-message", message), new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    System.out.println("Message sent to topic " + metadata.topic() +
                            " partition " + metadata.partition() +
                            " offset " + metadata.offset());
                }
            }
        }).get();
    }

    public void close() {
        producer.close();
    }
}
