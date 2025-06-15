#include <WiFi.h>
#include <PubSubClient.h>
#include <DHT.h>
#include <WiFiUdp.h>
#include <NTPClient.h>
#include "arduino_secrets.h"


// WiFi credentials
char ssid[] = SSID;        
char pass[] = PASS;

// Define WiFi and MQTT client
WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

// NTP Setup
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 0, 60000);

// MQTT broker settings
const char* broker = "broker.emqx.io"; 
int port = 1883;  

// MQTT topics for sensors
const char sensorTopic[] = "sensor/dht22/data"; // topic for dht22 sensor
const char mq5Topic[] = "sensor/mq5/data"; // topic for mq5 sensor
const char* esp32Topic = "esp32/gas";         // topic from ESP32 x1
const char* esp32x2Topic = "esp32x2/data"; // topic from ESP32 x2
const char* forwardedTopic = "sensor/esp32x1/data";    // topic to send to server for ESP32 x1
const char* forwardedx2Topic = "sensor/esp32x2/data"; // topic to send to server for ESP32 x2
const char* clientId = "Robicu03";

// DHT sensor setup
#define DHTPIN 2        // DHT22 is connected to digital pin 2
#define DHTTYPE DHT22   // Using DHT22 sensor
DHT dht(DHTPIN, DHTTYPE);

// MQ5 sensor setup
#define MQ5PIN A0 // MQ5 is connected to A0 on board

// Interval for sending messages (milliseconds)
const long interval = 5000;
unsigned long previousMillis = 0;

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived on topic: ");
  Serial.println(topic);

  String message;
  for (unsigned int i = 0; i < length; i++) {
    message += (char)payload[i];
  }

  Serial.print("Payload: ");
  Serial.println(message);

  if (String(topic) == esp32Topic) {
    mqttClient.publish(forwardedTopic, message.c_str());
    Serial.println("Forwarded to sensor/esp32x1/data");
  } 
  else if (String(topic) == esp32x2Topic) {
    mqttClient.publish(forwardedx2Topic, message.c_str());
    Serial.println("Forwarded to sensor/esp32x2/data");
  }
}


void setup() {
  Serial.begin(9600);
  while (!Serial);

  // Connect to WiFi
  Serial.print("Connecting to WiFi: ");
  while (WiFi.begin(ssid, pass) != WL_CONNECTED) {
    Serial.print(".");
    delay(5000);
  }
  Serial.println("\nConnected to WiFi!");

  // Set up MQTT client
  mqttClient.setServer(broker, port);
  mqttClient.setCallback(callback);

  // Connect to MQTT broker
  while (!mqttClient.connected()) {
    Serial.print("Connecting to MQTT broker...");
    if (mqttClient.connect(clientId)) {
      Serial.println("Connected to MQTT broker!");
      mqttClient.subscribe(esp32Topic); // Subscribe to ESP32 x1 Topic
      mqttClient.subscribe(esp32x2Topic); // Subscribe to ESP32 x2 Topic
    } else {
      Serial.print("Failed to connect, retrying in 5 seconds. Error: ");
      Serial.println(mqttClient.state());
      delay(5000);
    }
  }

  // Start the DHT sensor
  dht.begin();

  // Start NTP client
  timeClient.begin();

}

void loop() {
  mqttClient.loop();  // Handle incoming messages

  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) {
    previousMillis = currentMillis;

    // Read temperature and humidity for DHT22
    float temperature = dht.readTemperature();
    float humidity = dht.readHumidity();
    int mq5Value = analogRead(MQ5PIN);

    // Check if reading is valid
    if (isnan(temperature) || isnan(humidity)) {
      Serial.println("Failed to read from DHT22 sensor!");
      return;
    }

    // Update NTP time
    timeClient.update();
    unsigned long timestamp = timeClient.getEpochTime();

    // Print the values to the serial monitor
    Serial.print("Temperature: ");
    Serial.print(temperature);
    Serial.println(" °C");

    Serial.print("Humidity: ");
    Serial.print(humidity);
    Serial.println(" %");

    Serial.print("Timestamp: ");
    Serial.println(timestamp);

    Serial.print("Gas level: ");
    Serial.println(mq5Value);

    // JSON formatted string for DHT22
    char dht22Payload[150];
    snprintf(dht22Payload, sizeof(dht22Payload), "{\"temperature\": %.2f, \"humidity\": %.2f, \"timestamp\": %lu}", temperature, humidity, timestamp);

    // Publish the data read by DHT22    
    mqttClient.publish(sensorTopic, dht22Payload);

    // JSON formatted string for MQ5
    char mq5Payload[150];
    snprintf(mq5Payload, sizeof(mq5Payload),"{\"gasLevel\": %d, \"timestamp\": %lu}", mq5Value, timestamp);
    mqttClient.publish(mq5Topic, mq5Payload);

    Serial.println("Data published to MQTT broker!");
  }
}
