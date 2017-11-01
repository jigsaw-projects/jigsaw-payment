/*
Navicat MySQL Data Transfer

Source Server         : 192.168.99.20
Source Server Version : 50635
Source Host           : 192.168.99.20:3306
Source Database       : jigsaw

Target Server Type    : MYSQL
Target Server Version : 50635
File Encoding         : 65001

Date: 2017-11-01 21:35:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sms_verif
-- ----------------------------
DROP TABLE IF EXISTS `sms_verif`;
CREATE TABLE `sms_verif` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `msg_type_ver_id` bigint(20) NOT NULL COMMENT '短息验证码信息ID ',
  `phone_no` char(11) NOT NULL COMMENT '目标手机号码',
  `cd_key` char(6) NOT NULL COMMENT '验证码回执编号',
  `ver_code` char(6) NOT NULL COMMENT '短信验证码',
  `auth_stat` tinyint(4) NOT NULL DEFAULT '0' COMMENT '鉴权状态',
  `pre_auth_stat` tinyint(4) NOT NULL DEFAULT '0' COMMENT '预鉴权状态',
  `exp_time` datetime NOT NULL COMMENT '验证码的有效截止时间',
  `msg_type` tinyint(4) NOT NULL COMMENT '短信类型',
  `sys_id` tinyint(4) NOT NULL COMMENT '请求系统编码',
  `version` int(11) DEFAULT '1' COMMENT '版本号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信验证码信息表';
