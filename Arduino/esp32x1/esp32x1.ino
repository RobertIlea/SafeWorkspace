#include <WiFi.h>                
#include <PubSubClient.h>       
#include <WiFiUdp.h>            
#include <NTPClient.h>          
#include "arduino_secrets.h"    

// Load Wi-Fi credentials from arduino_secrets.h
char ssid[] = SSID;
char pass[] = PASS;

// Create Wi-Fi and MQTT clients
WiFiClient wifiClient;
PubSubClient mqttClient(wifiClient);

// Setup NTP client
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org", 0, 60000);

// MQTT broker configuration
const char* broker = "broker.emqx.io";
int port = 1883;
const char* clientId = "esp32x1Robicu";
const char* topic = "esp32/gas";

// Analog pins for gas sensors
const int mq2Pin = 36;  // MQ2: smoke, LPG, methane
const int mq5Pin = 39;  // MQ5: LPG, natural gas, coal gas

// Connect to Wi-Fi
void setup_wifi() {
  delay(100);
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected");
}

// Reconnect to MQTT broker if disconnected
void reconnect() {
  while (!mqttClient.connected()) {
    if (mqttClient.connect(clientId)) {
      Serial.println("Connected to MQTT");
    } else {
      Serial.print("Failed, rc=");
      Serial.print(mqttClient.state());
      delay(2000); // Retry every 2 seconds
    }
  }
}

void setup() {
  Serial.begin(115200);               // Start serial monitor
  setup_wifi();                       // Connect to Wi-Fi
  mqttClient.setServer(broker, port); // Set MQTT broker address
  timeClient.begin();                 // Start NTP client
}

void loop() {
  // Ensure MQTT is connected
  if (!mqttClient.connected()) {
    reconnect();
  }
  mqttClient.loop(); // Handle MQTT keep-alive
  timeClient.update(); // Sync current time

  // Read values from MQ2 and MQ5 gas sensors
  int mq2Value = analogRead(mq2Pin);
  int mq5Value = analogRead(mq5Pin);

  // Get current timestamp in UNIX format
  unsigned long timestamp = timeClient.getEpochTime();

  // Construct JSON payload with sensor values and timestamp
  String payload = "{";
  payload += "\"timestamp\":\"" + String(timestamp) + "\",";
  payload += "\"mq2\":" + String(mq2Value) + ",";
  payload += "\"mq5\":" + String(mq5Value);
  payload += "}";

  // Print and publish to MQTT
  Serial.println("Publishing: " + payload);
  mqttClient.publish(topic, payload.c_str());

  delay(5000); // Wait 5 seconds before next reading
}
