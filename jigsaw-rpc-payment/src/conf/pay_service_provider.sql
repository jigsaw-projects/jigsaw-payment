/*
Navicat MySQL Data Transfer

Source Server         : online-qiyi_pay
Source Server Version : 50626
Source Host           : bj.qiyipay.r.qiyi.db:6217
Source Database       : qiyi_pay

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2017-08-17 17:33:39
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pay_partner
-- ----------------------------
DROP TABLE IF EXISTS `pay_partner`;
CREATE TABLE `pay_partner` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `type` decimal(19,0) NOT NULL,
  `priority` int(9) DEFAULT NULL,
  `email` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `contact` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_user` decimal(19,0) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_user` decimal(19,0) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(9) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pay_partner_name_uni_inx` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=306 DEFAULT CHARSET=utf8;
