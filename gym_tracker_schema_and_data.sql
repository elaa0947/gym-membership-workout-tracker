-- MySQL dump 10.13  Distrib 26.7.0, for macos26.6 (arm64)
--
-- Host: localhost    Database: gym_tracker
-- ------------------------------------------------------
-- Server version	26.7.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `attendance_records`
--

DROP TABLE IF EXISTS `attendance_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `attendance_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `attendance_date` date NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_attendance_member_date` (`member_id`,`attendance_date`),
  CONSTRAINT `fk_attendance_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `attendance_records`
--

LOCK TABLES `attendance_records` WRITE;
/*!40000 ALTER TABLE `attendance_records` DISABLE KEYS */;
INSERT INTO `attendance_records` VALUES (1,2,'2026-11-04'),(9,2,'2026-11-05'),(13,2,'2026-11-07'),(22,4,'2026-11-07'),(10,6,'2026-11-05'),(11,7,'2026-11-05');
/*!40000 ALTER TABLE `attendance_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `members`
--

DROP TABLE IF EXISTS `members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `members` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) NOT NULL,
  `fitness_goal` varchar(255) DEFAULT NULL,
  `height` double DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `onboarding_completed` bit(1) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `weight` double DEFAULT NULL,
  `membership_plan` varchar(50) DEFAULT NULL,
  `membership_status` varchar(50) DEFAULT 'NONE',
  `membership_duration_months` int DEFAULT NULL,
  `membership_start_date` date DEFAULT NULL,
  `membership_end_date` date DEFAULT NULL,
  `membership_price` double DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'MEMBER',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9d30a9u1qpg8eou0otgkwrp5d` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `members`
--

LOCK TABLES `members` WRITE;
/*!40000 ALTER TABLE `members` DISABLE KEYS */;
INSERT INTO `members` VALUES (1,'2026-10-13 23:34:41.229719','jagantp@gmail.com','Muscle Gain',177,'Jagannath',_binary '','$2a$10$jbk3Bu2vM3/m4ArDLn1oM.rH5wmSEmfXP/ToYBKm3CrnAFTUSQ0tK','8610339463',63.2,NULL,'NONE',NULL,NULL,NULL,NULL,'MEMBER'),(2,'2026-11-02 09:11:30.442576','testmember@example.com','Muscle Gain',176,'Test Member',_binary '','$2a$10$93T79/2flQfzbzchHAWHGu.bgufBu8TL6mw/la99GRUz1Y1v8OQBO','9876543210',62,'PRO','ACTIVE',3,'2026-11-04','2027-02-04',1999,'MEMBER'),(3,'2026-11-04 22:00:48.874070','jag@gmail.com',NULL,NULL,'jag',_binary '\0','$2a$10$UinzQlF4G/97h/3PU3kzV.9yP3Fj2I2clgV87WQ9yQFnz2lj7JBk.','8610339463',NULL,NULL,'NONE',NULL,NULL,NULL,NULL,'MEMBER'),(4,'2026-11-04 22:23:35.293639','greentest@example.com','Muscle Building',175,'Green Champion',_binary '','$2a$10$YHR5/n0jRnjxFeZPP2pWS.c6wvQ6jhImmJEaHI0tXrCy2.fikfWTO','9876543210',73.5,'PRO','ACTIVE',3,'2026-11-07','2027-02-07',1999,'MEMBER'),(5,'2026-11-05 00:33:30.932253','qa_1793819010@test.com',NULL,NULL,'QA Tester',_binary '\0','$2a$10$GpOPczb9dgXzTVzAMlQ/ke6Yaf0Mi3TCrXXiGkc5LQNAs.UK4XUuq','9876543210',NULL,'PRO','NONE',NULL,NULL,NULL,NULL,'MEMBER'),(6,'2026-11-05 00:35:56.782250','suite_1793819156@test.com','Strength',180,'Suite Tester Updated',_binary '','$2a$10$2oTx.G9e/gvjJTwNrc4aq.ozFi5RrWhOUcavlj8Exebv.Eaq0pk/y','9876543211',76.2,'ELITE','ACTIVE',6,'2026-11-05','2027-05-05',3499,'MEMBER'),(7,'2026-11-05 00:36:49.731068','suite_1793819209@test.com','Strength',180,'Suite Tester Updated',_binary '','$2a$10$AM80hM1/g80BIFVDNjgaxOvFgs0yyV/a0WYTcOcUp17XYQp2POF66','9876543211',76.2,'ELITE','ACTIVE',6,'2026-11-05','2027-05-05',3499,'MEMBER'),(8,'2026-11-07 01:43:10.111000','admin@corelygym.com','Operations & Management',NULL,'Corely Administrator',_binary '','$2a$10$3l8I3uCvt7gdy.hPuSX7ce/S1L9tIinS56t7mkAzA94BQDHTci/4a','9876543210',NULL,'ELITE','ACTIVE',NULL,NULL,NULL,NULL,'ADMIN'),(9,'2026-11-07 19:45:46.888575','vercel_user_1@test.com',NULL,NULL,'Vercel User',_binary '\0','$2a$10$SuNcqzAjp5syISOKi7Yhp.z/m0ZuOHpB6Rp.ZEuJORLsiVQukue5y','9876543210',NULL,NULL,'NONE',NULL,NULL,NULL,NULL,'MEMBER');
/*!40000 ALTER TABLE `members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `weight_entries`
--

DROP TABLE IF EXISTS `weight_entries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `weight_entries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `measurement_date` date NOT NULL,
  `weight` double NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_weight_member_date` (`member_id`,`measurement_date`),
  CONSTRAINT `fk_weight_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `weight_entries`
--

LOCK TABLES `weight_entries` WRITE;
/*!40000 ALTER TABLE `weight_entries` DISABLE KEYS */;
INSERT INTO `weight_entries` VALUES (1,2,'2026-11-04',62),(8,6,'2026-11-05',76.2),(9,7,'2026-11-05',76.2),(19,4,'2026-11-07',73.5);
/*!40000 ALTER TABLE `weight_entries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workout_exercises`
--

DROP TABLE IF EXISTS `workout_exercises`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workout_exercises` (
  `workout_id` bigint NOT NULL,
  `exercise_name` varchar(120) NOT NULL,
  KEY `fk_exercise_workout` (`workout_id`),
  CONSTRAINT `fk_exercise_workout` FOREIGN KEY (`workout_id`) REFERENCES `workout_sessions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workout_exercises`
--

LOCK TABLES `workout_exercises` WRITE;
/*!40000 ALTER TABLE `workout_exercises` DISABLE KEYS */;
INSERT INTO `workout_exercises` VALUES (1,'Push-ups'),(1,'Bodyweight squats'),(1,'Plank'),(2,'Bodyweight squats'),(2,'Push-ups'),(2,'Dumbbell rows'),(2,'Plank'),(2,'Brisk treadmill walk'),(12,'Bodyweight squats'),(12,'Push-ups'),(12,'Dumbbell rows'),(12,'Plank'),(12,'Brisk treadmill walk'),(13,'Barbell Squats'),(13,'Deadlifts'),(13,'Overhead Press'),(15,'Barbell Squats'),(15,'Deadlifts'),(15,'Overhead Press'),(17,'Bodyweight squats'),(17,'Push-ups'),(17,'Dumbbell rows'),(17,'Plank'),(17,'Brisk treadmill walk'),(26,'Bodyweight squats'),(27,'Bodyweight squats'),(27,'Push-ups'),(27,'Dumbbell rows'),(27,'Plank'),(27,'Brisk treadmill walk');
/*!40000 ALTER TABLE `workout_exercises` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `workout_sessions`
--

DROP TABLE IF EXISTS `workout_sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `workout_sessions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `session_date` date NOT NULL,
  `saved_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_workout_member_date` (`member_id`,`session_date`),
  CONSTRAINT `fk_workout_member` FOREIGN KEY (`member_id`) REFERENCES `members` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `workout_sessions`
--

LOCK TABLES `workout_sessions` WRITE;
/*!40000 ALTER TABLE `workout_sessions` DISABLE KEYS */;
INSERT INTO `workout_sessions` VALUES (1,2,'2026-11-04','2026-11-04 21:49:50.526014'),(2,2,'2026-11-04','2026-11-04 22:01:54.582842'),(12,2,'2026-11-05','2026-11-05 00:25:56.908148'),(13,6,'2026-11-05','2026-11-05 00:35:57.127185'),(15,7,'2026-11-05','2026-11-05 00:36:50.059541'),(17,2,'2026-11-07','2026-11-07 01:06:44.942872'),(26,4,'2026-11-07','2026-11-07 19:46:25.068163'),(27,2,'2026-11-07','2026-11-07 19:53:29.380295');
/*!40000 ALTER TABLE `workout_sessions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-07 20:02:52
