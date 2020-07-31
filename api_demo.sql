/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80020
Source Host           : localhost:3306
Source Database       : api_demo

Target Server Type    : MYSQL
Target Server Version : 80020
File Encoding         : 65001

Date: 2020-07-31 12:01:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for mch
-- ----------------------------
DROP TABLE IF EXISTS `mch`;
CREATE TABLE `mch` (
  `id` int NOT NULL,
  `access_key` varchar(100) DEFAULT NULL,
  `public_key` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of mch
-- ----------------------------
INSERT INTO `mch` VALUES ('1', 'demo', 'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCS9GSMr6jVqnRP3erPypmeQNOVzCmtfxf1gaU4SCrjSRoQhomJRl9J88SP/7h6V8xEswCyd0bwuQh0bYdhwxzLKf2N560Q2ywcJZopmj/XGfWnCIbFt77A7LElCTx1aGrELntz4zft9nQnX81J/A/9O51ApfsvoTmECEjSc2dlSQIDAQAB');
