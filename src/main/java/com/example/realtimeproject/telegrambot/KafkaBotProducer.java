package com.example.realtimeproject.telegrambot;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaBotProducer {
    private final KafkaProducer<String, String> producer;

    public KafkaBotProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    // Example main method to test sending a message
    public static void main(String[] args) {
        KafkaBotProducer producer = new KafkaBotProducer();
        try {
            producer.sendMessage("Hello from com.example.realtimeproject.telegrambot.KafkaBotProducer!");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            producer.close();
        }
    }

    public void sendMessage(String message) throws ExecutionException, InterruptedException {
        producer.send(new ProducerRecord<>("telegram-messages", message), new Callback() {
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