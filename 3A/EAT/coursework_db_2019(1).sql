-- MySQL dump 10.13  Distrib 5.1.61, for redhat-linux-gnu (x86_64)
--
-- Host: localhost    Database: bastinl_db
-- ------------------------------------------------------
-- Server version	5.1.61

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clients` (
  `clientid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL,
  `password` varchar(16) NOT NULL,
  PRIMARY KEY (`clientid`)
) ENGINE=MyISAM AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
INSERT INTO `clients` VALUES (1,'snowbunny','snowbunny'),(2,'skiwidow','skiwidow'),(3,'scaredofsnow','scaredofsnow'),(4,'zoomer77','zoomer77'),(5,'doctorluz','doctorluz'),(6,'CornMeister','CornMeister'),(7,'masterJones','masterJones'),(8,'LegInTraction','LegInTraction'),(9,'Harperman','Harperman'),(10,'Harperman','Harperman');
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lessons`
--

DROP TABLE IF EXISTS `lessons`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lessons` (
  `description` varchar(35) NOT NULL,
  `level` smallint(6) DEFAULT NULL,
  `startDateTime` datetime NOT NULL,
  `endDateTime` datetime NOT NULL,
  `lessonid` varchar(6) NOT NULL,
  PRIMARY KEY (`lessonid`)
) ENGINE=MyISAM AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lessons`
--

LOCK TABLES `lessons` WRITE;
/*!40000 ALTER TABLE `lessons` DISABLE KEYS */;
INSERT INTO `lessons` VALUES ('Snowboarding for dummies',1,'2010-12-01 09:00:00','2010-12-01 12:00:00','L1'),('Advanced Carving Techniques',3,'2010-12-02 09:00:00','2010-12-02 14:00:00','L2'),('How to not fall off the draglift',1,'2010-12-02 14:00:00','2010-12-02 16:00:00','L3'),('Gnarliness Extreeeeeeeme',3,'2010-12-03 10:00:00','2010-12-03 13:00:00','L4'),('Parallel turns',2,'2010-12-04 09:30:00','2010-12-04 12:30:00','L5'),('How to splint a broken leg with a s',1,'2010-12-04 13:00:00','2010-12-04 15:30:00','L6'),('Cross-country techniques',2,'2010-12-05 09:30:00','2010-12-05 12:30:00','L7'),('Aerobatics',3,'2010-12-05 13:30:00','2010-12-05 16:30:00','L8'),('Intermediate Slalom ',2,'2010-12-06 09:30:00','2010-12-06 12:30:00','L9');
/*!40000 ALTER TABLE `lessons` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lessons_booked`
--

DROP TABLE IF EXISTS `lessons_booked`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lessons_booked` (
  `clientid` int(11) NOT NULL DEFAULT '1',
  `lessonid` varchar(6) NOT NULL DEFAULT '',
  PRIMARY KEY (`clientid`,`lessonid`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'bastinl_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-11-09 11:51:43
