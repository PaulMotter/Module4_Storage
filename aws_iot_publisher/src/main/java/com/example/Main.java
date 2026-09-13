package com.example;

import java.util.concurrent.CountDownLatch;
import java.nio.charset.StandardCharsets;

import software.amazon.awssdk.crt.mqtt5.Mqtt5Client;
import software.amazon.awssdk.crt.mqtt5.Mqtt5ClientOptions;
import software.amazon.awssdk.crt.mqtt5.OnAttemptingConnectReturn;
import software.amazon.awssdk.crt.mqtt5.OnConnectionFailureReturn;
import software.amazon.awssdk.crt.mqtt5.OnConnectionSuccessReturn;
import software.amazon.awssdk.crt.mqtt5.OnDisconnectionReturn;
import software.amazon.awssdk.crt.mqtt5.OnStoppedReturn;
import software.amazon.awssdk.crt.mqtt5.QOS;
import software.amazon.awssdk.crt.mqtt5.packets.ConnectPacket;
import software.amazon.awssdk.crt.mqtt5.packets.PublishPacket;
import software.amazon.awssdk.iot.AwsIotMqtt5ClientBuilder;

public class Main {
    static final String endpoint = "a40203d8x0q5g-ats.iot.us-west-2.amazonaws.com";
    static final String topic = "5100/checkin";
    static final String cert = "device.pem.crt";
    static final String key = "private.pem.key";
    static final String ca = "AmazonRootCA1.pem";
    static final String clientId = "student-" + System.currentTimeMillis();
    static final String payload = """
    {
        \"id\":\"pmotter@calpoly.edu\",
        \"name\":\"Paul Motter\"
    }""";
            
    public static void main(String[] args) {
        // signals connection is done
        CountDownLatch connected = new CountDownLatch(1);

        // manages the lifecycle of the client. 
        // defines actions/functions for special events. 
        Mqtt5ClientOptions.LifecycleEvents lifecycleEvents =
            new Mqtt5ClientOptions.LifecycleEvents() {
        
                @Override
                public void onAttemptingConnect(Mqtt5Client client, OnAttemptingConnectReturn data) {
                    System.out.println("Client attempting to connect.");
                }
        
                @Override
                public void onConnectionSuccess(Mqtt5Client client, OnConnectionSuccessReturn data) {
                    System.out.println("Client Connected.");
                    connected.countDown();
                }
        
                @Override
                public void onConnectionFailure( Mqtt5Client client, OnConnectionFailureReturn data) {
                    System.out.println("Client connection failed: " + data);
                    connected.countDown();
                }
        
                @Override
                public void onDisconnection( Mqtt5Client client, OnDisconnectionReturn data) {
                    System.out.println("Client disconnected: " + data);
                }
        
                @Override
                public void onStopped(Mqtt5Client client, OnStoppedReturn data) {
                    System.out.println("Client stopped");
                }
            };
        
        AwsIotMqtt5ClientBuilder builder =
            AwsIotMqtt5ClientBuilder.newDirectMqttBuilderWithMtlsFromPath(endpoint,cert,key);
        
        builder.withCertificateAuthorityFromPath(null, ca);
        
        ConnectPacket.ConnectPacketBuilder connect =
            new ConnectPacket.ConnectPacketBuilder().withClientId(clientId);
        
        builder.withConnectProperties(connect);
        builder.withLifeCycleEvents(lifecycleEvents); //monitor connection lifecycle
        
        Mqtt5Client client = builder.build();
        
        try {
            client.start();
            
            // wait until client is ready.
            connected.await();
            
            // build and send message.
            PublishPacket message =
                new PublishPacket.PublishPacketBuilder(topic, QOS.AT_LEAST_ONCE, payload.getBytes(StandardCharsets.UTF_8))
                .build();
            client.publish(message).get();
            
            System.out.println("Message sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.stop();
            client.close();
        }
    }
}