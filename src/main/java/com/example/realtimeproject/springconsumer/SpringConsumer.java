package com.example.realtimeproject.springconsumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class SpringConsumer {

    private final KafkaConsumer<String, String> consumer;
    private long maxSubscribers = 0;
    private long maxVideos = 0;
    private String topSubscriberChannel = "";
    private String topVideoChannel = "";

    // ✅ 构造函数
    public SpringConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.22.57.210:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "telegram-bot-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 如果没有记录 offset，从最早开始读

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("telegram-message"));

        System.out.println("✅ Subscribed to topic: telegram-message");
    }

    // ✅ 消费消息的方法
    public void pollMessages() {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

                for (ConsumerRecord<String, String> record : records) {
                    try {
                        String messageValue = record.value().trim();
                        if (!messageValue.startsWith("{")) {
                            System.out.println( messageValue);
                            continue;
                        }

                        JSONObject obj = new JSONObject(messageValue);

                        String formatted = obj.optString("formattedMessage", null);

                        if (formatted != null) {
                            System.out.println("\n============================================================================");
                            System.out.println(formatted);
                            System.out.println("============================================================================\n");

                            if (!record.value().trim().startsWith("{")) {
                                System.out.println(record.value());
                                continue;
                            }
                            String[] lines = formatted.split("\n");
                            String channelName = "";
                            long subscribers = 0;
                            long videos = 0;

                            for (String line : lines) {
                                if (line.startsWith("📺 Channel: ")) {
                                    channelName = line.replace("📺 Channel: ", "").trim();
                                } else if (line.startsWith("👥 Subscribers: ")) {
                                    String count = line.replace("👥 Subscribers: ", "").replace(",", "").trim();
                                    try {
                                        subscribers = Long.parseLong(count);
                                    } catch (NumberFormatException ignored) {}
                                } else if (line.startsWith("📹 Videos: ")) {
                                    String count = line.replace("📹 Videos: ", "").replace(",", "").trim();
                                    try {
                                        videos = Long.parseLong(count);
                                    } catch (NumberFormatException ignored) {}
                                }
                            }

                            if (subscribers > maxSubscribers) {
                                maxSubscribers = subscribers;
                                topSubscriberChannel = channelName;
                            }

                            if (videos > maxVideos) {
                                maxVideos = videos;
                                topVideoChannel = channelName;
                            }

                            // ✅ Always show the top channels (even if unchanged)
                            System.out.println("🏆 Highest Subscribers: " + topSubscriberChannel + " (" + maxSubscribers + ")");
                            System.out.println("🎥 Highest Videos: " + topVideoChannel + " (" + maxVideos + ")");
                        } else {
                            System.out.println("⚠️ Message does not contain 'formattedMessage':");
                            System.out.println(record.value());
                        }

                    } catch (Exception e) {
                        System.err.println("❌ Error parsing message: " + record.value());
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            consumer.close();
        }
    }

    // ✅ 主方法用于测试
    public static void main(String[] args) {
        SpringConsumer botConsumer = new SpringConsumer();
        botConsumer.pollMessages();
    }
}