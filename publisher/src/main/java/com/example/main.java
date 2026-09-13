package com.example;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.paho.client.mqttv3.MqttException;

class main {
    public static void main(String[] args) throws InterruptedException {

        // Input Parameters
        int QOS = 0;
        double iterationsPerSecond = 500.0;

        // Nanoseconds between iterations
        long intervalNano = (long) (1_000_000_000.0 / iterationsPerSecond);

        String broker = "tcp://test.mosquitto.org:1883";
        String clientId = "ASU-publisher";

        ArrayList<String> topicList = new ArrayList<>(
            Arrays.asList(
                "software/5100/Sensor1",
                "software/5100/Sensor2",
                "software/5100/Sensor3"
            )
        );

        ArrayList<Integer> counterList = new ArrayList<>(
            Arrays.asList(0, 0, 0)
        );
        long counter = 0;

        assert topicList.size() == counterList.size();

        Publisher pub = null;

        try {
            pub = new Publisher(broker, clientId);
        } catch (MqttException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while (true) {
            for (int i = 0; i < topicList.size(); i++) {
                long iterationStart = System.nanoTime();

                // String message = topicList.get(i) + ": " + counterList.get(i);
                String message = topicList.get(i) + ": " + counter;
                System.out.println("-----\n" + message);
                boolean success = pub.publish(topicList.get(i), message, QOS);
                if (success) {
                    // counterList.set(i, counterList.get(i) + 1);
                    ++counter;
                }

                long iterationEnd = System.nanoTime();
                long elapsedNano = iterationEnd - iterationStart;
                endIteration(intervalNano, elapsedNano);
            }
        }
    }

    public static void endIteration(long intervalGoal, long intervalActual) throws InterruptedException{
        if (intervalActual > intervalGoal){
            // Don't wait 
            System.out.printf("Rate: %.2f\n", 1.0e9f/(float)intervalActual);
        }
        else {
            long waitTime = intervalGoal-intervalActual;
            Thread.sleep((waitTime) / 1_000_000); //in milliseconds
            System.out.printf("Rate: %.2f/s (Maximum)\n", 1.0e9f/(float)intervalGoal);
        }
    }
}
