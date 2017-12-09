/*
Navicat MySQL Data Transfer

Source Server         : 192.168.99.20
Source Server Version : 50635
Source Host           : 192.168.99.20:3306
Source Database       : jigsaw

Target Server Type    : MYSQL
Target Server Version : 50635
File Encoding         : 65001

Date: 2017-11-01 21:47:57
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user_login_pwd
-- ----------------------------
DROP TABLE IF EXISTS `user_login_pwd`;
CREATE TABLE `user_login_pwd` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `pwd_type` tinyint(4) DEFAULT NULL COMMENT '密码类型',
  `pwd_stat` tinyint(4) DEFAULT NULL COMMENT '密码状态',
  `log_pwd` char(32) DEFAULT NULL COMMENT '密码密文',
  `init_pwd` tinyint(4) DEFAULT NULL COMMENT '初始密码标志',
  `cha_pwd` tinyint(4) DEFAULT NULL COMMENT '是否强制修改密码',
  `lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
  `lock_reson` varchar(255) DEFAULT NULL COMMENT '锁定原因',
  `last_update_time` datetime DEFAULT NULL COMMENT '最后修改时间',
  `version` int(12) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登陆密码';
