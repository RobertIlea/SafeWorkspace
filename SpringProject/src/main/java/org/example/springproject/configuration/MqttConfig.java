package org.example.springproject.configuration;

import com.google.cloud.Timestamp;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.entity.Alert;
import org.example.springproject.entity.Details;
import org.example.springproject.service.AlertService;
import org.example.springproject.service.RoomService;
import org.example.springproject.service.SensorService;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.*;
import java.util.concurrent.ExecutionException;

@Configuration

public class MqttConfig {
    private final String brokerUrl = "tcp://broker.emqx.io:1883";
    private final String topicDht22= "sensor/dht22/data";
    private final String topicMq5 = "sensor/mq5/data";
    @Autowired
    private final SensorService sensorService;
    private final RoomService roomService;
    private final AlertService alertService;

    public MqttConfig(SensorService sensorService, RoomService roomService, AlertService alertService) {
        this.sensorService = sensorService;
        this.roomService = roomService;
        this.alertService = alertService;
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
                new MqttPahoMessageDrivenChannelAdapter(brokerUrl, "testClient", topicDht22, topicMq5);
        adapter.setCompletionTimeout(8000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(0);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    private void alertDHT22(String roomId, SensorDTO sensorDTO) {
        List<Details> detailsList = sensorDTO.getDetails();
        for(Details details : detailsList) {
            Map<String, Float> data = details.getData();
            if (data.containsKey("temperature")) {
                float temperature = data.get("temperature");
                if (temperature > 30) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Temperature is too high: " + temperature + "°C");
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"temperature is too high: " + temperature + " °C");
                    alertService.saveAlert(alert);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
                if (temperature < 10) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Temperature is too low: " + temperature + "°C");
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"temperature is too low: " + temperature + " °C");
                    alertService.saveAlert(alert);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
            }
            if (data.containsKey("humidity")) {
                float humidity = data.get("humidity");
                if (humidity > 75) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Humidity is too high: " + humidity + "%");
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"humidity is too high: " + humidity + " %");
                    alertService.saveAlert(alert);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
                if (humidity < 30) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Humidity is too low: " + humidity + "%");
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"humidity is too low: " + humidity + " %");
                    alertService.saveAlert(alert);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
            }
        }
    }

    private void alertMq5(String roomId, SensorDTO sensorDTO) {
        List<Details> detailsList = sensorDTO.getDetails();
        for(Details details : detailsList) {
            Map<String, Float> data = details.getData();
            if (data.containsKey("gasLevel")) {
                float gasLevel = data.get("gasLevel");

                if (gasLevel > 700) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Gas level is too high: " + gasLevel);
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Gas level is too high: " + gasLevel);
                    alertService.saveAlert(alert);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
            }
        }
    }

    private void processDataForRoom(SensorDTO sensorDTO, String roomId) {
        System.out.println("Processing sensor data for room: " + roomId);
        System.out.println("Id of the sensor: " + sensorDTO.getId());

        if(sensorDTO.getSensorType().equals("DHT22")){
            alertDHT22(roomId, sensorDTO);
        }
        if(sensorDTO.getSensorType().equals("MQ5")){
            alertMq5(roomId, sensorDTO);
        }
        try{
            sensorService.saveSensorData(sensorDTO);
            roomService.updateRoomWithSensorData(roomId, sensorDTO);
            System.out.println("Room " + roomId + " saved");
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String payload = (String) message.getPayload();
                System.out.println("Processed message: " + payload);
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();

                String primaryRoomId = "1RdkB5aniSqVc1GayEVr";

                if (topic.equals(topicDht22)) {
                    System.out.println("Processing DHT22 topic: " + topic);

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

                    String dhtSensorId ="HGNX6Kp9FiSTSclNwHHN";

                    SensorDTO dht22SensorDTO = new SensorDTO(dhtSensorId,"DHT22",21, List.of(details));
                    System.out.println("Sensor id MQ5: " + dht22SensorDTO.getId());
                    processDataForRoom(dht22SensorDTO,primaryRoomId);
                }

                if(topic.equals(topicMq5)){
                    System.out.println("Processing MQ5 topic: " + topic);

                    Gson gson = new Gson();
                    @SuppressWarnings("unchecked")
                    Map<String,Object> data = gson.fromJson(payload,Map.class);
                    int gasLevel = ((Number) data.get("gasLevel")).intValue();
                    long timestamp = ((Number) data.get("timestamp")).longValue();

                    Map<String,Float> mq5Data = new HashMap<>();
                    mq5Data.put("gas", (float) gasLevel);
                    Details details = new Details(timestamp,mq5Data);

                    String mq5SensorId ="nv0MubTXWBrjHZpQlZxl";

                    SensorDTO mq5SensorDTO = new SensorDTO(mq5SensorId,"MQ5",0, List.of(details));
                    processDataForRoom(mq5SensorDTO,primaryRoomId);
                }

            }
        };
    }

}
