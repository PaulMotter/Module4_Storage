import org.eclipse.paho.client.mqttv3.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Subscriber implements MqttCallback {
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    MqttClient client;

    public Subscriber(String broker) {
        try {
            String clientId = MqttClient.generateClientId();
            client = new MqttClient(broker, clientId);
            client.setCallback(this);
            client.connect();
            System.out.println("Connected to BROKER: " + broker);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void addTopic(String topic) throws MqttException {
        client.subscribe(topic);
    }
    public void removeTopic(String topic) throws MqttException {
        client.unsubscribe(topic);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection lost: " + throwable.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        String message = new String(mqttMessage.getPayload());
        pcs.firePropertyChange("message",topic, message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

}