#include <Arduino.h>
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <SoftwareSerial.h>

char responseBuffer[300];
WiFiClient client;

String SSID = "";
String PASS = "";

String SERVER_IP = "192.168.100.6";
int SERVER_PORT = 8090;

void sendGetSensor(int);
void sendGetMotor(int);
void sendPostSensor(int,float,float,long));
void sendPostMotor(int,float,long));

void setup() {
  Serial.begin(9600);

  WiFi.begin(SSID, PASS);

  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.print("Connected, IP address: ");
  Serial.print(WiFi.localIP());
}

void loop() {
  sendGetSensor(1);
  sendGetMotor(4);
  delay(3000);
  sendPostSensor();
  sendPostMotor();
  delay(3000000);
}

void sendGetSensor(int id){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/getSensorValueById/" + String(id), true);
    int httpCode = http.GET();

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(5) + 60;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error){
      Serial.print("deserializeJson() failed: ");
      Serial.println(error.c_str());
      return;
    }
    Serial.println(F("Response:"));
    int idsensor_value = doc["idsensor_value"].as<int>();
    int idsensor = doc["idsensor"].as<int>();
    float value = doc["value"].as<float>();
    float accuracy = doc["accuracy"].as<float>();
    long timestamp = doc["timestamp"].as<long>();

    Serial.println("idsensor_value: " + String(idsensor_value));
    Serial.println("idsensor: " + String(idsensor));
    Serial.println("value: " + String(value));
    Serial.println("accuracy: " + String(accuracy));
    Serial.println("timestamp: " + String(timestamp));
  }
}

void sendGetMotor(int id){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/getMotorValueById/" + String(id), true);
    int httpCode = http.GET();

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(4) + JSON_ARRAY_SIZE(0) + 60;
    DynamicJsonDocument doc(capacity);

    DeserializationError error = deserializeJson(doc, payload);
    if (error){
      Serial.print("deserializeJson() failed: ");
      Serial.println(error.c_str());
      return;
    }
    Serial.println(F("Response:"));
    int idmotor_value = doc["idmotor_value"].as<int>();
    float value = doc["value"].as<float>();
    long timestamp = doc["timestamp"].as<long>();
    int idmotor = doc["idmotor"].as<int>();

    Serial.println("idmotor_value: " + String(idmotor_value));
    Serial.println("Motor ID: " + String(idmotor));
    Serial.println("Value: " + String(value));
    Serial.println("Time: " + String(timestamp));
  }
}


void sendPostSensor(int idsensor, float value, float accuracy, long timestamp){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/putSensorValue", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["idsensor"] = idsensor;
    doc["value"] = value;
    doc["accuracy"] = accuracy;
    doc["timestamp"] = timestamp;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }

}

void sendPostMotor(int idmotor, float value, long timestamp){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/putMotorValue", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["value"] = value;
    doc["timestamp"] = timestamp;
    doc["idmotor"] = idmotor;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }
}
