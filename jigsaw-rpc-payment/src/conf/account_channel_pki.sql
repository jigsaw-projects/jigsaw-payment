/*
Navicat MySQL Data Transfer

Source Server         : bj.payaccount.w.qiyi.db
Source Server Version : 50626
Source Host           : bj.payaccount.w.qiyi.db:8847
Source Database       : pay_account

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2017-08-18 18:50:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for account_channel_pki
-- ----------------------------
DROP TABLE IF EXISTS `account_channel_pki`;
CREATE TABLE `account_channel_pki` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `channel_entity_id` int(11) NOT NULL DEFAULT '0' COMMENT 'pay_partner固定编码',
  `channel_account_no` varchar(128) NOT NULL COMMENT '商户号',
  `type` int(11) NOT NULL COMMENT '文件类型:1.P12;2.CER;3.JKS;4.pem; 5.pfx',
  `status` int(11) NOT NULL COMMENT '状态: 1.有效;2.过期',
  `memo` varchar(512) NOT NULL COMMENT '描述',
  `usage` int(11) NOT NULL COMMENT '用途：1.SSL 2.SIGN 3.ENC 4.CHANNEL_SSL 5.CHANNEL_SIGN 6.CHANNEL_ENC',
  `pass_phrase` varchar(128) NOT NULL COMMENT '私钥密码',
  `data` blob COMMENT '文件内容',
  `digest` varchar(32) NOT NULL COMMENT '校验md5',
  `create_time` timestamp NOT NULL,
  `update_time` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_account` (`channel_entity_id`,`channel_account_no`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;
