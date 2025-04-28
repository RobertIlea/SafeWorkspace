package org.example.springproject.configuration;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Details;
import org.example.springproject.entity.Sensor;
import org.example.springproject.service.RoomService;
import org.example.springproject.service.SensorService;
import org.example.springproject.util.SensorType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Configuration

public class MqttConfig {
    private final String brokerUrl = "tcp://broker.emqx.io:1883";
    private final String topicDht22= "sensor/dht22/data";
    private final SensorService sensorService;
    private final RoomService roomService;

    public MqttConfig(SensorService sensorService, RoomService roomService) {
        this.sensorService = sensorService;
        this.roomService = roomService;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }
    @Bean
    public MqttClient mqttClient() throws Exception {
        MqttClient client = new MqttClient(brokerUrl, "Robicu03");
        client.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                System.out.println("Message received on topic " + topic + ": " + new String(message.getPayload()));
            }

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("MQTT connection lost: " + cause.getMessage());
            }

            @Override
            public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                try {
                    System.out.println("Message delivery complete: " + token.getMessage());
                } catch (MqttException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        client.connect();
        client.subscribe(topicDht22);
        return client;
    }
    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(brokerUrl, "testClient", topicDht22);
        adapter.setCompletionTimeout(8000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(0);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    private void processSensorDataForRoom1(SensorDTO sensorDTO) {
        // Process the sensor data for room 1
        System.out.println("Processing sensor data for room 1: " + sensorDTO.toString());
        System.out.println("Id of the sensor: " + sensorDTO.getId());

        String roomId = "5Ejp4XJFAS7XwkZV7Xg7";
        try{
            sensorService.saveSensorData(sensorDTO);
            roomService.updateRoomWithSensorData(roomId, sensorDTO);
            System.out.println("Room 1 saved");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(SensorService sensorService) {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String payload = (String) message.getPayload();
                System.out.println("Processed message: " + payload);
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
                if (topic.equals(topicDht22)) {
                    System.out.println("Temperature received: " + payload);
                }
                // Parse JSON
                Gson gson = new Gson();
                @SuppressWarnings("unchecked")
                Map<String, Object> data = gson.fromJson(payload,Map.class);
                float temperature = ((Number) data.get("temperature")).floatValue();
                float humidity = ((Number) data.get("humidity")).floatValue();
                long timestamp = ((Number) data.get("timestamp")).longValue();


                Map<String,Float> sensorData = new HashMap<>();
                sensorData.put("temperature",temperature);
                sensorData.put("humidity",humidity);
                Details details = new Details(timestamp,sensorData);

                // DHT22 SENSOR from room 1
                String sensorId = "DQZbnMngXpqwP67hMVUx";

                SensorDTO sensorDTO = new SensorDTO(sensorId,"DHT22",21, List.of(details));
                System.out.println("Sensor id: " + sensorDTO.getId());
                processSensorDataForRoom1(sensorDTO);

            }
        };
    }

}
