-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: lol_app
-- ------------------------------------------------------
-- Server version	8.0.46

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
-- Table structure for table `champion_lanes`
--

DROP TABLE IF EXISTS `champion_lanes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `champion_lanes` (
  `champion_name` varchar(50) NOT NULL,
  `lane` varchar(20) NOT NULL,
  PRIMARY KEY (`champion_name`,`lane`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `champion_lanes`
--

LOCK TABLES `champion_lanes` WRITE;
/*!40000 ALTER TABLE `champion_lanes` DISABLE KEYS */;
INSERT INTO `champion_lanes` VALUES ('Aatrox','top'),('Ahri','middle'),('Akali','middle'),('Akali','top'),('Alistar','utility'),('Ambessa','top'),('Amumu','jungle'),('Anivia','middle'),('Annie','middle'),('Aphelios','bottom'),('Ashe','bottom'),('AurelionSol','middle'),('Azir','middle'),('Bard','utility'),('BelVeth','jungle'),('Blitzcrank','utility'),('Brand','utility'),('Braum','utility'),('Briar','jungle'),('Caitlyn','bottom'),('Camille','top'),('Cassiopeia','middle'),('ChoGath','top'),('Corki','middle'),('Darius','top'),('Diana','jungle'),('Draven','bottom'),('DrMundo','top'),('Ekko','jungle'),('Ekko','middle'),('Elise','jungle'),('Ezreal','bottom'),('Fiddlesticks','jungle'),('Fiora','top'),('Fizz','middle'),('Galio','middle'),('Gangplank','top'),('Garen','top'),('Gnar','top'),('Gragas','jungle'),('Gragas','top'),('Graves','jungle'),('Gwen','top'),('Hecarim','jungle'),('Hwei','middle'),('Illaoi','top'),('Irelia','middle'),('Irelia','top'),('Ivern','jungle'),('Janna','utility'),('JarvanIV','jungle'),('Jax','top'),('Jayce','middle'),('Jayce','top'),('Jhin','bottom'),('Jinx','bottom'),('KaiSa','bottom'),('Kalista','bottom'),('Karma','utility'),('Karthus','jungle'),('Kassadin','middle'),('Katarina','middle'),('Kayle','top'),('Kayn','jungle'),('Kennen','top'),('KhaZix','jungle'),('Kindred','jungle'),('Kled','top'),('KogMaw','bottom'),('KSante','top'),('LeBlanc','middle'),('LeeSin','jungle'),('Leona','utility'),('Lillia','jungle'),('Lissandra','middle'),('Lucian','bottom'),('Lulu','utility'),('Lux','middle'),('Lux','utility'),('Malphite','top'),('Malzahar','middle'),('MasterYi','jungle'),('Milio','utility'),('MissFortune','bottom'),('Mordekaiser','top'),('Morgana','utility'),('Naafiri','middle'),('Nami','utility'),('Nasus','top'),('Nautilus','utility'),('Neeko','middle'),('Nidalee','jungle'),('Nilah','bottom'),('Nocturne','jungle'),('Nunu','jungle'),('Olaf','top'),('Orianna','middle'),('Ornn','top'),('Pantheon','jungle'),('Pantheon','top'),('Poppy','top'),('Pyke','utility'),('Qiyana','middle'),('Quinn','top'),('Rakan','utility'),('RekSai','jungle'),('Rell','utility'),('Renata','utility'),('Renekton','top'),('Rengar','jungle'),('Riven','top'),('Rumble','top'),('Ryze','middle'),('Samira','bottom'),('Senna','utility'),('Seraphine','utility'),('Sett','top'),('Shaco','jungle'),('Shen','top'),('Shyvana','jungle'),('Singed','top'),('Sion','top'),('Sivir','bottom'),('Skarner','jungle'),('Smolder','bottom'),('Smolder','middle'),('Sona','utility'),('Soraka','utility'),('Swain','middle'),('Swain','utility'),('Sylas','middle'),('Syndra','middle'),('TahmKench','top'),('Taliyah','jungle'),('Talon','middle'),('Taric','utility'),('Teemo','top'),('Thresh','utility'),('Tristana','bottom'),('Tristana','middle'),('Trundle','jungle'),('Tryndamere','top'),('TwistedFate','middle'),('Twitch','bottom'),('Udyr','jungle'),('Urgot','top'),('Varus','bottom'),('Vayne','bottom'),('Veigar','middle'),('VelKoz','middle'),('VelKoz','utility'),('Vi','jungle'),('Viego','jungle'),('Viktor','middle'),('Volibear','jungle'),('Volibear','top'),('Warwick','jungle'),('Warwick','top'),('Xayah','bottom'),('Xerath','middle'),('Xerath','utility'),('XinZhao','jungle'),('Yasuo','middle'),('Yone','middle'),('Yorick','top'),('Yuumi','utility'),('Zac','jungle'),('Zac','top'),('Zed','middle'),('Zeri','bottom'),('Ziggs','bottom'),('Zilean','utility'),('Zoe','middle'),('Zyra','utility');
/*!40000 ALTER TABLE `champion_lanes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `champions`
--

DROP TABLE IF EXISTS `champions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `champions` (
  `champion_id` int NOT NULL,
  `champion_name` varchar(50) NOT NULL,
  `lolalytics_slug` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`champion_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `champions`
--

LOCK TABLES `champions` WRITE;
/*!40000 ALTER TABLE `champions` DISABLE KEYS */;
INSERT INTO `champions` VALUES (1,'アニー','annie'),(2,'オラフ','olaf'),(3,'ガリオ','galio'),(4,'ツイステッド・フェイト','twistedfate'),(5,'シン・ジャオ','xinzhao'),(6,'アーゴット','urgot'),(7,'ルブラン','leblanc'),(8,'ブラッドミア','vladimir'),(9,'フィドルストックス',''),(10,'ケイル','kayle'),(11,'マスター・イー','masteryi'),(12,'アリスター','alistar'),(13,'ライズ','ryze'),(14,'サイオン','sion'),(15,'シヴィア','sivir'),(16,'ソラカ','soraka'),(17,'ティーモ','teemo'),(18,'トリスターナ','tristana'),(19,'ワーウィック','warwick'),(20,'ヌヌ＆ウィルンプ','nunu'),(21,'ミス・フォーチュン','missfortune'),(22,'アッシュ','ashe'),(23,'トリンダメア','tryndamere'),(24,'ジャックス','jax'),(25,'モルガナ','morgana'),(26,'ジリアン','zilean'),(27,'シンジド','singed'),(28,'イブリン','evelynn'),(29,'トゥイッチ','twitch'),(30,'カーサス','karthus'),(31,'チョ＝ガス','chogath'),(32,'アムム','amumu'),(33,'ラムス','rammus'),(34,'アニビア','anivia'),(35,'シャコ','shaco'),(36,'ドクター・ムンド','drmundo'),(37,'ソナ','sona'),(38,'カサディン','kassadin'),(39,'イレリア','irelia'),(40,'ジャンナ','janna'),(41,'ガングプランク','gangplank'),(42,'コーキ','corki'),(43,'カルマ','karma'),(44,'タリック','taric'),(45,'ベイガー','veigar'),(48,'トランドル','trundle'),(50,'スウェイン','swain'),(51,'ケイトリン','caitlyn'),(53,'ブリッツクランク','blitzcrank'),(54,'マルファイト','malphite'),(55,'カタリナ',''),(56,'ノクターン','nocturne'),(57,'マオカイ','maokai'),(58,'レネクトン','renekton'),(59,'ジャーヴァンIV','jarvaniv'),(60,'エリス','elise'),(61,'オリアナ','orianna'),(62,'ウーコン','wukong'),(63,'ブランド','brand'),(64,'リー・シン','leesin'),(67,'ヴェイン','vayne'),(68,'ランブル','rumble'),(69,'カシオペア','cassiopeia'),(72,'スカーナー','skarner'),(74,'ハイマーディンガー','heimerdinger'),(75,'ナスス',''),(76,'ニダリー','nidalee'),(77,'ウディア','udyr'),(78,'ポッピー','poppy'),(79,'グラガス','gragas'),(80,'パンテオン','pantheon'),(81,'エズリアル','ezreal'),(82,'モルデカイザー','mordekaiser'),(83,'ヨリック','yorick'),(84,'アカリ','akali'),(85,'ケネン','kennen'),(86,'ガレン','garen'),(89,'レオナ','leona'),(90,'マルザハール','malzahar'),(91,'タロン','talon'),(92,'リベン',''),(96,'コグ＝マウ','kogmaw'),(98,'シェン','shen'),(99,'ラックス','lux'),(101,'ゼラス','xerath'),(102,'シヴァーナ','shyvana'),(103,'アーリ','ahri'),(104,'グレイブス','graves'),(105,'フィズ','fizz'),(106,'ボリベア','volibear'),(107,'レンガー','rengar'),(110,'ヴァルス','varus'),(111,'ノーチラス','nautilus'),(112,'ビクター','viktor'),(113,'セジュアニ','sejuani'),(114,'フィオラ','fiora'),(115,'ジグス','ziggs'),(117,'ルル','lulu'),(119,'ドレイヴン','draven'),(120,'ヘカリム','hecarim'),(121,'カ＝ジックス','khazix'),(122,'ダリウス','darius'),(126,'ジェイス','jayce'),(127,'リッサンドラ',''),(131,'ダイアナ','diana'),(133,'クイン','quinn'),(134,'シンドラ','syndra'),(136,'オレリオン・ソル','aurelionsol'),(141,'ケイン','kayn'),(142,'ゾーイ','zoe'),(143,'ザイラ','zyra'),(145,'カイ＝サ','kaisa'),(147,'セラフィーン',''),(150,'ナー','gnar'),(154,'ザック','zac'),(157,'ヤスオ','yasuo'),(161,'ヴェル＝コズ','velkoz'),(163,'タリヤ','taliyah'),(164,'カミール','camille'),(166,'アクシャン','akshan'),(200,'ベル＝ヴェス','belveth'),(201,'ブラウム','braum'),(202,'ジン','jhin'),(203,'キンドレッド','kindred'),(221,'ゼリ','zeri'),(222,'ジンクス','jinx'),(223,'タム・ケンチ','tahmkench'),(233,'ブライアー','briar'),(234,'ヴィエゴ','viego'),(235,'セナ','senna'),(236,'ルシアン','lucian'),(238,'ゼド','zed'),(240,'クレッド','kled'),(245,'エコー',''),(246,'キヤナ','qiyana'),(266,'エートロックス','aatrox'),(267,'ナミ','nami'),(268,'アジール','azir'),(350,'ユーミ','yuumi'),(360,'サミーラ','samira'),(412,'スレッシュ','thresh'),(420,'イラオイ','illaoi'),(421,'レク＝サイ',''),(427,'アイバーン','ivern'),(429,'カリスタ','kalista'),(432,'バード','bard'),(497,'ラカン','rakan'),(498,'ザヤ','xayah'),(516,'オーン','ornn'),(517,'サイラス','sylas'),(518,'ニーコ','neeko'),(523,'アフェリオス','aphelios'),(555,'パイク','pyke'),(711,'ヴェックス','vex'),(777,'ヨネ','yone'),(875,'セット','sett'),(876,'リリア','lillia'),(887,'グウェン','gwen'),(888,'レナータ・グラスク','renata'),(895,'ニーラ',''),(897,'カサンテ',''),(901,'スモルダー','smolder'),(902,'ミリオ','milio'),(903,'アンベッサ','ambessa'),(904,'メル',''),(910,'フェイ','hwei'),(950,'ナフィーリ','naafiri');
/*!40000 ALTER TABLE `champions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `global_counters`
--

DROP TABLE IF EXISTS `global_counters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `global_counters` (
  `enemy_champion` varchar(50) NOT NULL,
  `counter_champion` varchar(50) NOT NULL,
  `win_rate` decimal(4,1) NOT NULL,
  PRIMARY KEY (`enemy_champion`,`counter_champion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `global_counters`
--

LOCK TABLES `global_counters` WRITE;
/*!40000 ALTER TABLE `global_counters` DISABLE KEYS */;
INSERT INTO `global_counters` VALUES ('エートロックス','Fiora',52.4),('エートロックス','Irelia',53.8),('エートロックス','Kled',53.1),('エートロックス','Malphite',51.2),('エートロックス','Wukong',51.9),('ケネン','Jayce',51.9),('ケネン','Malphite',52.5),('ケネン','Nasus',54.2),('ケネン','Ornn',53.1),('ケネン','Poppy',53.8),('セット','Aatrox',52.8),('セット','Fiora',51.9),('セット','Jax',52.1),('セット','Renekton',53.5),('セット','Vayne',51.2),('ドクター・ムンド','Aatrox',53.2),('ドクター・ムンド','Camille',52.0),('ドクター・ムンド','Gwen',56.1),('ドクター・ムンド','Illaoi',52.7),('ドクター・ムンド','Kled',54.8),('レネクトン','Garen',53.2),('レネクトン','Illaoi',54.0),('レネクトン','Quinn',52.6),('レネクトン','Urgot',51.8),('レネクトン','Vayne',51.1);
/*!40000 ALTER TABLE `global_counters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `match_results`
--

DROP TABLE IF EXISTS `match_results`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `match_results` (
  `id` int NOT NULL AUTO_INCREMENT,
  `my_champion` varchar(50) NOT NULL,
  `enemy_champion` varchar(50) NOT NULL,
  `kills` int DEFAULT '0',
  `deaths` int DEFAULT '0',
  `assists` int DEFAULT '0',
  `cs` int DEFAULT '0',
  `is_win` tinyint(1) NOT NULL,
  `match_date` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `match_results`
--

LOCK TABLES `match_results` WRITE;
/*!40000 ALTER TABLE `match_results` DISABLE KEYS */;
/*!40000 ALTER TABLE `match_results` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `matchup_notes`
--

DROP TABLE IF EXISTS `matchup_notes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `matchup_notes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `my_champion` varchar(50) NOT NULL,
  `enemy_champion` varchar(50) NOT NULL,
  `note_text` text,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_matchup` (`my_champion`,`enemy_champion`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `matchup_notes`
--

LOCK TABLES `matchup_notes` WRITE;
/*!40000 ALTER TABLE `matchup_notes` DISABLE KEYS */;
/*!40000 ALTER TABLE `matchup_notes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-09 17:22:18
