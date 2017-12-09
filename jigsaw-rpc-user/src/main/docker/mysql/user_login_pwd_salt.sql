/*
Navicat MySQL Data Transfer

Source Server         : 192.168.99.20
Source Server Version : 50635
Source Host           : 192.168.99.20:3306
Source Database       : jigsaw

Target Server Type    : MYSQL
Target Server Version : 50635
File Encoding         : 65001

Date: 2017-11-01 21:48:03
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_login_pwd_salt
-- ----------------------------
DROP TABLE IF EXISTS `user_login_pwd_salt`;
CREATE TABLE `user_login_pwd_salt` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `salt` char(8) DEFAULT NULL COMMENT '加密盐值',
  `version` int(12) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登陆密码盐值表（将密文与盐值分离存储）';
