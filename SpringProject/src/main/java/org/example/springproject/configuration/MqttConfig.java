/**
 * MqttConfig.java
 * Configuration class for Message Queuing Telemetry Transport (MQTT).
 * This class sets up MQTT client, message channels, and handlers for processing sensor data.
 * @author Ilea Robert-Ioan
 */
package org.example.springproject.configuration;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.example.springproject.dto.RoomDTO;
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

/**
 * MqttConfig class is responsible for configuring MQTT client, message channels and handlers.
 * It processes incoming sensor data, checks for alerts based on sensor readings and custom alerts.
 * It also saves sensor data and updates room information accordingly.
 * It is marked as a Spring configuration class with @Configuration annotation so that it can be scanned and registered as a bean in the Spring context.
 */
@Configuration
public class MqttConfig {

    /**
     * brokerUrl is the URL of the MQTT broker to which the client will connect.
     */
    private final String brokerUrl = "tcp://broker.emqx.io:1883";

    /**
     * topicDht22, topicMq5, topicEsp32x1, topicEsp32x2 are the MQTT topics to which the client will subscribe.
     */
    private final String topicDht22= "sensor/dht22/data";
    private final String topicMq5 = "sensor/mq5/data";
    private final String topicEsp32x1 = "sensor/esp32x1/data";
    private final String topicEsp32x2 = "sensor/esp32x2/data";

    /**
     * Autowired services for handling sensor data, room management, alerts, custom alerts, user management and Twilio service.
     * These services are injected into the MqttConfig class to be used for processing sensor data and sending alerts.
     */
    @Autowired
    private final SensorService sensorService;
    private final RoomService roomService;
    private final AlertService alertService;
    private final CustomAlertService customAlertService;
    private final AlertManager alertManager;
    private final UserService userService;
    private final TwilioService twilioService;

    /**
     * Constructor for MqttConfig class.
     * Initializes the MqttConfig with the required services.
     * @param sensorService the service for handling sensor data
     * @param roomService the service for managing rooms
     * @param alertService the service for handling alerts
     * @param customAlertService the service for handling custom alerts
     * @param alertManager the service for managing alerts and sending notifications
     * @param userService the service for managing users
     * @param twilioService the service for handling Twilio SMS and calls
     */
    public MqttConfig(SensorService sensorService, RoomService roomService, AlertService alertService, CustomAlertService customAlertService, AlertManager alertManager, UserService userService, TwilioService twilioService) {
        this.sensorService = sensorService;
        this.roomService = roomService;
        this.alertService = alertService;
        this.customAlertService = customAlertService;
        this.alertManager = alertManager;
        this.userService = userService;
        this.twilioService = twilioService;
    }

    /**
     * mqttInputChannel method creates a DirectChannel bean for receiving MQTT messages.
     * This channel will be used to receive messages from MQTT topics and process them accordingly.
     * This method is annotated with @Bean, which indicates that it will be registered as a bean in the Spring application context.
     * @return a MessageChannel that can be used to receive messages from MQTT topics.
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * mqttClient method creates an MqttClient bean that connects to the MQTT broker.
     * It sets up a callback to handle incoming messages, connection loss, and message delivery completion.
     * It also subscribes to the specified MQTT topics.
     * This method is annotated with @Bean, which indicates that it will be registered as a bean in the Spring application context.
     * @return an MqttClient that is connected to the broker and subscribed to the specified topics.
     * @throws Exception if there is an error during the creation or connection of the MQTT client.
     */
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

    /**
     * inbound method creates an MqttPahoMessageDrivenChannelAdapter bean that listens to the specified MQTT topics.
     * It sets the completion timeout, message converter, quality of service (QoS), and output channel for the adapter.
     * This method is annotated with @Bean, which indicates that it will be registered as a bean in the Spring application context.
     * @return an MqttPahoMessageDrivenChannelAdapter that listens to the specified MQTT topics and sends messages to the mqttInputChannel.
     */
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

    /**
     * alertDHT22 method checks the sensor data for DHT22 sensors and triggers alerts based on temperature and humidity system threshold.
     * It processes the sensor data, checks for high or low temperature and humidity levels, and sends alerts if the system thresholds are exceeded.
     * It also saves the alert to the database and sends an email notification to the user associated with the room.
     * It also sends an SMS and makes a call to the user using Twilio service.
     * @param roomId the ID of the room where the sensor is located
     * @param sensorDTO the SensorDTO object containing sensor data
     */
    private void alertDHT22(String roomId, SensorDTO sensorDTO) {
        List<Details> detailsList = sensorDTO.getDetails();
        UserDTO userDTO = userService.getUserByRoomId(roomId);
        User userFromDTO = UserMapper.toEntity(userDTO);
        Sensor sensorFromDTO = SensorMapper.toEntity(sensorDTO);
        RoomDTO roomDTO = roomService.getRoomById(roomId);

        for(Details details : detailsList) {
            Map<String, Float> data = details.getData();
            if (data.containsKey("temperature")) {
                float temperature = data.get("temperature");
                if (temperature > 50) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Temperature is too high: " + temperature + "°C");

                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Temperature in room: " + roomDTO.getName() + " is too high " + temperature + " °C");
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    notifyUser(userDTO, alert, sensorFromDTO);

                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
                if (temperature < -15) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Temperature is too low: " + temperature + "°C");

                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Temperature in room: " + roomDTO.getName() + " is too low: " + temperature + " °C");
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    notifyUser(userDTO, alert, sensorFromDTO);

                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
            }
            if (data.containsKey("humidity")) {
                float humidity = data.get("humidity");
                if (humidity > 95) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Humidity is too high: " + humidity + "%");

                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Humidity in room: "+ roomDTO.getName() + " is too high: " + humidity + " %");
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    notifyUser(userDTO, alert, sensorFromDTO);

                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
                if (humidity < 10) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Humidity is too low: " + humidity + "%");

                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Humidity in room: " + roomDTO.getName() + " is too low: " + humidity + " %");
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    notifyUser(userDTO, alert, sensorFromDTO);

                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
            }
        }
    }

    /**
     * alertMq5 method checks the sensor data for MQ5 sensors and triggers alerts based on gas levels.
     * It processes the sensor data, checks for high gas levels, and sends alerts if the system threshold is exceeded.
     * It also saves the alert to the database and sends an email notification to the user associated with the room.
     * It also sends an SMS and makes a call to the user using Twilio service.
     * @param roomId the ID of the room where the sensor is located
     * @param sensorDTO the SensorDTO object containing sensor data
     */
    private void alertMq5(String roomId, SensorDTO sensorDTO) {
        List<Details> detailsList = sensorDTO.getDetails();
        UserDTO userDTO = userService.getUserByRoomId(roomId);
        User userFromDTO = UserMapper.toEntity(userDTO);
        Sensor sensorFromDTO = SensorMapper.toEntity(sensorDTO);
        RoomDTO roomDTO = roomService.getRoomById(roomId);

        for(Details details : detailsList) {
            Map<String, Float> data = details.getData();
            if (data.containsKey("gas")) {
                float gasLevel = data.get("gas");

                if (gasLevel > 700) {
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Gas level is too high: " + gasLevel);

                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Gas level in room: " + roomDTO.getName() + " is too high: " + gasLevel);
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    notifyUser(userDTO, alert, sensorFromDTO);

                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }
            }
        }
    }

    /**
     * alertMq2 method checks the sensor data for MQ2 sensors and triggers alerts based on gas levels.
     * It processes the sensor data, checks for high gas levels, and sends alerts if the system threshold is exceeded.
     * It also saves the alert to the database and sends an email notification to the user associated with the room.
     * @param roomId the ID of the room where the sensor is located
     * @param sensorDTO the SensorDTO object containing sensor data
     */
    private void alertMq2(String roomId, SensorDTO sensorDTO) {
        List<Details> detailsList = sensorDTO.getDetails();
        UserDTO userDTO = userService.getUserByRoomId(roomId);
        User userFromDTO = UserMapper.toEntity(userDTO);
        Sensor sensorFromDTO = SensorMapper.toEntity(sensorDTO);
        RoomDTO roomDTO = roomService.getRoomById(roomId);

        for(Details details : detailsList) {
            Map<String, Float> data = details.getData();
            if (data.containsKey("gas")) {
                float gasLevel = data.get("gas");

                if(gasLevel > 800){
                    System.out.println("@@@@@@@@@@@@@ ALERT @@@@@@@@@@@@@");
                    System.out.println("Gas level is too high: " + gasLevel);

                    Alert alert = new Alert(roomId,sensorDTO.getId(),details.getTimestamp(),sensorDTO.getSensorType(),data,"Smoke or gas level in room: "+ roomDTO.getName() + " is too high: " + gasLevel);
                    alertService.saveAlert(alert);
                    alertManager.sendEmail(userFromDTO,alert,sensorFromDTO);
                    notifyUser(userDTO, alert, sensorFromDTO);

                    System.out.println("Alert saved");
                    System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                }

            }
        }
    }

    /**
     * notifyUser method sends an SMS and makes a call to the user associated with the room when an alert is triggered.
     * @param userDTO the UserDTO object containing user information
     * @param alert the Alert object containing alert information
     * @param sensor the Sensor object containing sensor information
     * @throws RuntimeException if there is an error while trying to contact the user
     */
    private void notifyUser(UserDTO userDTO, Alert alert, Sensor sensor) throws RuntimeException {
        try {
            // Get user's phone number
            String userPhoneNumber = userService.getUserPhoneNumber(userDTO.getId());

            // Message the user
            String message = "Hello, " + userDTO.getName() + "\n" + "The sensor type is: " + sensor.getSensorType() + "\n" + alert.getMessage();
            twilioService.sendSms(userPhoneNumber,message);

            // Call the user
            twilioService.makeCall(userPhoneNumber);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't contact the user!" + e);
        }
    }

    /**
     * checkCustomAlerts method checks for custom alerts based on sensor data.
     * It retrieves all custom alerts associated with the sensor, evaluates the conditions for each alert, and triggers an alert if the condition is met.
     * It saves the custom alert to the database and sends an email notification to the user associated with the room.
     * It also sends an SMS and makes a call to the user using Twilio service.
     * @param roomId the ID of the room where the sensor is located
     * @param sensorDTO the SensorDTO object containing sensor data
     * @throws Exception if there is an error while processing custom alerts
     */
    private void checkCustomAlerts(String roomId, SensorDTO sensorDTO) throws Exception {
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
                        notifyUser(userDTO, alert, sensorFromDTO);

                        System.out.println("Custom alert saved");
                        System.out.println("@@@@@@@@@@@@@ END OF THE ALERT @@@@@@@@@@@@@");
                    }
                }
            }
        }
    }

    /**
     * evaluateCondition method evaluates a condition based on the value, condition string, and threshold.
     * @param value the value to be evaluated
     * @param condition the condition string (">", "<", ">=", "<=", "==")
     * @param threshold the threshold value to compare against
     * @return true if the condition is met, false otherwise
     */
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

    /**
     * processDataForRoom method processes the sensor data for a specific room.
     * It checks the sensor type and calls the appropriate alert method based on the sensor type.
     * It also saves the sensor data and updates the room with the sensor data.
     * @param sensorDTO the SensorDTO object containing sensor data
     * @param roomId the ID of the room where the sensor is located
     * @throws Exception if there is an error while processing the data for the room
     */
    private void processDataForRoom(SensorDTO sensorDTO, String roomId) throws Exception {
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
        } catch (Exception e) {
            throw new RuntimeException("Failed to proccess data for room: " + roomId + e);
        }
    }

    /**
     * handler method creates a MessageHandler bean that processes incoming MQTT messages.
     * It extracts the payload and topic from the message, processes the sensor data based on the topic and calls the processDataForRoom method.
     * This method is annotated with @ServiceActivator, which indicates that it will be used to handle messages from the mqttInputChannel.
     * @return a MessageHandler that processes incoming MQTT messages and triggers alerts based on sensor data.
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            /**
             * handleMessage method processes the incoming message.
             * It extracts the payload and topic from the message, processes the sensor data based on the topic.
             * It checks the topic and processes the sensor data accordingly, triggering alerts if necessary.
             * @param message the incoming message containing the sensor data
             * @throws MessagingException if there is an error while processing the message
             */
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String payload = (String) message.getPayload();
                System.out.println("Processed message: " + payload);
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();

                // Define room IDs for processing sensor data
                String primaryRoomId = "1RdkB5aniSqVc1GayEVr";
                String esp32x1RoomId = "9JFObYv8R4mCtkYtac77";
                String esp32x2RoomId = "jmwXMITpjvJSSK1egu4O";

                if (topic.equals(topicDht22)) {
                    // Create a Gson instance to parse JSON data
                    Gson gson = new Gson();

                    // Parse the JSON payload into a Map
                    // Suppress unchecked warnings for type casting because we know for sure the payload is a Map<String, Object>
                    // It is Map<String, Object> because the payload is a JSON string that contains sensor data
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = gson.fromJson(payload,Map.class);

                    // Extract temperature, humidity, and timestamp from the parsed data
                    float temperature = ((Number) data.get("temperature")).floatValue();
                    float humidity = ((Number) data.get("humidity")).floatValue();
                    long timestamp = ((Number) data.get("timestamp")).longValue();

                    // Create a map to hold sensor data
                    Map<String,Float> sensorData = new HashMap<>();
                    sensorData.put("temperature",temperature);
                    sensorData.put("humidity",humidity);
                    Details details = new Details(timestamp,sensorData);

                    // Define the sensor ID for DHT22 sensor
                    String dhtSensorId ="HGNX6Kp9FiSTSclNwHHN";

                    // Create a SensorDTO object with the sensor ID, type, and details
                    SensorDTO dht22SensorDTO = new SensorDTO(dhtSensorId,"DHT22",21, List.of(details));

                    try {
                        // Process the sensor data for the primary room
                        processDataForRoom(dht22SensorDTO,primaryRoomId);
                    } catch (Exception e) {
                        throw new RuntimeException("Couldn't process data for DHT22 sensor: " + e);
                    }
                }

                if(topic.equals(topicMq5)){
                    // Create a Gson instance to parse JSON data
                    Gson gson = new Gson();

                    // Parse the JSON payload into a Map
                    // Suppress unchecked warnings for type casting because we know for sure the payload is a Map<String, Object>
                    // It is Map<String, Object> because the payload is a JSON string that contains sensor data
                    @SuppressWarnings("unchecked")
                    Map<String,Object> data = gson.fromJson(payload,Map.class);
                    int gasLevel = ((Number) data.get("gasLevel")).intValue();
                    long timestamp = ((Number) data.get("timestamp")).longValue();

                    // Create a map to hold sensor data
                    Map<String,Float> mq5Data = new HashMap<>();
                    mq5Data.put("gas", (float) gasLevel);
                    Details details = new Details(timestamp,mq5Data);

                    // Define the sensor ID for MQ5 sensor
                    String mq5SensorId ="nv0MubTXWBrjHZpQlZxl";

                    // Create a SensorDTO object with the sensor ID, type, and details
                    SensorDTO mq5SensorDTO = new SensorDTO(mq5SensorId,"MQ5",0, List.of(details));

                    try {
                        // Process the sensor data for the primary room
                        processDataForRoom(mq5SensorDTO,primaryRoomId);
                    } catch (Exception e) {
                        throw new RuntimeException("Couldn't process data for MQ5 sensor: " + e);
                    }
                }

                if(topic.equals(topicEsp32x1)){
                    // Create a Gson instance to parse JSON data
                    Gson gson = new Gson();

                    // Parse the JSON payload into a Map
                    // Suppress unchecked warnings for type casting because we know for sure the payload is a Map<String, Object>
                    // It is Map<String, Object> because the payload is a JSON string that contains sensor data
                    @SuppressWarnings("unchecked")
                    Map<String,Object> data = gson.fromJson(payload,Map.class);
                    int mq2Value = ((Number) data.get("mq2")).intValue();
                    int mq5Value = ((Number) data.get("mq5")).intValue();
                    long timestamp = Long.parseLong((String) data.get("timestamp"));
                    Map<String,Float> esp32x1Mq2Data = new HashMap<>();
                    Map<String,Float> esp32x1Mq5Data = new HashMap<>();

                    // Create a map to hold MQ2 sensor data
                    esp32x1Mq2Data.put("mq2Value", (float) mq2Value);
                    Details detailsMq2 = new Details(timestamp,esp32x1Mq2Data);

                    // Create a map to hold MQ5 sensor data
                    esp32x1Mq5Data.put("gas", (float) mq5Value);
                    Details detailsMq5 = new Details(timestamp,esp32x1Mq5Data);

                    // Define sensor IDs for MQ2 and MQ5 sensors
                    String esp32x1Mq2SensorId = "EblfbGBn7wrJi9Zqtb0S";
                    String esp32x1Mq5SensorId = "ok7tYDjTHI5OLUUcRm8q";

                    // Create SensorDTO objects for MQ2 and MQ5 sensors
                    SensorDTO esp32x1Mq2SensorDTO = new SensorDTO(esp32x1Mq2SensorId,"MQ2",36,List.of(detailsMq2));
                    SensorDTO esp32x1Mq5SensorDTO = new SensorDTO(esp32x1Mq5SensorId,"MQ5",39,List.of(detailsMq5));

                    try {
                        // Process the sensor data for Esp32x1 sensors
                        processDataForRoom(esp32x1Mq2SensorDTO,esp32x1RoomId);
                        processDataForRoom(esp32x1Mq5SensorDTO,esp32x1RoomId);
                    } catch (Exception e) {
                        throw new RuntimeException("Couldn't process data for Esp32x1 sensors: " + e);
                    }
                }
                if(topic.equals(topicEsp32x2)){
                    // Create a Gson instance to parse JSON data
                    Gson gson = new Gson();

                    // Parse the JSON payload into a Map
                    // Suppress unchecked warnings for type casting because we know for sure the payload is a Map<String, Object>
                    // It is Map<String, Object> because the payload is a JSON string that contains sensor data
                    @SuppressWarnings("unchecked")
                    Map<String,Object> data = gson.fromJson(payload,Map.class);
                    int mq2Value = ((Number) data.get("mq2Value")).intValue();
                    float temperature = ((Number) data.get("temperature")).floatValue();
                    float humidity = ((Number) data.get("humidity")).floatValue();
                    long timestamp = Long.parseLong((String) data.get("timestamp"));

                    // Create a map to hold MQ2 sensor.
                    Map<String,Float> esp32x2Mq2Data = new HashMap<>();
                    esp32x2Mq2Data.put("mq2Value", (float) mq2Value);
                    Details detailsMq2 = new Details(timestamp,esp32x2Mq2Data);
                    String mq2SensorId = "bS85GgrlLs9ikiNG0EXU";
                    SensorDTO esp32x2Mq2SensorDTO = new SensorDTO(mq2SensorId,"MQ2",36,List.of(detailsMq2));

                    // Create a map to hold DHT22 sensor data
                    Map<String,Float> esp32x2Dht22Data = new HashMap<>();
                    esp32x2Dht22Data.put("temperature", temperature);
                    esp32x2Dht22Data.put("humidity", humidity);
                    Details detailsDht22 = new Details(timestamp,esp32x2Dht22Data);
                    String dhtSensorId = "OpjcAjYNdCkMgEb2CV0T";
                    SensorDTO esp32x2Dht22SensorDTO = new SensorDTO(dhtSensorId,"DHT22",4,List.of(detailsDht22));

                    try {
                        // Process the sensor data for Esp32x2 sensors
                        processDataForRoom(esp32x2Mq2SensorDTO,esp32x2RoomId);
                        processDataForRoom(esp32x2Dht22SensorDTO,esp32x2RoomId);
                    } catch (Exception e) {
                        throw new RuntimeException("Couldn't process data for Esp32x2 sensors: " + e);
                    }
                }
            }
        };
    }

}
