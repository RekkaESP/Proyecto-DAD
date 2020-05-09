#include <Arduino.h>
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <SoftwareSerial.h>

char responseBuffer[300];
WiFiClient client;

String SSID = "";
String PASS = "";

String SERVER_IP = "www.mocky.io";
int SERVER_PORT = 80;

void sendGetRequest();
void sendPostRequest();

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
  sendGetRequest();
  delay(3000);
  sendPostRequest();
  delay(3000);
}

void sendGetRequest(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/v2/5eb691963100006a00c89f4b", true);
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
    String sensor = doc["sensor"].as<char*>();
    long time = doc["time"].as<long>();
    float data = doc["data"].as<float>();

    Serial.println("Sensor name: " + sensor);
    Serial.println("Time: " + String(time));
    Serial.println("Data: " + String(data));
  }
}



void sendPostRequest(){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/v2/5185415ba171ea3a00704eed", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["temperature"] = 18;
    doc["humidity"] = 78;
    doc["timestamp"] = 124123123;
    doc["name"] = "sensor1";

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }

}
