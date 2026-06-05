# Host: localhost  (Version 5.7.30-log)
# Date: 2025-12-24 22:24:05
# Generator: MySQL-Front 6.1  (Build 1.26)


#
# Structure for table "t_category"
#

DROP TABLE IF EXISTS `t_category`;
CREATE TABLE `t_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

#
# Data for table "t_category"
#

INSERT INTO `t_category` VALUES (3,'旅游'),(4,'学习'),(5,'生活'),(6,'穿搭'),(8,'美食'),(10,'运动'),(11,'养生'),(12,'电竞');

#
# Structure for table "t_user"
#

DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(30) NOT NULL,
  `password` varchar(64) NOT NULL,
  `role` tinyint(4) DEFAULT '1' COMMENT '1用户 2管理员',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;

#
# Data for table "t_user"
#

INSERT INTO `t_user` VALUES (5,'user1','$2a$10$ui5SCJZM45RWqUcAdOgt3.J1WBSqDtd.0KuTc3m3d3D3IpOO2H0Vu',1,'2025-12-20 17:33:59'),(8,'user2','$2a$10$2NSvLMA2dSVk9D6RY9SWYuCCD8ohxmGtFg9I1PYnsf87Ssb9Y4MKi',1,'2025-12-22 11:59:04'),(9,'user3','$2a$10$wvcclSIg2ryV.XhY7wS3Le1HB25S.FSVGYM8rMmhYjc4fITcYx68W',1,'2025-12-22 11:59:10'),(11,'user5','$2a$10$1TemZ/KkaGMNGR0LW7SB/.nrmxv3UJWY8UjCeiw3zzzWi67YLrYKO',1,'2025-12-22 11:59:18'),(12,'user6','$2a$10$72ReIpAfZ..RnsgRwMKi7OHMuKn6TEkSonPDWCTRtkUVbMjOzDR/y',1,'2025-12-22 11:59:22'),(13,'user4','$2a$10$CIrx1yiKCXXXmryTErcMI.PBToctvmIq1PNv4c/OnPJsh8QCM0CAa',1,'2025-12-22 12:01:50'),(15,'admin','$2a$10$9W47h.b/QuGkhYkqWngk1epen0sWFdr8rQ3pHG5Ks2IiJFQn8HSg6',2,'2025-12-24 22:18:21');

#
# Structure for table "t_article"
#

DROP TABLE IF EXISTS `t_article`;
CREATE TABLE `t_article` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `content` longtext,
  `category_id` bigint(20) DEFAULT NULL,
  `author_id` bigint(20) DEFAULT NULL,
  `view_num` bigint(20) DEFAULT '0',
  `status` tinyint(4) DEFAULT '0' COMMENT '0待审 1通过 2拒绝',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`),
  KEY `author_id` (`author_id`),
  CONSTRAINT `t_article_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `t_category` (`id`),
  CONSTRAINT `t_article_ibfk_2` FOREIGN KEY (`author_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4;

#
# Data for table "t_article"
#

INSERT INTO `t_article` VALUES (4,'我爱学习','我特别爱学习1',4,5,105,1,'2025-12-21 15:21:43','2025-12-24 11:53:39'),(5,'我爱养生','枸杞红枣',11,8,0,1,'2025-12-22 12:00:21','2025-12-22 12:03:55'),(6,'我爱旅游','trip',3,9,2,1,'2025-12-22 12:01:15','2025-12-24 21:51:35'),(7,'我爱生活','life',5,13,1,1,'2025-12-22 12:02:19','2025-12-24 21:51:32'),(8,'我爱电竞','game',12,11,6,1,'2025-12-22 12:02:44','2025-12-24 21:50:54'),(9,'我爱穿搭','cloth',6,12,20,1,'2025-12-22 12:03:31','2025-12-24 21:50:51'),(12,'你好','hello',11,5,1,1,'2025-12-24 20:38:42','2025-12-24 21:50:40');

#
# Structure for table "t_comment"
#

DROP TABLE IF EXISTS `t_comment`;
CREATE TABLE `t_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `article_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `content` varchar(500) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `article_id` (`article_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `t_comment_ibfk_1` FOREIGN KEY (`article_id`) REFERENCES `t_article` (`id`),
  CONSTRAINT `t_comment_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4;

#
# Data for table "t_comment"
#

INSERT INTO `t_comment` VALUES (32,8,5,'支持','2025-12-24 18:59:03');
