/*
Navicat MySQL Data Transfer

Source Server         : online-qiyi_pay
Source Server Version : 50626
Source Host           : bj.qiyipay.r.qiyi.db:6217
Source Database       : qiyi_pay

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2017-08-17 17:34:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pay_payment_service
-- ----------------------------
DROP TABLE IF EXISTS `pay_payment_service`;
CREATE TABLE `pay_payment_service` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `description` varchar(256) DEFAULT NULL COMMENT '备注说明',
  `type` int(9) NOT NULL,
  `class_name` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `pay_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `contract_id` decimal(19,0) DEFAULT NULL,
  `partner_id` decimal(19,0) DEFAULT NULL,
  `rate_info_id` decimal(19,0) DEFAULT NULL,
  `status` int(9) NOT NULL,
  `create_user` decimal(19,0) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_user` decimal(19,0) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(9) NOT NULL,
  `total_money` decimal(19,0) NOT NULL,
  `total_rate_money` decimal(19,0) NOT NULL,
  `payment_service_type` int(6) DEFAULT NULL,
  `dut_type` int(10) DEFAULT NULL COMMENT '支付类型(支付，签约，代扣，签约支付）',
  `is_third_channel` smallint(2) DEFAULT NULL COMMENT '是否第三方通道1是0否',
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_name_unq_inx` (`name`),
  UNIQUE KEY `service_pay_code_unq_inx` (`pay_code`)
) ENGINE=InnoDB AUTO_INCREMENT=454 DEFAULT CHARSET=utf8;
