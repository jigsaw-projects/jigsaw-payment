/*
Navicat MySQL Data Transfer

Source Server         : 192.168.99.20
Source Server Version : 50635
Source Host           : 192.168.99.20:3306
Source Database       : jigsaw

Target Server Type    : MYSQL
Target Server Version : 50635
File Encoding         : 65001

Date: 2017-11-01 21:35:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for entity_customer
-- ----------------------------
DROP TABLE IF EXISTS `entity_customer`;
CREATE TABLE `entity_customer` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `cust_id` bigint(20) NOT NULL COMMENT '客户ID',
  `cust_name` varchar(50) DEFAULT NULL,
  `gender_code` tinyint(4) DEFAULT NULL COMMENT '性别代码',
  `auth_stat` tinyint(4) DEFAULT NULL COMMENT '实名状态',
  `auth_time` datetime DEFAULT NULL COMMENT '实名认证时间 (最后一次客户认证时间)',
  `cloz_date` datetime DEFAULT NULL COMMENT '销户日期',
  `cloz_rsn` varchar(255) DEFAULT NULL COMMENT '销户原因',
  `user_count_num` int(11) DEFAULT '0' COMMENT '用户数',
  `use_valid_num` int(11) DEFAULT '0' COMMENT '有效用户数',
  `crt_user` int(11) DEFAULT '0' COMMENT '创建用户数',
  `upd_user` int(11) DEFAULT '0' COMMENT '修改用户数',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_cust_id` (`cust_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='个人客户基本信息(实体)';
