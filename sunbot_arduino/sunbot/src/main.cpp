
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
#define MSG_BUFFER_SIZE	(50)
char msg[MSG_BUFFER_SIZE];
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

//Servo motor
const int servo = D4;

//Id de los sensores
const int idSensorIzq = 0;
const int idSensorDer = 1;
const int idSensorHum = 2;

//Id de los Motores
const int idMotorIzq = 0;
const int idMotorDer = 1;
const int idMotores = 3;

//Luminosidad suficiente
const int lumin_sufic = 500;

//Estados del sensor de Luz
#define LUMIN_IGUAL_IZQ_DER 0
#define LUMIN_IZQ_MAYOR 1
#define LUMIN_DER_MAYOR 2
#define LUMIN_LEIDA_RECIENTEMENTE_SUFICIENTE 3
#define LUMIN_LEIDA_RECIENTEMENTE_BAJA 4
#define LUMIN_SUFICIENTE 5
#define LUMIN_MENOR_QUE_ANTES 6

//Estados del motor
#define MOV_ADELANTE 0
#define MOV_ATRAS 1
#define MOV_DERECHA 2
#define MOV_IZQUIERDA 3
#define MOV_QUIETO 4

typedef struct {
  int array[10];
  int idActual;
}HistorialLuz;

Servo servo_motor;

HistorialLuz hist = {{},0};

int distancia = 100;
int distanciaDer = 0;
int distanciaIzq = 0;
int lumIzq = 0;
int lumDer = 0;
int distanciaCalc=0;
int calcLum = 0;
int humedad = 0;
int mediaLuz = 0;
int ultimoEstadoMotorEnviado = -1;

float diferenciaLum = 0;

long ultimaLumEnviada = 0;
long ultimaLectura = 0;
long ultimaHum = 0;
long ultimoMensajeTelegram = 0;
long t = 0;
long d = 0;

void mueveAdelante();
void mueveAtras();
void giraIzquierda(int);
void giraDerecha(int);
void pararMotores();
void evitaChocar();
void reconnect();
void sendGetSensor(int);
void sendGetMotor(int);
void sendPostSensor(int,float,float);
void sendPostMotor(int,float);
void guardarLuz();
void calculaHumedad();

int realizaMediaLuz();
int buscaLuz();
int calculaDistancia();
int miraIzquierda();
int miraDerecha();

void setup() {
  Serial.begin(9600);
  //WIFI
  WiFi.begin(SSID, PASS);
  MQTTclient.setServer(mqtt_server, 1885);
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
  ultimaLectura += 100;
  ultimaLumEnviada += 100;
  ultimaHum += 100;
  ultimoMensajeTelegram += 100;
  if(ultimaLumEnviada>=2000){
    sendPostSensor(0,0,0); //Envío con todo a 0 (el primer envío nunca llega por algún motivo)
    sendPostSensor(idSensorIzq,lumIzq,1);
    sendPostSensor(idSensorDer,lumDer,1);
    ultimaLumEnviada = 0;
  }
  if(ultimaHum > 6000){
    calculaHumedad();
    sendPostSensor(0,0,0);
    sendPostSensor(idSensorHum,humedad,1);
    if (humedad > 900 && ultimoMensajeTelegram > 1800000) { //Cada 30 min = 1.800.000 miliseg
      snprintf (msg, MSG_BUFFER_SIZE, "[Humedad]%d", humedad);
      ultimoMensajeTelegram = 0;
    }
    ultimaHum = 0;
  }
  distancia = calculaDistancia();
  calcLum = buscaLuz();
  if(calcLum==LUMIN_LEIDA_RECIENTEMENTE_SUFICIENTE){
    printf("Lectura de luminosidad realizada recientemente, no se leerá ni moverá.\n");
    pararMotores();
  }else if(calcLum==LUMIN_SUFICIENTE){
    printf("Hay suficiente luz. Parando motores. (Luz derecha = %i; Luz izquierda=%i)\n",lumDer,lumIzq);
    pararMotores();
  }else if (distancia <= 20){
    printf("Objeto a %icm, evitando chocar...\n",distancia);
    evitaChocar();
  }else if(calcLum==LUMIN_LEIDA_RECIENTEMENTE_BAJA){
    printf("Lectura de luminosidad realizada recientemente, no se leerá.\n");
    mueveAdelante();
  }else if(calcLum==LUMIN_IGUAL_IZQ_DER){
    printf("No hay peligro y luz igual en ambos lados(Luz derecha = %i; Luz izquierda=%i), moviendo hacia delante... [%ld]\n",lumDer,lumIzq,ultimaLectura);
    mueveAdelante();
  }else if(calcLum==LUMIN_IZQ_MAYOR){
    printf("Luz derecha = %i; Luz izquierda=%i, moviendo izquierda...\n",lumDer,lumIzq);
    giraIzquierda(1000);
  }else if(calcLum==LUMIN_DER_MAYOR){
    printf("Luz derecha = %i; Luz izquierda=%i, moviendo derecha...\n",lumDer,lumIzq);
    giraDerecha(1000);
  }else if(calcLum==LUMIN_MENOR_QUE_ANTES){
    printf("La luminosidad ha bajado con el tiempo, dando la vuelta...\n");
    giraIzquierda(3000);
  }

}

//MQTT

void reconnect() {
  while (!MQTTclient.connected()) {
    Serial.print("Attempting MQTT connection...");
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    if (MQTTclient.connect(clientId.c_str(),mqtt_username,mqtt_password)) {
      Serial.println("connected");
      MQTTclient.publish("info", "ESP8266 Conectado",true);
      MQTTclient.subscribe("sensor", 0);
      MQTTclient.publish("sensor", msg);
    } else {
      Serial.print("failed, rc=");
      Serial.print(MQTTclient.state());
      Serial.println(" trying again in 5 seconds");
      delay(3000);
    }
  }
}

//FUNCIONAMIENTO ROBOT
void evitaChocar(){
  if(distancia<10){
    mueveAtras();
    delay(500);
  }
  pararMotores();
  distanciaDer = miraDerecha();
  distanciaIzq = miraIzquierda();
  if (distanciaDer >= distanciaIzq){
    giraDerecha(1200);
  }else if (distanciaIzq >= distanciaDer){
    giraIzquierda(1200);
  }
}

void calculaLuminosidadIzq(){
  digitalWrite(S1,HIGH);
  digitalWrite(S0,LOW);
  lumIzq = analogRead(sensor);
}

void calculaLuminosidadDer(){
  digitalWrite(S1,LOW);
  digitalWrite(S0,HIGH);
  lumDer = analogRead(sensor);
}

void calculaHumedad(){
  digitalWrite(S1,LOW);
  digitalWrite(S0,LOW);
  humedad = analogRead(sensor);
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
  if(ultimoEstadoMotorEnviado!=MOV_QUIETO){
    sendPostMotor(0,0);
    sendPostMotor(idMotores,MOV_QUIETO);
    ultimoEstadoMotorEnviado = MOV_QUIETO;
  }
  digitalWrite(motorDerAdelante, LOW);
  digitalWrite(motorIzqAdelante, LOW);
  digitalWrite(motorDerAtras, LOW);
  digitalWrite(motorIzqAtras, LOW);
}

void mueveAdelante(){
  if(ultimoEstadoMotorEnviado!=MOV_ADELANTE){
    sendPostMotor(0,0);
    sendPostMotor(idMotores,MOV_ADELANTE);
    ultimoEstadoMotorEnviado = MOV_ADELANTE;
  }
  digitalWrite(motorIzqAdelante, HIGH);
  digitalWrite(motorDerAdelante, HIGH);
  digitalWrite(motorIzqAtras, LOW);
  digitalWrite(motorDerAtras, LOW);
}

void mueveAtras(){
  if(ultimoEstadoMotorEnviado!=MOV_ATRAS){
    sendPostMotor(0,0);
    sendPostMotor(idMotores,MOV_ATRAS);
    ultimoEstadoMotorEnviado = MOV_ATRAS;
  }
  digitalWrite(motorIzqAtras, HIGH);
  digitalWrite(motorDerAtras, HIGH);
  digitalWrite(motorIzqAdelante, LOW);
  digitalWrite(motorDerAdelante, LOW);
}

void giraDerecha(int t){
  if(ultimoEstadoMotorEnviado!=MOV_DERECHA){
    sendPostMotor(0,0);
    sendPostMotor(idMotores,MOV_DERECHA);
    ultimoEstadoMotorEnviado = MOV_DERECHA;
  }
  digitalWrite(motorIzqAdelante, HIGH);
  digitalWrite(motorDerAtras, HIGH);
  digitalWrite(motorIzqAtras, LOW);
  digitalWrite(motorDerAdelante, LOW);
  delay(t);
}

void giraIzquierda(int t){
  if(ultimoEstadoMotorEnviado!=MOV_IZQUIERDA){
    sendPostMotor(0,0);
    sendPostMotor(idMotores,MOV_IZQUIERDA);
    ultimoEstadoMotorEnviado = MOV_IZQUIERDA;
  }
  digitalWrite(motorIzqAtras, HIGH);
  digitalWrite(motorDerAdelante, HIGH);
  digitalWrite(motorIzqAdelante, LOW);
  digitalWrite(motorDerAtras, LOW);
  delay(t);
}

int calculaDistancia(){
  servo_motor.write(90);
  delay(10);
  digitalWrite(Trigger, HIGH);
  delayMicroseconds(10);          //Enviamos un pulso de 10us
  digitalWrite(Trigger, LOW);
  t = pulseIn(Echo, HIGH); //obtenemos el ancho del pulso
  d = t/59;             //escalamos el tiempo a una distancia en cm
  if(d==0){
    d = 20;
  }
  return d;
}

int buscaLuz() {
  if(ultimaLectura>=500){
    calculaLuminosidadIzq();
    calculaLuminosidadDer();
    diferenciaLum = lumIzq - lumDer;
    ultimaLectura = 0;
    mediaLuz = realizaMediaLuz();
    if(lumIzq < mediaLuz-80 && lumDer < mediaLuz-80 && hist.idActual >= 5){
      guardarLuz();
      return LUMIN_MENOR_QUE_ANTES;
    }else if(lumIzq > lumin_sufic && lumDer > lumin_sufic){
      guardarLuz();
      return LUMIN_SUFICIENTE;
    }else if(diferenciaLum < -100){
      guardarLuz();
      return LUMIN_DER_MAYOR;
    }else if(diferenciaLum > 100){
      guardarLuz();
      return LUMIN_IZQ_MAYOR;
    }else{
      guardarLuz();
      return LUMIN_IGUAL_IZQ_DER;
    }
  }else if(ultimaLectura<500 && lumIzq > lumin_sufic && lumDer > lumin_sufic){
    guardarLuz();
    return LUMIN_LEIDA_RECIENTEMENTE_SUFICIENTE;
  }else {
    guardarLuz();
    return LUMIN_LEIDA_RECIENTEMENTE_BAJA;
  }
}

int realizaMediaLuz(){
  int media = 0;
  for(int i=0;i<hist.idActual+1;i++){
    media+=hist.array[i];
  }
  media = media/hist.idActual+1;
  return media;
}

void guardarLuz(){
  if(hist.idActual == 10){
    hist = {{},0};
  }
  if(lumIzq>=lumDer){
    hist.array[hist.idActual] = lumIzq;
  }else{
    hist.array[hist.idActual] = lumDer;
  }
  hist.idActual++;
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


void sendPostSensor(int idsensor, float value, float accuracy){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/putSensorValue", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["idsensor"] = idsensor;
    doc["value"] = value;
    doc["accuracy"] = accuracy;

    String output;
    serializeJson(doc, output);
    int httpCode = http.PUT(output);


    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);
  }
}

void sendPostMotor(int idmotor, float value){
  if (WiFi.status() == WL_CONNECTED){
    HTTPClient http;
    http.begin(client, SERVER_IP, SERVER_PORT, "/api/putMotorValue", true);
    http.addHeader("Content-Type", "application/json");

    const size_t capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 60;
    DynamicJsonDocument doc(capacity);
    doc["value"] = value;
    doc["idmotor"] = idmotor;

    String output;
    serializeJson(doc, output);

    int httpCode = http.PUT(output);

    Serial.println("Response code: " + httpCode);

    String payload = http.getString();

    Serial.println("Resultado: " + payload);

  }
}
