package com.example;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class subscriber implements MqttCallback{
    public static void main(String[] args) {
        String broker = "tcp://test.mosquitto.org:1883";
        String topic = "software/5100";
        String clientId = "ASU-subscriber";

        try {
            MqttClient client = new MqttClient(broker, clientId);
            client.setCallback(new subscriber());
            client.connect();
            System.out.println("Connecteed to broker: " + broker);
            client.subscribe(topic);
            System.out.println("Subscribed to topic: " + topic);
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