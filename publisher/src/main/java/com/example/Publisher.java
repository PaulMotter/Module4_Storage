package com.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class Publisher{
    private MqttClient client;

    // creates the client and connects.
    public Publisher(String broker, String clientId ) throws MqttSecurityException, MqttException{
        client = new MqttClient(broker, clientId);
        client.connect();
        System.out.println("Connected to broker " + broker);
    };

    // Returns true when successful false if not.
    // publishes the message to the specific topic.
    public boolean publish(String topic, String message, int QOS){
        MqttMessage payload = new MqttMessage(message.getBytes());
        payload.setQos(QOS);

        try {
            if (client.isConnected()){
                client.publish(topic, payload);
                return true;
            }
            else {
                client.connect();
                client.publish(topic, payload);
                return true;
            }
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
    };
}