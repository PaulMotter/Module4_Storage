package com.example;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Subscriber implements MqttCallback, Runnable {
    MqttClient client;
    String[] topic;
    Buffer buffer;

    public Subscriber(MqttClient client, String[] topic, Buffer buffer) {
        this.client = client;
        this.topic = topic;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            client.setCallback(this);
            client.connect();
            client.subscribe(topic);

        } catch (Exception e) {
        }
    }
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost: " + throwable.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        String message = new String(mqttMessage.getPayload());
        buffer.add(message);
        // System.out.println(message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    }

}
