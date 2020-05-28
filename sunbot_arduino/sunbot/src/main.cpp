
#include <Arduino.h>
#include "ArduinoJson.h"
#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <SoftwareSerial.h>
#include <PubSubClient.h>
#include <Servo.h>

char responseBuffer[300];
WiFiClient client;
String SSID = "FTTH_Palacios_ilCqwk";
String PASS = "3X76dhNs";
String SERVER_IP = "192.168.100.46";
int SERVER_PORT = 8090;

//MQTT
PubSubClient MQTTclient(client);
unsigned long lastMsg = 0;
#define MSG_BUFFER_SIZE	(50)
char msg[MSG_BUFFER_SIZE];
int value = 0;
const char* mqtt_server = "192.168.100.46";
const char* mqtt_username = "mqttbroker";
const char* mqtt_password = "mqttbrokerpass";

//Motores
const int motorIzqAdelante = D7;
const int motorIzqAtras = D8;
const int motorDerAdelante = D5;
const int motorDerAtras = D6;

//Sensor ultrasonico
const int Trigger = D0;
const int Echo = D1;

//Multiplexor
const int S0 = D2;
const int S1 = D3;
const int sensor = A0;

//servo_motor
const int servo = D4;

Servo servo_motor;
boolean haciaDelante = false;
int distancia = 100;
int valorSensor = 0;
int salida = 0;
int distanciaDer = 0;
int distanciaIzq = 0;
int lumIzq = 0;
int lumDer = 0;
int distanciaCalc=0;
int calcLum = 0;
float diferenciaLum = 0;
long ultimaLectura = 0;
long t = 0;
long d = 0;

void mueveAdelante();
void mueveAtras();
void giraIzquierda();
void giraDerecha();
void pararMotores();
void evitaChocar();
int buscaLuz();
int calculaDistancia();
int miraIzquierda();
int miraDerecha();
void callback(char* topic, byte* payload, unsigned int length);
void reconnect();
void sendGetSensor(int);
void sendGetMotor(int);
void sendPostSensor(int,float,float,long);
void sendPostMotor(int,float,long);


void setup() {
  Serial.begin(9600);
  //WIFI
  WiFi.begin(SSID, PASS);
  MQTTclient.setServer(mqtt_server, 1885);
  MQTTclient.setCallback(callback);
  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print(".");
  }
  Serial.print("Connected, IP address: ");
  Serial.print(WiFi.localIP());
  //PINES
  pinMode(Trigger, OUTPUT);
  pinMode(Echo, INPUT);
  pinMode(motorDerAdelante, OUTPUT);
  pinMode(motorIzqAdelante, OUTPUT);
  pinMode(motorIzqAtras, OUTPUT);
  pinMode(motorDerAtras, OUTPUT);
  pinMode(S1,OUTPUT);
  pinMode(S0,OUTPUT);
  pinMode(sensor,INPUT);
  digitalWrite(Trigger, LOW);
  servo_motor.attach(servo);
  servo_motor.write(90);
  delay(75);
}

void loop(){
  if (!MQTTclient.connected()) {
    reconnect();
  }
  MQTTclient.loop();

  delay(100);
  ultimaLectura = ultimaLectura+100;
  distancia = calculaDistancia();
  calcLum = buscaLuz();
  if (distancia <= 20){
    printf("Objeto a %icm, evitando chocar...\n",distancia);
    evitaChocar();
  }else if(calcLum==3){
    printf("Lectura de luminosidad realizada recientemente, no se leerá.\n");
    mueveAdelante();
  }else if(calcLum==0){
    printf("No hay peligro y luz igual en ambos lados(Luz derecha = %i; Luz izquierda=%i), moviendo hacia delante... [%ld]\n",lumDer,lumIzq,ultimaLectura);
    mueveAdelante();
  }else if(calcLum==1){
    printf("Luz derecha = %i; Luz izquierda=%i, moviendo izquierda...\n",lumDer,lumIzq);
    giraIzquierda();
  }else if(calcLum==2){
    printf("Luz derecha = %i; Luz izquierda=%i, moviendo derecha...\n",lumDer,lumIzq);
    giraDerecha();
  }
  /*
  unsigned long now = millis();
  if (now - lastMsg > 6000) {
    lastMsg = now;
    ++value;
    snprintf (msg, MSG_BUFFER_SIZE, "[Humedad]%d", value);
    Serial.print("Publish message: ");
    Serial.println(msg);
    MQTTclient.publish("sensor", msg);
    }
    */
}

//MQTT
void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived [");
  Serial.print(topic);
  Serial.print("] ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
  Serial.println();

  // Switch on the LED if an 1 was received as first character
  if ((char)payload[0] == '1') {
    digitalWrite(BUILTIN_LED, LOW);   // Turn the LED on (Note that LOW is the voltage level
    // but actually the LED is on; this is because
    // it is active low on the ESP-01)
  } else {
    digitalWrite(BUILTIN_LED, HIGH);  // Turn the LED off by making the voltage HIGH
  }
}

void reconnect() {
  // Loop until we're reconnected
  while (!MQTTclient.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (MQTTclient.connect(clientId.c_str(),mqtt_username,mqtt_password)) {
      Serial.println("connected");
      // Once connected, publish an announcement...
      MQTTclient.publish("info", "ESP8266 Conectado",true);
      // ... and resubscribe
      MQTTclient.subscribe("sensor", 0);
    } else {
      Serial.print("failed, rc=");
      Serial.print(MQTTclient.state());
      Serial.println(" trying again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

//FUNCIONAMIENTO ROBOT
void evitaChocar(){
  if(distancia<10){
    mueveAtras();
    delay(200);
  }
  pararMotores();
  distanciaDer = miraDerecha();
  delay(100);
  distanciaIzq = miraIzquierda();
  if (distanciaDer >= distanciaIzq){
    giraIzquierda();
    delay(200);
  }else if (distanciaIzq >= distanciaDer){
    giraDerecha();
    delay(200);
  }
}

void calculaLuminosidadIzq(){
  digitalWrite(S1,LOW);
  digitalWrite(S0,HIGH);
  lumIzq = analogRead(sensor);
}

void calculaLuminosidadDer(){
  digitalWrite(S1,HIGH);
  digitalWrite(S0,LOW);
  lumDer = analogRead(sensor);
}

int calculaHumedad(){
  int hum = 0;
  digitalWrite(S1,LOW);
  digitalWrite(S0,LOW);
  hum = analogRead(sensor);
  return hum;
}

int miraDerecha(){
  servo_motor.write(30);
  delay(200);
  distanciaCalc = calculaDistancia();
  delay(100);
  servo_motor.write(90);
  return distanciaCalc;
}

int miraIzquierda(){
  servo_motor.write(150);
  delay(200);
  distanciaCalc = calculaDistancia();
  delay(100);
  servo_motor.write(90);
  return distanciaCalc;
}

void pararMotores(){
  digitalWrite(motorDerAdelante, LOW);
  digitalWrite(motorIzqAdelante, LOW);
  digitalWrite(motorDerAtras, LOW);
  digitalWrite(motorIzqAtras, LOW);
}

void mueveAdelante(){
  digitalWrite(motorIzqAdelante, HIGH);
  digitalWrite(motorDerAdelante, HIGH);
  digitalWrite(motorIzqAtras, LOW);
  digitalWrite(motorDerAtras, LOW);
}

void mueveAtras(){
  digitalWrite(motorIzqAtras, HIGH);
  digitalWrite(motorDerAtras, HIGH);
  digitalWrite(motorIzqAdelante, LOW);
  digitalWrite(motorDerAdelante, LOW);
}

void giraDerecha(){
  digitalWrite(motorIzqAdelante, LOW);
  digitalWrite(motorDerAtras, LOW);
  digitalWrite(motorIzqAtras, HIGH);
  digitalWrite(motorDerAdelante, HIGH);
  delay(1000);
  digitalWrite(motorIzqAdelante, HIGH);
  digitalWrite(motorDerAdelante, HIGH);
  digitalWrite(motorIzqAtras, LOW);
  digitalWrite(motorDerAtras, LOW);
}

void giraIzquierda(){
  digitalWrite(motorIzqAtras, HIGH);
  digitalWrite(motorDerAdelante, HIGH);
  digitalWrite(motorIzqAdelante, LOW);
  digitalWrite(motorDerAtras, LOW);
  delay(1000);
  digitalWrite(motorIzqAdelante, HIGH);
  digitalWrite(motorDerAdelante, HIGH);
  digitalWrite(motorIzqAtras, LOW);
  digitalWrite(motorDerAtras, LOW);
}

int calculaDistancia(){
  servo_motor.write(90);
  delay(10);
  digitalWrite(Trigger, HIGH);
  delayMicroseconds(10);          //Enviamos un pulso de 10us
  digitalWrite(Trigger, LOW);
  t = pulseIn(Echo, HIGH); //obtenemos el ancho del pulso
  d = t/59;             //escalamos el tiempo a una distancia en cm
  return d;
}

int buscaLuz() {
  calculaLuminosidadIzq();
  calculaLuminosidadDer();
  diferenciaLum = lumIzq - lumDer;
  if(ultimaLectura>=500){
    ultimaLectura = 0;
    if(diferenciaLum > 100){
      return 1;
    }else if(diferenciaLum < -100){
      return 2;
    }else{
      return 0;
    }
  }else{
    return 3;
  }
}
//API REST
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
