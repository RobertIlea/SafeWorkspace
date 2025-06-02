package org.example.springproject.configuration;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.springproject.dto.SensorDTO;
import org.example.springproject.dto.UserDTO;
import org.example.springproject.entity.*;
import org.example.springproject.service.*;
import org.example.springproject.util.AlertManager;
import org.example.springproject.util.SensorMapper;
import org.example.springproject.util.UserMapper;
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
    private final String topicEsp32x1 = "sensor/esp32x1/data";
    private final String topicEsp32x2 = "sensor/esp32x2/data";

    @Autowired
    private final SensorService sensorService;
    private final RoomService roomService;
    private final AlertService alertService;
    private final CustomAlertService customAlertService;
    private final AlertManager alertManager;
    private final UserService userService;

    public MqttConfig(SensorService sensorService, RoomService roomService, AlertService alertService, CustomAlertService customAlertService, AlertManager alertManager, UserService userService) {
        this.sensorService = sensorService;
        this.roomService = roomService;
        this.alertService = alertService;
        this.customAlertService = customAlertService;
        this.alertManager = alertManager;
        this.userService = userService;
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
                new MqttPahoMessageDrivenChannelAdapter(brokerUrl, "testClient", topicDht22, topicMq5,topicEsp32x1,topicEsp32x2);
        adapter.setCompletionTimeout(8000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(0);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    private void alertDHT22(String roomId, SensorDTO sensorDTO) {
        List<Details> detailsList = sensorDTO.getDetails();
        UserDTO userDTO = userService.getUserByRoomId(roomId);
        User userFromDTO = UserMapper.toEntity(userDTO);
        Sensor sensorFromDTO = SensorMapper.toEntity(sensorDTO);

        for(Details details : detailsList) {
            Map<String, Float> data = details.getData();
            if (data.containsKey("temperature")) {
                float temperature = data.get("temperature");
                if (temperature > 40) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Temperature is too high: " + temperature + "°C");
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Temperature is too high: " + temperature + " °C");
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
                if (temperature < 0) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Temperature is too low: " + temperature + "°C");
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Temperature is too low: " + temperature + " °C");
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
            }
            if (data.containsKey("humidity")) {
                float humidity = data.get("humidity");
                if (humidity > 75) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Humidity is too high: " + humidity + "%");
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Humidity is too high: " + humidity + " %");
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
                if (humidity < 30) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Humidity is too low: " + humidity + "%");
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Humidity is too low: " + humidity + " %");
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
            }
        }
    }

    private void alertMq5(String roomId, SensorDTO sensorDTO) {
        List<Details> detailsList = sensorDTO.getDetails();
        UserDTO userDTO = userService.getUserByRoomId(roomId);
        User userFromDTO = UserMapper.toEntity(userDTO);
        Sensor sensorFromDTO = SensorMapper.toEntity(sensorDTO);

        for(Details details : detailsList) {
            Map<String, Float> data = details.getData();
            if (data.containsKey("gas")) {
                float gasLevel = data.get("gas");

                if (gasLevel > 700) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Gas level is too high: " + gasLevel);
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Gas level is too high: " + gasLevel);
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
            }
        }
    }

    private void alertMq2(String roomId, SensorDTO sensorDTO) {
        List<Details> detailsList = sensorDTO.getDetails();
        UserDTO userDTO = userService.getUserByRoomId(roomId);
        User userFromDTO = UserMapper.toEntity(userDTO);
        Sensor sensorFromDTO = SensorMapper.toEntity(sensorDTO);

        for(Details details : detailsList) {
            Map<String, Float> data = details.getData();
            if (data.containsKey("gas")) {
                float gasLevel = data.get("gas");

                if(gasLevel > 1000){
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Gas level is too high: " + gasLevel);
                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Smoke or gas level is too high: " + gasLevel);
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }

            }
        }
    }

    private void checkCustomAlerts(String roomId, SensorDTO sensorDTO) {
        List<CustomAlert> customAlerts = customAlertService.getAllCustomAlertsBySensorId(sensorDTO.getId());
        UserDTO userDTO = userService.getUserByRoomId(roomId);
        User userFromDTO = UserMapper.toEntity(userDTO);
        Sensor sensorFromDTO = SensorMapper.toEntity(sensorDTO);

        for(Details detail: sensorDTO.getDetails()) {
            Map<String, Float> data = detail.getData();
            for (CustomAlert customAlert : customAlerts) {
                Float value = data.get(customAlert.getParameter());
                if (value != null) {
                    if (evaluateCondition(value, customAlert.getCondition(), customAlert.getThreshold())) {
                        System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                        System.out.println("Custom alert triggered: " + customAlert.getMessage());
                        Alert alert = new Alert(roomId,sensorDTO.getId(),detail.getTimestamp(),sensorDTO.getSensorType(),data,customAlert.getMessage());
                        alertService.saveAlert(alert);
                        alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                        System.out.println("Custom alert saved");
                        System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                    }
                }
            }
        }
    }

    private boolean evaluateCondition(float value, String condition, float threshold) {
        return switch (condition) {
            case ">" -> value > threshold;
            case "<" -> value < threshold;
            case ">=" -> value >= threshold;
            case "<=" -> value <= threshold;
            case "==" -> value == threshold;
            default -> false;
        };
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
        if(sensorDTO.getSensorType().equals("MQ2")){
            alertMq2(roomId, sensorDTO);
        }
        checkCustomAlerts(roomId, sensorDTO);
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
                String esp32x1RoomId = "9JFObYv8R4mCtkYtac77";
                String esp32x2RoomId = "jmwXMITpjvJSSK1egu4O";

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

                if(topic.equals(topicEsp32x1)){
                    System.out.println("Processing Esp32x1 topic: " + topic);
                    Gson gson = new Gson();

                    @SuppressWarnings("unchecked")
                    Map<String,Object> data = gson.fromJson(payload,Map.class);
                    int mq2Value = ((Number) data.get("mq2")).intValue();
                    int mq5Value = ((Number) data.get("mq5")).intValue();
                    long timestamp = Long.parseLong((String) data.get("timestamp"));
                    Map<String,Float> esp32x1Mq2Data = new HashMap<>();
                    Map<String,Float> esp32x1Mq5Data = new HashMap<>();

                    esp32x1Mq2Data.put("mq2Value", (float) mq2Value);
                    esp32x1Mq5Data.put("gas", (float) mq5Value);
                    Details detailsMq2 = new Details(timestamp,esp32x1Mq2Data);
                    Details detailsMq5 = new Details(timestamp,esp32x1Mq5Data);

                    String esp32x1Mq2SensorId = "EblfbGBn7wrJi9Zqtb0S";
                    String esp32x1Mq5SensorId = "ok7tYDjTHI5OLUUcRm8q";

                    SensorDTO esp32x1Mq2SensorDTO = new SensorDTO(esp32x1Mq2SensorId,"MQ2",36,List.of(detailsMq2));
                    SensorDTO esp32x1Mq5SensorDTO = new SensorDTO(esp32x1Mq5SensorId,"MQ5",39,List.of(detailsMq5));

                    processDataForRoom(esp32x1Mq2SensorDTO,esp32x1RoomId);
                    processDataForRoom(esp32x1Mq5SensorDTO,esp32x1RoomId);

                }
                if(topic.equals(topicEsp32x2)){
                    System.out.println("Processing Esp32x2 topic: " + topic);
                    Gson gson = new Gson();

                    @SuppressWarnings("unchecked")
                    Map<String,Object> data = gson.fromJson(payload,Map.class);
                    int mq2Value = ((Number) data.get("mq2Value")).intValue();
                    float temperature = ((Number) data.get("temperature")).floatValue();
                    float humidity = ((Number) data.get("humidity")).floatValue();
                    long timestamp = Long.parseLong((String) data.get("timestamp"));

                    Map<String,Float> esp32x2Mq2Data = new HashMap<>();
                    esp32x2Mq2Data.put("mq2Value", (float) mq2Value);
                    Details detailsMq2 = new Details(timestamp,esp32x2Mq2Data);
                    String mq2SensorId = "bS85GgrlLs9ikiNG0EXU";
                    SensorDTO esp32x2Mq2SensorDTO = new SensorDTO(mq2SensorId,"MQ2",36,List.of(detailsMq2));

                    Map<String,Float> esp32x2Dht22Data = new HashMap<>();
                    esp32x2Dht22Data.put("temperature", temperature);
                    esp32x2Dht22Data.put("humidity", humidity);
                    Details detailsDht22 = new Details(timestamp,esp32x2Dht22Data);
                    String dhtSensorId = "OpjcAjYNdCkMgEb2CV0T";
                    SensorDTO esp32x2Dht22SensorDTO = new SensorDTO(dhtSensorId,"DHT22",4,List.of(detailsDht22));

                    processDataForRoom(esp32x2Mq2SensorDTO,esp32x2RoomId);
                    processDataForRoom(esp32x2Dht22SensorDTO,esp32x2RoomId);

                }

            }
        };
    }

}
