/*
Navicat MySQL Data Transfer

Source Server         : 192.168.99.20
Source Server Version : 50635
Source Host           : 192.168.99.20:3306
Source Database       : jigsaw

Target Server Type    : MYSQL
Target Server Version : 50635
File Encoding         : 65001

Date: 2017-11-01 21:35:21
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_login
-- ----------------------------
DROP TABLE IF EXISTS `user_login`;
CREATE TABLE `user_login` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `id_type` tinyint(4) DEFAULT NULL COMMENT '标识类型',
  `login_id` varchar(50) DEFAULT NULL COMMENT '登录标识（手机号、邮箱、昵称）',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登陆标识';
