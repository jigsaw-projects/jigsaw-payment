/*
Navicat MySQL Data Transfer

Source Server         : 192.168.99.20
Source Server Version : 50635
Source Host           : 192.168.99.20:3306
Source Database       : jigsaw

Target Server Type    : MYSQL
Target Server Version : 50635
File Encoding         : 65001

Date: 2017-11-01 21:35:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for entity_user
-- ----------------------------
DROP TABLE IF EXISTS `entity_user`;
CREATE TABLE `entity_user` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `user_name` varchar(128) NOT NULL COMMENT '用户昵称',
  `user_stat` tinyint(4) NOT NULL DEFAULT '0' COMMENT '用户状态',
  `safe_phone` char(11) DEFAULT NULL COMMENT '安全手机',
  `safe_mail` varchar(128) DEFAULT NULL COMMENT '安全邮箱',
  `idf_flag` tinyint(4) DEFAULT NULL COMMENT '用户是否已经识别为客户',
  `user_safe_stat` tinyint(4) DEFAULT NULL COMMENT '用户安全状态',
  `logphone_flag` tinyint(4) DEFAULT NULL COMMENT '是否手机号认证',
  `logphone_time` datetime DEFAULT NULL COMMENT '手机号认证',
  `reg_date` datetime DEFAULT NULL COMMENT '开户日期',
  `act_date` datetime DEFAULT NULL COMMENT '激活日期',
  `clz_date` datetime DEFAULT NULL COMMENT '销户日期',
  `clz_reson` varchar(255) DEFAULT NULL COMMENT '销户原因',
  `cha_type` tinyint(4) DEFAULT NULL COMMENT '注册渠道',
  `perview_info` varchar(255) DEFAULT NULL COMMENT '预留信息',
  `lst_log_ip` varchar(32) DEFAULT NULL COMMENT '最后登录IP',
  `lst_log_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_user_id` (`user_id`) USING BTREE,
  UNIQUE KEY `idx_username` (`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户基本信息(实体)';
