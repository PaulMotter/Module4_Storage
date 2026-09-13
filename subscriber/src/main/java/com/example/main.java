package com.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class main implements MqttCallback{
    public static void main(String[] args) {
        String broker = "tcp://test.mosquitto.org:1883";
        String[] topic = {"software/5100/Sensor1", "software/5100/Sensor2","software/5100/Sensor3"};
        String clientId = "ASU-subscriber";

        try {
            MqttClient client = new MqttClient(broker, clientId);
            Buffer b = new Buffer();
            Subscriber sub = new Subscriber(client, topic, b);
            Thread t = new Thread(sub);
            Thread t2 = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        List<String> messages = b.clean();
                        String content = messages.stream().map( s -> s + ",\n").collect(Collectors.joining());
                        Files.writeString(Path.of("output.txt"), content, StandardOpenOption.APPEND);
                    } catch (Exception e) {
                    }
                }
            });
            t.start();
            t2.start();
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection Lost: " + throwable.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        System.out.println("Message arrived. Topic: " + topic + " Message: " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }
}
