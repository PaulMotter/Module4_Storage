package com.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class publisher{
    public static void main(String[] args) {
        String broker = "tcp://test.mosquitto.org:1883";
        String topic = "software/5100";
        String clientId = "ASU-publisher";

        try{
            MqttClient client = new MqttClient(broker, clientId);
            client.connect();
            System.out.println("Connected to broker " + broker);

            int counter = 0;
            while(true){
                String content = "This is counter = " + counter;
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(2);

                if (client.isConnected()) {
                    client.publish(topic, message);
                    System.out.println("Message Published: " + content);
                } else {
                    System.out.println("Error: message not published.");
                }
                Thread.sleep(5000);
                counter++;
            }
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}