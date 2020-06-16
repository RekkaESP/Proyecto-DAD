CREATE DATABASE  IF NOT EXISTS `dad_sunbot` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `dad_sunbot`;
-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: localhost    Database: dad_sunbot
-- ------------------------------------------------------
-- Server version	8.0.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `device`
--

DROP TABLE IF EXISTS `device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device` (
  `iddevice` int NOT NULL AUTO_INCREMENT COMMENT 'El identificador numérico del dispositivo',
  `ip` varchar(45) DEFAULT NULL COMMENT 'La dirección IP del dispositivo',
  `name` varchar(45) DEFAULT NULL COMMENT 'Nombre del dispositivo',
  `initialTimestamp` bigint NOT NULL COMMENT 'Fecha y hora (en milisegundos) en la que el dispositivo se introdujo en la base de datos.',
  PRIMARY KEY (`iddevice`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `device`
--

LOCK TABLES `device` WRITE;
/*!40000 ALTER TABLE `device` DISABLE KEYS */;
INSERT INTO `device` VALUES (1,'192.168.0.1','ESP8266',1585223531);
/*!40000 ALTER TABLE `device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `motor`
--

DROP TABLE IF EXISTS `motor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `motor` (
  `idmotor` int NOT NULL AUTO_INCREMENT COMMENT 'El identificador numérico del motor.',
  `wheel` int NOT NULL COMMENT 'Ruedas que controla el motor\n0 -> delante 1 -> detras\\n',
  `iddevice` int NOT NULL COMMENT 'El identificador numérico del dispositivo al que pertenece el motor.',
  PRIMARY KEY (`idmotor`),
  KEY `motor_device_idx` (`iddevice`),
  CONSTRAINT `motor_device` FOREIGN KEY (`iddevice`) REFERENCES `device` (`iddevice`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `motor`
--

LOCK TABLES `motor` WRITE;
/*!40000 ALTER TABLE `motor` DISABLE KEYS */;
INSERT INTO `motor` VALUES (0,0,1),(1,1,1),(3,2,1);
/*!40000 ALTER TABLE `motor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `motor_value`
--

DROP TABLE IF EXISTS `motor_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `motor_value` (
  `idmotor_value` int NOT NULL AUTO_INCREMENT COMMENT 'El identificador numérico del valor de un motor.',
  `value` float NOT NULL COMMENT 'El valor numérico obtenido del estado del motor. 0->Hacia delante, 1->Hacia atrás',
  `timestamp` bigint DEFAULT NULL COMMENT 'Fecha y hora (en milisegundos) en la que se guardó el valor.',
  `idmotor` int NOT NULL COMMENT 'El identificador numérico del motor al que pertenece el valor.',
  PRIMARY KEY (`idmotor_value`),
  KEY `motor_value_motor_idx` (`idmotor`),
  CONSTRAINT `motor_value_motor` FOREIGN KEY (`idmotor`) REFERENCES `motor` (`idmotor`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=19557 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `motor_value`
--

LOCK TABLES `motor_value` WRITE;
/*!40000 ALTER TABLE `motor_value` DISABLE KEYS */;
/*!40000 ALTER TABLE `motor_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensor`
--

DROP TABLE IF EXISTS `sensor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensor` (
  `idsensor` int NOT NULL AUTO_INCREMENT COMMENT 'El identificador numérico del sensor.',
  `type` varchar(45) NOT NULL COMMENT 'Tipo de sensor: humidity, temperature, luminosity.',
  `name` varchar(45) NOT NULL COMMENT 'Nombre que recibe el sensor.',
  `iddevice` int DEFAULT NULL COMMENT 'El identificador numérico del dispositivo al que pertenece el sensor.',
  PRIMARY KEY (`idsensor`),
  KEY `sensor_device_idx` (`iddevice`),
  CONSTRAINT `sensor_device` FOREIGN KEY (`iddevice`) REFERENCES `device` (`iddevice`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensor`
--

LOCK TABLES `sensor` WRITE;
/*!40000 ALTER TABLE `sensor` DISABLE KEYS */;
INSERT INTO `sensor` VALUES (0,'luminosityI','Sensor de Luminosidad izqda',1),(1,'luminosityD','Sensor de Luminosidad derecha',1),(2,'humidity','Sensor de Humedad',1);
/*!40000 ALTER TABLE `sensor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sensor_value`
--

DROP TABLE IF EXISTS `sensor_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensor_value` (
  `idsensor_value` int NOT NULL AUTO_INCREMENT COMMENT 'El identificador numÃ©rico de un valor de cierto sensor.',
  `idsensor` int NOT NULL COMMENT 'El identificador numÃ©rico del sensor al que pertenece el valor.',
  `value` float NOT NULL COMMENT 'Valor numÃ©rico obtenido de la mediciÃ³n.\nHumedad: Porcentaje (%)\nTemperatura: grados (Âº)\nLuminosidad: nivel de luminosidad(0-1024)',
  `accuracy` float NOT NULL COMMENT 'PrecisiÃ³n de la mediciÃ³n.',
  `timestamp` bigint DEFAULT NULL COMMENT 'Fecha y hora (en milisegundos) en la que se guardÃ³ el valor.',
  PRIMARY KEY (`idsensor_value`),
  KEY `sensor_value_sensor_idx` (`idsensor`),
  CONSTRAINT `sensor_value_sensor` FOREIGN KEY (`idsensor`) REFERENCES `sensor` (`idsensor`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6117 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sensor_value`
--

LOCK TABLES `sensor_value` WRITE;
/*!40000 ALTER TABLE `sensor_value` DISABLE KEYS */;
/*!40000 ALTER TABLE `sensor_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'dad_sunbot'
--
/*!50003 DROP PROCEDURE IF EXISTS `borra_relleno` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `borra_relleno`()
DELETE FROM sensor_value
WHERE accuracy = 0 ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-06-16 18:49:17
