#include <WiFi.h>              
#include <PubSubClient.h>       
#include <DHT.h>                
#include <WiFiUdp.h>            
#include <NTPClient.h>          
#include "arduino_secrets.h"    

// Load Wi-Fi credentials from arduino_secrets.h
char ssid[] = SSID;
char pass[] = PASS;

// Create Wi-Fi and MQTT clients
WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

// Setup NTP client to get current epoch time
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 0, 60000);

// MQTT broker configuration
const char* broker = "broker.emqx.io";
int port = 1883;
const char* clientId = "esp32x2Robicu";      // Unique client ID
const char* topic = "esp32x2/data";          // MQTT topic to publish data

// DHT sensor configuration
#define DHTPIN 4           // GPIO pin connected to DHT22
#define DHTTYPE DHT22      // Using DHT22 sensor
DHT dht(DHTPIN, DHTTYPE);

// MQ2 gas sensor pin
const int mq2Pin = 36;

// Connect to Wi-Fi network
void connectToWiFi() {
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("WiFi connected.");
}

// Connect to MQTT broker
void connectToMQTT() {
  while (!mqttClient.connected()) {
    Serial.print("Connecting to MQTT...");
    if (mqttClient.connect(clientId)) {
      Serial.println("connected.");
    } else {
      Serial.print("failed, rc=");
      Serial.print(mqttClient.state());
      Serial.println(" try again in 5 seconds.");
      delay(5000);
    }
  }
}

void setup() {
  Serial.begin(115200);     // Initialize serial communication for debugging
  dht.begin();              // Start DHT sensor

  connectToWiFi();          // Connect to Wi-Fi
  mqttClient.setServer(broker, port); // Set MQTT broker info
  timeClient.begin();       // Start NTP client
  timeClient.update();      // Sync current time
}

void loop() {
  // Ensure MQTT client is connected
  if (!mqttClient.connected()) {
    connectToMQTT();
  }
  mqttClient.loop();

  // Update time from NTP server
  timeClient.update();

  // Read sensor values
  float temperature = dht.readTemperature();
  float humidity = dht.readHumidity();
  int mq2Value = analogRead(mq2Pin);

  // Get current timestamp (epoch time)
  unsigned long timestamp = timeClient.getEpochTime();

  // If readings are valid, create JSON payload and publish to MQTT
  if (!isnan(temperature) && !isnan(humidity)) {
    String payload = "{";
    payload += "\"timestamp\":\"" + String(timestamp) + "\",";
    payload += "\"mq2Value\":" + String(mq2Value) + ",";
    payload += "\"temperature\":" + String(temperature) + ",";
    payload += "\"humidity\":" + String(humidity);
    payload += "}";

    Serial.println("Publishing: " + payload);
    mqttClient.publish(topic, payload.c_str()); // Publish to MQTT topic
  }

  delay(5000);  // Wait 5 seconds before next reading
}
