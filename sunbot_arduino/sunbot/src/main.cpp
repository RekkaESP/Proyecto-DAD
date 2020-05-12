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
void sendGetMotor();
void sendPostSensor();
void sendPostMotor();

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
//  sendGetMotor();
  delay(3000);
//  sendPostSensor();
//  sendPostMotor();
  delay(3000000);
}

void sendGetSensor(int id){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/getSensorValueById/" + String(id), true);
    int httpCode = http.GET();

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(5) + JSON_ARRAY_SIZE(0) + 60;
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

void sendGetMotor(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/v2/5eba732b2f00001a323c3833", true);
    int httpCode = http.GET();

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
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


void sendPostSensor(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/putSensorValue", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["idsensor"] = 1;
    doc["value"] = 10.0;
    doc["accuracy"] = 0.58;
    doc["timestamp"] = 124123123;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }

}

void sendPostMotor(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/putMotorValue", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["value"] = 78;
    doc["timestamp"] = 124123123;
    doc["idmotor"] = 1;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }
}
