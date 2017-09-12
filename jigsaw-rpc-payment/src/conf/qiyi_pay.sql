/*
Navicat MySQL Data Transfer

Source Server         : online-qiyi_pay
Source Server Version : 50626
Source Host           : bj.qiyipay.r.qiyi.db:6217
Source Database       : qiyi_pay

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2017-08-17 17:34:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for a_no
-- ----------------------------
DROP TABLE IF EXISTS `a_no`;
CREATE TABLE `a_no` (
  `uid` varchar(32) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for a_nono
-- ----------------------------
DROP TABLE IF EXISTS `a_nono`;
CREATE TABLE `a_nono` (
  `uid` varchar(32) NOT NULL,
  KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for a_uid
-- ----------------------------
DROP TABLE IF EXISTS `a_uid`;
CREATE TABLE `a_uid` (
  `uid` varchar(32) NOT NULL,
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for b_id
-- ----------------------------
DROP TABLE IF EXISTS `b_id`;
CREATE TABLE `b_id` (
  `uid` varchar(32) NOT NULL,
  KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for b_uid
-- ----------------------------
DROP TABLE IF EXISTS `b_uid`;
CREATE TABLE `b_uid` (
  `uid` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for c_id
-- ----------------------------
DROP TABLE IF EXISTS `c_id`;
CREATE TABLE `c_id` (
  `uid` varchar(32) NOT NULL,
  KEY `uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for c_pro
-- ----------------------------
DROP TABLE IF EXISTS `c_pro`;
CREATE TABLE `c_pro` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(62) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_c_pro_uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=22835 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for c_unscribe_user
-- ----------------------------
DROP TABLE IF EXISTS `c_unscribe_user`;
CREATE TABLE `c_unscribe_user` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(62) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for c_unsubscribe
-- ----------------------------
DROP TABLE IF EXISTS `c_unsubscribe`;
CREATE TABLE `c_unsubscribe` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `uid` varchar(62) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_c_unsubscribe_uid` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=23427 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dba_1
-- ----------------------------
DROP TABLE IF EXISTS `dba_1`;
CREATE TABLE `dba_1` (
  `id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mobile_except
-- ----------------------------
DROP TABLE IF EXISTS `mobile_except`;
CREATE TABLE `mobile_except` (
  `id` bigint(19) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mobile_exception
-- ----------------------------
DROP TABLE IF EXISTS `mobile_exception`;
CREATE TABLE `mobile_exception` (
  `id` bigint(19) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mobile_sub_temp
-- ----------------------------
DROP TABLE IF EXISTS `mobile_sub_temp`;
CREATE TABLE `mobile_sub_temp` (
  `mobile` bigint(20) DEFAULT NULL,
  `province` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for mobile_temp
-- ----------------------------
DROP TABLE IF EXISTS `mobile_temp`;
CREATE TABLE `mobile_temp` (
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  KEY `mobile_idx` (`mobile`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_accesser
-- ----------------------------
DROP TABLE IF EXISTS `pay_accesser`;
CREATE TABLE `pay_accesser` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `contract_id` int(20) NOT NULL,
  `gate_way` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `key_id` decimal(19,0) DEFAULT NULL,
  `last_key` decimal(19,0) DEFAULT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `partner_id` decimal(19,0) NOT NULL,
  `status` bigint(10) NOT NULL,
  `user_id` decimal(19,0) NOT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `pay_rate_info_id` decimal(19,0) DEFAULT NULL,
  `alias_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `auth_tags` bigint(20) DEFAULT NULL COMMENT '二进制授权码',
  `accesser_type` int(10) DEFAULT NULL COMMENT '接入方大类',
  `is_partner` smallint(2) DEFAULT NULL COMMENT '是否业务方0否1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=484 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_accesser_payment
-- ----------------------------
DROP TABLE IF EXISTS `pay_accesser_payment`;
CREATE TABLE `pay_accesser_payment` (
  `accesser_id` decimal(19,0) NOT NULL,
  `payment_id` decimal(19,0) NOT NULL,
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8913 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_accesser_paytype_payrate
-- ----------------------------
DROP TABLE IF EXISTS `pay_accesser_paytype_payrate`;
CREATE TABLE `pay_accesser_paytype_payrate` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  `accesser_id` decimal(19,0) DEFAULT NULL,
  `pay_rate_id` decimal(19,0) DEFAULT NULL,
  `pay_type_id` decimal(19,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_appstore_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_appstore_order`;
CREATE TABLE `pay_appstore_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transaction_id` bigint(20) DEFAULT NULL,
  `order_code` varchar(32) DEFAULT NULL,
  `receipt_data` longtext,
  PRIMARY KEY (`id`),
  KEY `IDX_APPSTORE_ORDER_TRANSID` (`transaction_id`) USING BTREE,
  KEY `order_code` (`order_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_appstore_verify
-- ----------------------------
DROP TABLE IF EXISTS `pay_appstore_verify`;
CREATE TABLE `pay_appstore_verify` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `receipt_data` longtext NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` decimal(22,0) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=80697 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_appstore_verify_log
-- ----------------------------
DROP TABLE IF EXISTS `pay_appstore_verify_log`;
CREATE TABLE `pay_appstore_verify_log` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `notify_count` int(9) DEFAULT NULL,
  `receipt_data` decimal(19,0) DEFAULT NULL,
  `notify_time` datetime DEFAULT NULL,
  `notify_url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `return_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` decimal(22,0) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=79275 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_appstore_white_list
-- ----------------------------
DROP TABLE IF EXISTS `pay_appstore_white_list`;
CREATE TABLE `pay_appstore_white_list` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  `p_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `partner` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `price` int(10) DEFAULT NULL COMMENT '۸',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=674 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_task
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_task`;
CREATE TABLE `pay_async_task` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `cronexpression` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `inqueue` bigint(10) DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_ASYNC_TASK_TIMERRUNAT` (`timerrun_at`),
  KEY `IDX_ASYNC_TASK_POOLTYPE_PRIORITY_ID` (`pooltype`,`priority`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1171921305 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_task_1
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_task_1`;
CREATE TABLE `pay_async_task_1` (
  `id` int(20) NOT NULL DEFAULT '0',
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `cronexpression` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `inqueue` bigint(10) DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_task_2
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_task_2`;
CREATE TABLE `pay_async_task_2` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `cronexpression` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `inqueue` bigint(10) DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_ASYNC_TASK_TIMERRUNAT` (`timerrun_at`),
  KEY `IDX_ASYNC_TASK_POOLTYPE_PRIORITY_ID` (`pooltype`,`priority`,`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62261135 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_task_processed
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_task_processed`;
CREATE TABLE `pay_async_task_processed` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `failed_reason` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `processed_at` datetime DEFAULT NULL,
  `status` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26727157 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_task0703
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_task0703`;
CREATE TABLE `pay_async_task0703` (
  `id` int(20) NOT NULL DEFAULT '0',
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `cronexpression` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `inqueue` bigint(10) DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_task0704
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_task0704`;
CREATE TABLE `pay_async_task0704` (
  `id` int(20) NOT NULL DEFAULT '0',
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `cronexpression` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `inqueue` bigint(10) DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_taskbak0703
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_taskbak0703`;
CREATE TABLE `pay_async_taskbak0703` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `cronexpression` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `inqueue` bigint(10) DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_ASYNC_TASK_POOLTYPE` (`pooltype`),
  KEY `IDX_ASYNC_TASK_TIMERRUNAT` (`timerrun_at`)
) ENGINE=InnoDB AUTO_INCREMENT=57913765 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_taskbak0704
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_taskbak0704`;
CREATE TABLE `pay_async_taskbak0704` (
  `id` int(20) NOT NULL,
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `cronexpression` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `inqueue` bigint(10) DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_ASYNC_TASK_POOLTYPE` (`pooltype`),
  KEY `IDX_ASYNC_TASK_TIMERRUNAT` (`timerrun_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_taskbak0705
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_taskbak0705`;
CREATE TABLE `pay_async_taskbak0705` (
  `id` int(20) NOT NULL,
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `cronexpression` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `inqueue` bigint(10) DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_ASYNC_TASK_POOLTYPE` (`pooltype`),
  KEY `IDX_ASYNC_TASK_TIMERRUNAT` (`timerrun_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_async_taskbak070518
-- ----------------------------
DROP TABLE IF EXISTS `pay_async_taskbak070518`;
CREATE TABLE `pay_async_taskbak070518` (
  `id` int(20) NOT NULL,
  `classname` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `created_at` datetime DEFAULT NULL,
  `cronexpression` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `inqueue` bigint(10) DEFAULT NULL,
  `pooltype` bigint(10) DEFAULT NULL,
  `priority` bigint(10) DEFAULT NULL,
  `timerrun_at` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_ASYNC_TASK_POOLTYPE` (`pooltype`),
  KEY `IDX_ASYNC_TASK_TIMERRUNAT` (`timerrun_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_auth_authority
-- ----------------------------
DROP TABLE IF EXISTS `pay_auth_authority`;
CREATE TABLE `pay_auth_authority` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `enabled` int(1) DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_c0011183` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=194 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_auth_authority_resource
-- ----------------------------
DROP TABLE IF EXISTS `pay_auth_authority_resource`;
CREATE TABLE `pay_auth_authority_resource` (
  `resource_id` decimal(19,0) NOT NULL,
  `authority_id` decimal(19,0) NOT NULL,
  PRIMARY KEY (`resource_id`,`authority_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_auth_resource
-- ----------------------------
DROP TABLE IF EXISTS `pay_auth_resource`;
CREATE TABLE `pay_auth_resource` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `enabled` int(1) DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `priority` double DEFAULT NULL,
  `type` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `value` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=117 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_auth_role
-- ----------------------------
DROP TABLE IF EXISTS `pay_auth_role`;
CREATE TABLE `pay_auth_role` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `enabled` int(1) DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_user` decimal(19,0) NOT NULL DEFAULT '-1',
  `update_user` decimal(19,0) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_c0011202` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_auth_role_authority
-- ----------------------------
DROP TABLE IF EXISTS `pay_auth_role_authority`;
CREATE TABLE `pay_auth_role_authority` (
  `role_id` decimal(19,0) NOT NULL,
  `authority_id` decimal(19,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_auth_user
-- ----------------------------
DROP TABLE IF EXISTS `pay_auth_user`;
CREATE TABLE `pay_auth_user` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `enabled` int(1) DEFAULT NULL,
  `login_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `create_user` decimal(19,0) NOT NULL DEFAULT '-1',
  `update_user` decimal(19,0) NOT NULL DEFAULT '-1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `sys_c0011215` (`login_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1164 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_auth_user_authority
-- ----------------------------
DROP TABLE IF EXISTS `pay_auth_user_authority`;
CREATE TABLE `pay_auth_user_authority` (
  `user_id` decimal(19,0) NOT NULL,
  `auth_id` decimal(19,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_auth_user_role
-- ----------------------------
DROP TABLE IF EXISTS `pay_auth_user_role`;
CREATE TABLE `pay_auth_user_role` (
  `user_id` decimal(19,0) NOT NULL,
  `role_id` decimal(19,0) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_bank
-- ----------------------------
DROP TABLE IF EXISTS `pay_bank`;
CREATE TABLE `pay_bank` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `user_id` decimal(19,0) NOT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `type` int(10) DEFAULT '0' COMMENT '类别0.银行,1.行业卡,2.充值卡,3.海外,4.信用卡',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=238 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_bank_code
-- ----------------------------
DROP TABLE IF EXISTS `pay_bank_code`;
CREATE TABLE `pay_bank_code` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `bank_id` decimal(19,0) NOT NULL,
  `service_id` decimal(19,0) NOT NULL,
  `bank_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `create_user` decimal(19,0) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_user` decimal(19,0) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(9) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bank_code_sid_bid_unq_inx` (`service_id`,`bank_id`),
  UNIQUE KEY `bank_code_sid_code_unq_inx` (`service_id`,`bank_code`)
) ENGINE=InnoDB AUTO_INCREMENT=414 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_bank_info
-- ----------------------------
DROP TABLE IF EXISTS `pay_bank_info`;
CREATE TABLE `pay_bank_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `bank_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '银联制定的银行编码',
  `bank_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '银行名字',
  `icon_url` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '银行图标的url',
  `pay_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '银行的支付方式',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `bank_info_bank_code` (`bank_code`)
) ENGINE=InnoDB AUTO_INCREMENT=194 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_bank_mapping
-- ----------------------------
DROP TABLE IF EXISTS `pay_bank_mapping`;
CREATE TABLE `pay_bank_mapping` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '银行名称',
  `type` int(1) NOT NULL COMMENT '银行卡类型，1信用卡快捷，2储蓄卡快捷',
  `code` varchar(16) NOT NULL COMMENT '业务系统通用的银行编码',
  `user_id` decimal(19,0) NOT NULL COMMENT '操作人id',
  `version` decimal(22,0) NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL COMMENT '记录的创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '记录的更新时间',
  `baifubao_code` varchar(16) DEFAULT NULL COMMENT '百付宝银行编码',
  `baifubao_status` int(1) NOT NULL DEFAULT '0' COMMENT '百付宝银行开启状态，0关闭、默认，1开启',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pbm_type_code` (`type`,`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for pay_batchunsub_detail
-- ----------------------------
DROP TABLE IF EXISTS `pay_batchunsub_detail`;
CREATE TABLE `pay_batchunsub_detail` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `partner_id` decimal(19,0) DEFAULT NULL,
  `product_id` decimal(19,0) DEFAULT NULL,
  `status` bigint(11) DEFAULT NULL,
  `reason` bigint(11) DEFAULT NULL,
  `last_modifier` decimal(19,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5689454 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_batchunsub_stat
-- ----------------------------
DROP TABLE IF EXISTS `pay_batchunsub_stat`;
CREATE TABLE `pay_batchunsub_stat` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `total_count` decimal(19,0) DEFAULT NULL,
  `success_count` decimal(19,0) DEFAULT NULL,
  `fail_count` decimal(19,0) DEFAULT NULL,
  `last_modifier` decimal(19,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `partner_id` decimal(19,0) DEFAULT NULL,
  `product_id` decimal(19,0) DEFAULT NULL,
  `reason` bigint(11) DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  `ignore_count` decimal(19,0) DEFAULT NULL,
  `invalid_count` decimal(19,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1087 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_billing_key
-- ----------------------------
DROP TABLE IF EXISTS `pay_billing_key`;
CREATE TABLE `pay_billing_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `package_name` varchar(64) DEFAULT NULL,
  `public_key` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_billing_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_billing_order`;
CREATE TABLE `pay_billing_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `billing_order_id` varchar(64) DEFAULT NULL,
  `order_code` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=296829 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_black_white_list
-- ----------------------------
DROP TABLE IF EXISTS `pay_black_white_list`;
CREATE TABLE `pay_black_white_list` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `apps` varchar(64) NOT NULL COMMENT '应用标识码',
  `func` varchar(64) NOT NULL COMMENT '功能标识码',
  `accesser_id` int(19) DEFAULT NULL COMMENT '接入方id',
  `type` int(10) NOT NULL DEFAULT '0' COMMENT '0:白名单,1:黑名单',
  `filter` varchar(20000) NOT NULL COMMENT '过滤规则',
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_card
-- ----------------------------
DROP TABLE IF EXISTS `pay_card`;
CREATE TABLE `pay_card` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `card_num_last` char(4) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '卡号的最后4位',
  `bank_code` varchar(40) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '银行代码',
  `card_type` int(11) DEFAULT NULL COMMENT '银行卡类型: 1为借记卡, 2为信用卡',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '手机号',
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '订单代码',
  `pay_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '支付方式代码',
  `token` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'token内容或签约号',
  `status` int(11) NOT NULL COMMENT '状态：1为有效,2为无效',
  `version` int(11) DEFAULT NULL COMMENT '版本号',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pay_card_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1001987947 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_card_bank
-- ----------------------------
DROP TABLE IF EXISTS `pay_card_bank`;
CREATE TABLE `pay_card_bank` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `card_num_first` varchar(15) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '卡号的前几位',
  `bank_code` varchar(40) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '银行代码',
  `bank_name` varchar(60) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '银行名称',
  `card_type` int(11) DEFAULT NULL COMMENT '银行卡类型: 1为借记卡, 2为信用卡',
  `pay_type` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '支付方式代码',
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pay_card_bank_card_num_first` (`card_num_first`)
) ENGINE=InnoDB AUTO_INCREMENT=3546 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_cashier_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_cashier_order`;
CREATE TABLE `pay_cashier_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `partner` varchar(20) NOT NULL,
  `partner_order_no` varchar(64) NOT NULL,
  `pay_center_order_code` varchar(64) DEFAULT NULL,
  `uid` bigint(20) NOT NULL,
  `subject` varchar(64) NOT NULL,
  `description` varchar(64) DEFAULT NULL,
  `status` bigint(11) NOT NULL,
  `mobile` varchar(20) DEFAULT NULL,
  `fee` bigint(64) NOT NULL,
  `fee_unit` bigint(11) NOT NULL,
  `expire_time` varchar(20) DEFAULT NULL,
  `return_url` varchar(255) DEFAULT NULL,
  `notify_url` varchar(255) NOT NULL,
  `expire_url` varchar(255) DEFAULT NULL,
  `forward_url` varchar(255) DEFAULT NULL,
  `exit_tip` varchar(100) DEFAULT NULL,
  `expire_tip` varchar(100) DEFAULT NULL COMMENT '超时文案',
  `extra_common_param` varchar(100) DEFAULT NULL,
  `version` bigint(11) NOT NULL,
  `platform` varchar(20) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `pay_time` datetime DEFAULT NULL,
  `sign_type` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_partner_order_no` (`partner_order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=6983778 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_cashier_partner_paytype
-- ----------------------------
DROP TABLE IF EXISTS `pay_cashier_partner_paytype`;
CREATE TABLE `pay_cashier_partner_paytype` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `partner_id` bigint(11) NOT NULL COMMENT '接入方',
  `platform` varchar(32) NOT NULL COMMENT '平台',
  `pay_type` varchar(32) NOT NULL COMMENT '支付方式',
  `type_id` int(11) NOT NULL,
  `by_sort` int(11) NOT NULL COMMENT '排序',
  `is_hide` int(11) NOT NULL COMMENT '是否隐藏',
  `displayed_name` varchar(64) NOT NULL COMMENT '展示名称',
  `bak_pay_type` varchar(32) DEFAULT NULL COMMENT '备选支付方式',
  `is_checked` int(11) NOT NULL COMMENT '是否选中',
  `icon_url` varchar(64) DEFAULT NULL COMMENT 'pcweb图标地址',
  `banner` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=56 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_china_union_token
-- ----------------------------
DROP TABLE IF EXISTS `pay_china_union_token`;
CREATE TABLE `pay_china_union_token` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `content` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'token内容',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `bank_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '银行id',
  `account_num` char(4) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '账号的最后4位',
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '手机号',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_china_union_token_user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_cmb_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_cmb_order`;
CREATE TABLE `pay_cmb_order` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT 'Id',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '招行订单号',
  `order_code` varchar(64) DEFAULT NULL COMMENT '支付中心订单号',
  `order_date` varchar(20) DEFAULT NULL COMMENT '订单日期',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_bill_no_date` (`bill_no`,`order_date`),
  UNIQUE KEY `idx_order_code` (`order_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1782262 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_common_notify_log
-- ----------------------------
DROP TABLE IF EXISTS `pay_common_notify_log`;
CREATE TABLE `pay_common_notify_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` varchar(1024) DEFAULT NULL,
  `notify_count` int(11) DEFAULT NULL,
  `notify_time` datetime DEFAULT NULL,
  `notify_url` varchar(1024) DEFAULT NULL,
  `return_code` varchar(32) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=95029045 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_contract
-- ----------------------------
DROP TABLE IF EXISTS `pay_contract`;
CREATE TABLE `pay_contract` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `contract_no` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `end_time` datetime NOT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `partner_id` decimal(19,0) NOT NULL,
  `start_time` datetime NOT NULL,
  `user_id` decimal(19,0) NOT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=600 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_currency
-- ----------------------------
DROP TABLE IF EXISTS `pay_currency`;
CREATE TABLE `pay_currency` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `no` int(5) NOT NULL COMMENT '币种编号',
  `name` varchar(64) NOT NULL COMMENT '币种名称',
  `currency` varchar(16) NOT NULL COMMENT '币种代码 ',
  `create_time` datetime NOT NULL COMMENT '创建时间 ',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间 ',
  `version` decimal(22,0) NOT NULL COMMENT '版本号 ',
  PRIMARY KEY (`id`),
  UNIQUE KEY `no` (`no`),
  UNIQUE KEY `currency` (`currency`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COMMENT='币种信息表';

-- ----------------------------
-- Table structure for pay_dict
-- ----------------------------
DROP TABLE IF EXISTS `pay_dict`;
CREATE TABLE `pay_dict` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  `cn` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `demo` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `key` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `value` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `pay_dict_data`;
CREATE TABLE `pay_dict_data` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `description` varchar(512) DEFAULT NULL COMMENT '描述',
  `version` int(11) DEFAULT NULL,
  `env` varchar(64) DEFAULT NULL COMMENT '环境   test:测试环境    product:生产环境   cncp1  ctcp2',
  `data_key` varchar(128) DEFAULT NULL COMMENT '数据键',
  `data_value` varchar(1024) DEFAULT NULL COMMENT '数据值',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `AK_dict_data_union_key` (`data_key`,`env`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8 COMMENT='数据字典pay_dict_data';

-- ----------------------------
-- Table structure for pay_dut_conf
-- ----------------------------
DROP TABLE IF EXISTS `pay_dut_conf`;
CREATE TABLE `pay_dut_conf` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `partner_id` int(11) NOT NULL COMMENT '接入方id',
  `dut_type_id` int(11) NOT NULL COMMENT '代扣方式id',
  `product_code` varchar(64) NOT NULL COMMENT '代扣产品代码',
  `product_price` int(9) NOT NULL COMMENT '产品价格',
  `status` int(1) NOT NULL DEFAULT '1' COMMENT '状态，1-有效，2无效',
  `priority` int(2) NOT NULL DEFAULT '1' COMMENT '优先级',
  `times` int(2) NOT NULL DEFAULT '0' COMMENT '续费次数',
  `period` int(8) NOT NULL DEFAULT '0' COMMENT '续费周期，秒级，默认0',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `version` bigint(10) NOT NULL COMMENT '版本号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_dut_contract
-- ----------------------------
DROP TABLE IF EXISTS `pay_dut_contract`;
CREATE TABLE `pay_dut_contract` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `contract_no` varchar(64) NOT NULL COMMENT '签约流水号',
  `status` int(1) NOT NULL COMMENT '状态，0-未签约，1-已签约，2-取消签约',
  `uid` bigint(20) NOT NULL COMMENT '用户的唯一标识',
  `display_account` varchar(32) DEFAULT NULL COMMENT '用户的显示账号，或是昵称',
  `partner_id` int(11) NOT NULL COMMENT '接入方id',
  `platform_code` varchar(32) DEFAULT NULL COMMENT '平台编码',
  `src_pay_type_id` int(11) NOT NULL COMMENT '支付方式id',
  `dest_pay_type_id` int(11) NOT NULL COMMENT '支付服务id',
  `return_url` varchar(255) DEFAULT NULL COMMENT '同步通知地址',
  `notify_url` varchar(255) NOT NULL COMMENT '异步通知地址',
  `extend_params` varchar(4000) DEFAULT NULL COMMENT '扩展参数',
  `current_key` int(11) DEFAULT NULL COMMENT '签名key',
  `third_contract_no` varchar(64) DEFAULT NULL COMMENT '第三方签约号',
  `third_uid` varchar(64) DEFAULT NULL COMMENT '第三方对用户的唯一标识',
  `contract_time` datetime DEFAULT NULL COMMENT '成功签约的时间',
  `expired_time` datetime DEFAULT NULL COMMENT '签约有效期',
  `sign_corp_id` int(11) DEFAULT NULL COMMENT '签约公司id',
  `account_id` int(11) DEFAULT NULL COMMENT '商户号id',
  `extra_common_params` varchar(200) DEFAULT NULL COMMENT '公用回传参数',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_dutcontract_contract_no` (`contract_no`),
  KEY `idx_dutcontract_uid` (`uid`),
  KEY `idx_dutcontract_dest_pay_type_id` (`dest_pay_type_id`),
  KEY `idx_dutcontract_third_contract_no` (`third_contract_no`),
  KEY `idx_dutcontract_create_time` (`create_time`),
  KEY `idx_dutcontract_contract_time` (`contract_time`),
  KEY `idx_dutcontract_partner_id` (`partner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4066921 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_dut_log
-- ----------------------------
DROP TABLE IF EXISTS `pay_dut_log`;
CREATE TABLE `pay_dut_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uid` bigint(19) NOT NULL COMMENT '用户uid',
  `contract_no` varchar(32) NOT NULL COMMENT '签约协议号',
  `order_code` varchar(64) NOT NULL COMMENT '支付订单号',
  `status` int(2) DEFAULT '0' COMMENT '状态,0:未处理,1:成功 2:失败',
  `dut_type_id` int(20) NOT NULL COMMENT '代扣方式id',
  `dut_conf_id` int(20) NOT NULL COMMENT '代扣策略配置id',
  `dut_times` int(1) NOT NULL COMMENT '代扣次数',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `version` bigint(10) NOT NULL COMMENT '版本号',
  `error_code` varchar(256) DEFAULT NULL COMMENT '第三方错误码',
  `error_msg` varchar(256) DEFAULT NULL COMMENT '第三方错误信息',
  PRIMARY KEY (`id`),
  KEY `idx_dutlog_contractno` (`contract_no`),
  KEY `idx_dutlog_createtime` (`create_time`),
  KEY `idx_ordercode_duttimes` (`order_code`,`dut_times`)
) ENGINE=InnoDB AUTO_INCREMENT=9382115 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_dut_platform
-- ----------------------------
DROP TABLE IF EXISTS `pay_dut_platform`;
CREATE TABLE `pay_dut_platform` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `partner_id` int(11) NOT NULL,
  `platform_code` varchar(32) NOT NULL,
  `platform_desc` varchar(100) NOT NULL,
  `payment_type_id` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_exception_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_exception_order`;
CREATE TABLE `pay_exception_order` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `third_trade_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `fee` int(9) NOT NULL,
  `dest_pay_type` decimal(19,0) DEFAULT NULL,
  `partner_id` decimal(19,0) NOT NULL,
  `trade_time` datetime NOT NULL,
  `batch_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `cause_type` int(9) NOT NULL,
  `status` int(9) NOT NULL,
  `memo` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` int(9) NOT NULL,
  `create_user` decimal(19,0) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_user` decimal(19,0) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `corp_id` int(9) DEFAULT NULL,
  `accesser_id` int(9) DEFAULT NULL,
  `partner_order_no` varchar(64) DEFAULT NULL COMMENT '业务系统订单号',
  `order_type` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_accesser_id` (`accesser_id`),
  KEY `idx_batch_no` (`batch_no`),
  KEY `idx_order_code` (`order_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2417114 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_financial_statement
-- ----------------------------
DROP TABLE IF EXISTS `pay_financial_statement`;
CREATE TABLE `pay_financial_statement` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增长',
  `batch_no` varchar(32) NOT NULL COMMENT '批次号',
  `stat_date` datetime DEFAULT NULL COMMENT '统计日期',
  `sign_corp_id` int(10) DEFAULT NULL COMMENT '签约公司id',
  `account` varchar(128) DEFAULT NULL COMMENT '第三方的提供的商户号',
  `partner_id` int(10) DEFAULT NULL COMMENT '合约方id',
  `status` int(3) DEFAULT NULL COMMENT '状态: 0:未完结,1:已完结,2:重新对账中 3:已经重新对账',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `amount` int(16) DEFAULT NULL COMMENT '应收金额,支付中心总金额',
  `number` int(16) DEFAULT NULL COMMENT '应收单数,支付中心订单数',
  `real_amount` int(16) DEFAULT NULL COMMENT '实收金额,第三方总金额',
  `real_number` int(16) DEFAULT NULL COMMENT '实收单数,第三方订单数',
  `test_amount` int(16) DEFAULT NULL COMMENT '测试单金额',
  `test_number` int(16) DEFAULT NULL COMMENT '测试单数',
  `diff_amount` int(16) DEFAULT NULL COMMENT '差异金额',
  `diff_number` int(16) DEFAULT NULL COMMENT '差异单数',
  `accesser_id` int(10) DEFAULT NULL COMMENT '接入方id,只有区分接入方时才会有值',
  `statement_conf_id` int(10) DEFAULT NULL COMMENT '对账配置表id',
  `type` int(3) DEFAULT NULL COMMENT '类型:1 业务无关的对账结果  2:区分接入方的',
  `version` int(10) DEFAULT NULL COMMENT '版本号',
  `operation_type` int(10) DEFAULT NULL COMMENT '操作类型: 0: 自动对账 1:手动对账',
  `order_type` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27933 DEFAULT CHARSET=utf8 COMMENT='财务对账单';

-- ----------------------------
-- Table structure for pay_key
-- ----------------------------
DROP TABLE IF EXISTS `pay_key`;
CREATE TABLE `pay_key` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `invalid_time` datetime DEFAULT NULL,
  `status` bigint(10) DEFAULT NULL,
  `value` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `accesser_id` decimal(19,0) DEFAULT NULL,
  `sign_type` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `accesser_publickey` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `pay_publickey` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `pay_privatekey` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `accesser_privatekey` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_PAY_KEY_ACCESSER_ID_CT` (`accesser_id`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_lock
-- ----------------------------
DROP TABLE IF EXISTS `pay_lock`;
CREATE TABLE `pay_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `timeout` decimal(19,0) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pay_lock_name_unq` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2532286058 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_log_operator
-- ----------------------------
DROP TABLE IF EXISTS `pay_log_operator`;
CREATE TABLE `pay_log_operator` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `operator` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ip` varchar(40) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `module` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `action` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `method` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `type` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `object` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `info` varchar(2000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_LOG_OPERATOR_CREATETIME` (`create_time`) USING BTREE,
  KEY `IDX_LOG_OPERATOR_OPERATOR` (`operator`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=703252 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_blacklist
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_blacklist`;
CREATE TABLE `pay_mobile_blacklist` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(25) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `status` bigint(11) DEFAULT NULL,
  `invalid_time` datetime DEFAULT NULL,
  `rule` bigint(11) DEFAULT NULL,
  `last_modifier` decimal(19,0) DEFAULT NULL,
  `memo` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mobile_rule` (`mobile`,`rule`)
) ENGINE=InnoDB AUTO_INCREMENT=205525 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_exception_data
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_exception_data`;
CREATE TABLE `pay_mobile_exception_data` (
  `mobile` bigint(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_fee_code
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_fee_code`;
CREATE TABLE `pay_mobile_fee_code` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `fee` int(11) DEFAULT NULL,
  `fee_code` varchar(255) DEFAULT NULL,
  `pay_type_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_fee_product
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_fee_product`;
CREATE TABLE `pay_mobile_fee_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `accessor_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_fee_province
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_fee_province`;
CREATE TABLE `pay_mobile_fee_province` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `mobile_code_id` bigint(20) DEFAULT NULL,
  `mobile_product_id` bigint(20) DEFAULT NULL,
  `operator` int(11) DEFAULT NULL,
  `priority` int(11) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=538 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly`;
CREATE TABLE `pay_mobile_monthly` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `partner_id` int(20) NOT NULL,
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `mobile_product_id` int(19) NOT NULL,
  `code_id` int(20) NOT NULL,
  `provider_business_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `order_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `subscribe_time` datetime DEFAULT NULL,
  `unsubscribe_time` datetime DEFAULT NULL,
  `status` int(9) NOT NULL,
  `lastmodifier` int(19) DEFAULT NULL,
  `reason` bigint(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(9) DEFAULT NULL,
  `provider_id` int(19) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mobile_idx` (`mobile`),
  KEY `IDX_PAY_MOBILE_MONTHLY_USERID` (`user_id`) USING BTREE,
  KEY `provider_id_business_id_key` (`provider_id`,`provider_business_id`),
  KEY `IDX_PAY_MOBILE_MONTHLY_ORDERNO` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=12988249 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly_code
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly_code`;
CREATE TABLE `pay_mobile_monthly_code` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `provider_id` int(20) DEFAULT NULL,
  `operator` int(20) DEFAULT NULL,
  `provider_company_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `provider_business_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `provider_business_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subcode` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subnum` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `unsubcode` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `unsubnum` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `status` bigint(11) DEFAULT NULL,
  `price` bigint(11) DEFAULT NULL,
  `contract_id` int(20) DEFAULT NULL,
  `all_area_status` bigint(11) DEFAULT NULL,
  `last_modifier` int(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `code_id` int(20) DEFAULT NULL,
  `province` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `payment_serve_id` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=487 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly_code_area
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly_code_area`;
CREATE TABLE `pay_mobile_monthly_code_area` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `code_id` decimal(19,0) NOT NULL,
  `province` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `status` bigint(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=707 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly_payitem
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly_payitem`;
CREATE TABLE `pay_mobile_monthly_payitem` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `provider_id` int(10) DEFAULT NULL,
  `provider_business_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `sync_time` datetime DEFAULT NULL,
  `fee` int(9) DEFAULT '0',
  `info` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(20) DEFAULT NULL,
  `mobile_monthly_id` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mobile_idx` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=9109203 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly_product
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly_product`;
CREATE TABLE `pay_mobile_monthly_product` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `partner_id` decimal(19,0) DEFAULT NULL,
  `status` bigint(11) DEFAULT NULL,
  `last_modifier` decimal(19,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=210 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly_productarea
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly_productarea`;
CREATE TABLE `pay_mobile_monthly_productarea` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `product_id` decimal(19,0) DEFAULT NULL,
  `code_id` decimal(19,0) DEFAULT NULL,
  `operator` bigint(11) DEFAULT NULL,
  `province` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=503 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly_productcode
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly_productcode`;
CREATE TABLE `pay_mobile_monthly_productcode` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `product_id` decimal(19,0) DEFAULT NULL,
  `code_id` decimal(19,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  `priority` decimal(22,0) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly_sms
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly_sms`;
CREATE TABLE `pay_mobile_monthly_sms` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(25) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `fee` bigint(11) DEFAULT NULL,
  `send_time` datetime DEFAULT NULL,
  `operator` varchar(25) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `action` bigint(11) DEFAULT NULL,
  `status` bigint(11) DEFAULT NULL,
  `provider_id` decimal(19,0) DEFAULT NULL,
  `provider_business_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=2822664 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly_status
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly_status`;
CREATE TABLE `pay_mobile_monthly_status` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `provider_id` decimal(19,0) DEFAULT NULL,
  `provider_business_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `sync_time` datetime DEFAULT NULL,
  `content` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `status` decimal(22,0) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_MMS_SYNC_TIME_MOBILE` (`sync_time`,`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=97162590 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_monthly_unpay
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_monthly_unpay`;
CREATE TABLE `pay_mobile_monthly_unpay` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `provider_id` int(20) DEFAULT NULL,
  `provider_business_id` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `prepay_time` datetime DEFAULT NULL,
  `smsnum` int(9) DEFAULT NULL,
  `smssucnum` int(9) DEFAULT NULL,
  `info` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mobile_idx` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=853253 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_order_seg
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_order_seg`;
CREATE TABLE `pay_mobile_order_seg` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `mobile_monthly_id` decimal(19,0) DEFAULT NULL,
  `old_order_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `new_order_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `seg_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `PAY_MOBILE_ORDER_SEG_MONTHLYID` (`mobile_monthly_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10135740 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_segment
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_segment`;
CREATE TABLE `pay_mobile_segment` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `brand` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `end_no` decimal(19,0) DEFAULT NULL,
  `operator` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `province` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `section` varchar(25) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `start_no` decimal(19,0) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` bigint(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pay_mobile_segment_section_uq` (`section`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=632993 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mobile_stats_data
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_stats_data`;
CREATE TABLE `pay_mobile_stats_data` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `batch_no` varchar(32) DEFAULT NULL COMMENT '批次号',
  `provider_id` int(20) DEFAULT NULL COMMENT '提供商id',
  `provider_business_id` varchar(32) DEFAULT NULL COMMENT '提供商业务id',
  `exception_num` int(16) DEFAULT NULL COMMENT '异常数',
  `success_num` int(16) DEFAULT NULL COMMENT '成功数',
  `third_num` int(16) DEFAULT NULL COMMENT '第三方总数',
  `stat_time` datetime DEFAULT NULL COMMENT '统计日期',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `status` int(2) DEFAULT NULL COMMENT '状态(0:统计中 1:已通过  2:未通过)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `AK_stats_batch_no` (`batch_no`),
  KEY `AK_stats_create_time` (`create_time`),
  KEY `AK_stats_stat_date` (`stat_time`)
) ENGINE=InnoDB AUTO_INCREMENT=685 DEFAULT CHARSET=utf8 COMMENT='手机包月统计数据pay_mobile_stats_data';

-- ----------------------------
-- Table structure for pay_mobile_stats_exception
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_stats_exception`;
CREATE TABLE `pay_mobile_stats_exception` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `batch_no` varchar(32) DEFAULT NULL COMMENT '批次号',
  `provider_id` int(20) DEFAULT NULL COMMENT '提供商id',
  `provider_business_id` varchar(32) DEFAULT NULL COMMENT '提供商业务id',
  `mobile` varchar(11) DEFAULT NULL COMMENT '手机号',
  `stat_time` datetime DEFAULT NULL COMMENT '对账时间',
  `reason` int(2) DEFAULT NULL COMMENT '异常原因:(1：提供商订购中，我方申请中\n            2：提供商订购中，我方已退订\n            3：提供商订购中，我方不存在\n            4：提供商未订购，我方订购中\n            )',
  `status` int(2) DEFAULT NULL COMMENT '处理状态(0:未处理  1:已处理)',
  `memo` varchar(256) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `update_user` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `AK_exception_batch_no` (`batch_no`),
  KEY `AK_exception_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=26296760 DEFAULT CHARSET=utf8 COMMENT='手机包月对账异常数据pay_mobile_stats_exception';

-- ----------------------------
-- Table structure for pay_mobile_stats_third
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_stats_third`;
CREATE TABLE `pay_mobile_stats_third` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `batch_no` varchar(32) DEFAULT NULL COMMENT '批次号',
  `provider_id` int(20) DEFAULT NULL COMMENT '提供商id',
  `provider_business_id` varchar(32) DEFAULT NULL COMMENT '提供商业务id',
  `mobile` varchar(11) DEFAULT NULL COMMENT '手机号',
  `stat_time` datetime DEFAULT NULL COMMENT '对账时间',
  `status` int(2) DEFAULT NULL COMMENT '状态(0:退订状态 1:订购状态  2:待定)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `AK_third_record_batch_no` (`batch_no`),
  KEY `AK_third_record_union_key` (`provider_id`,`provider_business_id`,`status`),
  KEY `AK_third_record_mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=90557087 DEFAULT CHARSET=utf8 COMMENT='第三方手机包月渠道数据pay_mobile_stats_third';

-- ----------------------------
-- Table structure for pay_mobile_uid
-- ----------------------------
DROP TABLE IF EXISTS `pay_mobile_uid`;
CREATE TABLE `pay_mobile_uid` (
  `mobile` varchar(30) DEFAULT NULL,
  `uid` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_mod_queue
-- ----------------------------
DROP TABLE IF EXISTS `pay_mod_queue`;
CREATE TABLE `pay_mod_queue` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int(4) NOT NULL COMMENT '类别:0,订单',
  `status` int(4) NOT NULL COMMENT '状态值:0，未处理，1，已处理',
  `entity_id` bigint(20) NOT NULL COMMENT '实体id',
  `entity_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '实体code',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `create_time` datetime NOT NULL COMMENT '创建日期',
  PRIMARY KEY (`id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=3767341353 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_monthly_detail_stat
-- ----------------------------
DROP TABLE IF EXISTS `pay_monthly_detail_stat`;
CREATE TABLE `pay_monthly_detail_stat` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `provider_id` decimal(19,0) DEFAULT NULL,
  `month` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `stat_type` int(9) DEFAULT NULL,
  `smsnum` int(9) DEFAULT NULL,
  `smssucnum` int(9) DEFAULT NULL,
  `mobile_monthly_id` decimal(19,0) DEFAULT NULL,
  `subscribe_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `unsubscribe_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=618330 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_notify_log
-- ----------------------------
DROP TABLE IF EXISTS `pay_notify_log`;
CREATE TABLE `pay_notify_log` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `partner_id` int(9) DEFAULT NULL,
  `notify_count` int(9) DEFAULT NULL,
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `partner_order_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `notify_time` datetime DEFAULT NULL,
  `notify_url` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `return_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `status` int(9) DEFAULT NULL,
  `type` int(9) DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_NOTIFY_LOG_ORDER_CODE` (`order_code`) USING BTREE,
  KEY `IDX_CREATE_TIME` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=116891732 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_order`;
CREATE TABLE `pay_order` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `partner_order_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `third_trade_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `source_pay_type` int(9) DEFAULT NULL,
  `dest_pay_type` int(9) DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL,
  `third_create_time` datetime DEFAULT NULL,
  `third_pay_time` datetime DEFAULT NULL,
  `status` int(9) NOT NULL,
  `mobile_status` int(9) DEFAULT NULL,
  `fee` int(9) NOT NULL,
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `user_account` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subject` varchar(62) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `partner_id` int(9) DEFAULT NULL,
  `notify_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `return_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `current_key` int(9) DEFAULT NULL,
  `ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `extend_params` varchar(20000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `pid` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `pay_index` decimal(19,0) DEFAULT NULL,
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `extra_common_param` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `show_url` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `expire_time` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `parent_id` decimal(19,0) DEFAULT NULL,
  `sub_fee` int(9) DEFAULT NULL,
  `sub_real_fee` int(9) DEFAULT NULL,
  `real_fee` int(9) DEFAULT NULL,
  `gate_way` decimal(22,0) DEFAULT '2',
  `fee_unit` decimal(22,0) DEFAULT '1',
  `sign_corp_id` int(10) DEFAULT '0' COMMENT '????id',
  `pay_mode` tinyint(10) DEFAULT NULL COMMENT '1.??????',
  `account_id` int(10) DEFAULT NULL COMMENT '新的商户号id',
  `client_code` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pay_order_code` (`order_code`) USING BTREE,
  KEY `idx_pay_order_dest_pay_type` (`dest_pay_type`),
  KEY `idx_pay_order_partner_id` (`partner_id`),
  KEY `idx_pay_order_user_id` (`user_id`),
  KEY `idx_partner_order_no` (`partner_order_no`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_pay_create_time` (`create_time`),
  KEY `IDX_ORDER_PAY_TIME` (`pay_time`),
  KEY `idx_mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=1959529206 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_order_error_log
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_error_log`;
CREATE TABLE `pay_order_error_log` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `error_reason` varchar(300) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` decimal(22,0) DEFAULT NULL,
  `error_detail` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `data` blob,
  `type` int(20) DEFAULT NULL,
  `dest_pay_type` int(9) DEFAULT NULL COMMENT '支付服务id',
  `partner_id` int(9) DEFAULT NULL COMMENT '接入方id ',
  PRIMARY KEY (`id`),
  KEY `IDX_ERRLOG_ORDER_CODE` (`order_code`)
) ENGINE=InnoDB AUTO_INCREMENT=31201484 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_order_extend
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_extend`;
CREATE TABLE `pay_order_extend` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `order_key` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `pay_code` varchar(32) DEFAULT NULL,
  `card_id` varchar(64) DEFAULT NULL,
  `uid` varchar(32) DEFAULT NULL,
  `third_trade_code` varchar(64) DEFAULT NULL,
  `pay_time` timestamp NULL DEFAULT NULL,
  `third_uid` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_code` (`order_code`)
) ENGINE=InnoDB AUTO_INCREMENT=408396468 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_order_file
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_file`;
CREATE TABLE `pay_order_file` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `partner_id` decimal(19,0) NOT NULL,
  `file_path` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `order_month` varchar(6) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `batch_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `payment_serve_id` decimal(19,0) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `process_status` int(9) NOT NULL,
  `is_verify` int(9) NOT NULL,
  `version` int(9) NOT NULL,
  `type` int(9) DEFAULT NULL,
  `import_dataex` int(9) DEFAULT NULL,
  `channel_id` bigint(20) DEFAULT NULL,
  `corp_id` bigint(20) DEFAULT NULL,
  `date_between` varchar(255) DEFAULT NULL,
  `error_reason` varchar(255) DEFAULT NULL,
  `file_num` int(11) NOT NULL,
  `progress_rate` int(11) NOT NULL,
  `verify_email` varchar(45) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `statement_conf_id` int(10) DEFAULT NULL COMMENT '对账配置表id',
  `operation_type` int(10) DEFAULT NULL COMMENT '操作类型: 0: 自动对账 1:手动对账',
  `stat_date` datetime DEFAULT NULL COMMENT '统计时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12753 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_order_process
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_process`;
CREATE TABLE `pay_order_process` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `trans_one` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `trans_two` varchar(500) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `trans_one_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `trans_two_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(9) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_order_rate
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_rate`;
CREATE TABLE `pay_order_rate` (
  `order_id` decimal(19,0) NOT NULL,
  `status` int(9) DEFAULT NULL,
  `fee` int(9) DEFAULT NULL,
  `partner_price` decimal(10,2) DEFAULT NULL,
  `partner_id` int(9) DEFAULT NULL,
  `total_partner_price` decimal(19,0) DEFAULT NULL,
  `total_partner_rate_price` decimal(19,0) DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `pay_index` decimal(19,0) DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_order_refund
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_refund`;
CREATE TABLE `pay_order_refund` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `partner_order_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `third_trade_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `source_pay_type` int(9) DEFAULT NULL,
  `dest_pay_type` int(9) DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL,
  `third_create_time` datetime DEFAULT NULL,
  `third_pay_time` datetime DEFAULT NULL,
  `status` int(9) NOT NULL,
  `mobile_status` int(9) DEFAULT NULL,
  `fee` int(9) NOT NULL,
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `user_account` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `subject` varchar(62) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `partner_id` int(9) DEFAULT NULL,
  `notify_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `return_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `current_key` int(9) DEFAULT NULL,
  `ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `extend_params` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `pid` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `pay_index` decimal(19,0) DEFAULT NULL,
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `extra_common_param` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `show_url` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `expire_time` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `parent_id` decimal(19,0) DEFAULT NULL,
  `sub_fee` int(9) DEFAULT NULL,
  `sub_real_fee` int(9) DEFAULT NULL,
  `real_fee` int(9) DEFAULT NULL,
  `gate_way` decimal(22,0) DEFAULT '2',
  `fee_unit` decimal(22,0) DEFAULT '1',
  `sign_corp_id` int(10) DEFAULT '0',
  `pay_mode` tinyint(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_third_trade_code` (`third_trade_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2257 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_order_route
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_route`;
CREATE TABLE `pay_order_route` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(64) NOT NULL COMMENT '订单号',
  `partner_order_no` varchar(64) DEFAULT NULL,
  `partner_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_order_route_ORDER_CODE` (`order_code`),
  KEY `idx_order_route_partner_order_no` (`partner_order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=20296106 DEFAULT CHARSET=utf8 COMMENT='保存订单是否在分表分库里';

-- ----------------------------
-- Table structure for pay_order_route_config
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_route_config`;
CREATE TABLE `pay_order_route_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dest_pay_type` int(10) NOT NULL COMMENT '支付服务ID',
  `start_time` bigint(20) NOT NULL COMMENT '订单号前缀时间(2015112009)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='订单分库路由配置表';

-- ----------------------------
-- Table structure for pay_order_trans
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_trans`;
CREATE TABLE `pay_order_trans` (
  `id` bigint(32) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(32) DEFAULT NULL,
  `source_pay_type` int(11) DEFAULT NULL,
  `dest_pay_type` int(11) DEFAULT NULL,
  `sign_corp_id` int(11) DEFAULT NULL,
  `account_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_order_code` (`order_code`)
) ENGINE=InnoDB AUTO_INCREMENT=94768 DEFAULT CHARSET=utf8;

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

-- ----------------------------
-- Table structure for pay_payment_type
-- ----------------------------
DROP TABLE IF EXISTS `pay_payment_type`;
CREATE TABLE `pay_payment_type` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `description` varchar(256) DEFAULT NULL COMMENT '备注说明',
  `pay_code` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `bank_id` decimal(19,0) DEFAULT NULL,
  `service_id` decimal(19,0) DEFAULT NULL,
  `visible` int(9) NOT NULL,
  `priority` int(9) NOT NULL,
  `pay_type` int(9) NOT NULL,
  `create_user` decimal(19,0) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_user` decimal(19,0) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` int(9) NOT NULL,
  `version` int(9) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `payment_type_name_unq_inx` (`name`),
  UNIQUE KEY `payment_type_pay_code_unq_inx` (`pay_code`),
  UNIQUE KEY `payment_type_bank_id_unq_inx` (`bank_id`)
) ENGINE=InnoDB AUTO_INCREMENT=430 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_paypal_trans_data
-- ----------------------------
DROP TABLE IF EXISTS `pay_paypal_trans_data`;
CREATE TABLE `pay_paypal_trans_data` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(19) NOT NULL COMMENT '用户id',
  `transaction_count_total` int(15) NOT NULL COMMENT '交易成功总单数',
  `last_good_transaction_date` datetime DEFAULT NULL COMMENT '最后一次交易成功时间',
  `email` varchar(512) DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(15) DEFAULT NULL COMMENT '手机号',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pay_paypal_trans_data_uid_unq` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=68271 DEFAULT CHARSET=utf8 COMMENT='paypal 快速结账风控数据表';

-- ----------------------------
-- Table structure for pay_payservice_platform
-- ----------------------------
DROP TABLE IF EXISTS `pay_payservice_platform`;
CREATE TABLE `pay_payservice_platform` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `payment_service_id` decimal(19,0) DEFAULT NULL,
  `platform_id` decimal(19,0) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(9) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_phonenum_segment
-- ----------------------------
DROP TABLE IF EXISTS `pay_phonenum_segment`;
CREATE TABLE `pay_phonenum_segment` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `brand` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `city` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `end_no` decimal(19,0) DEFAULT NULL,
  `operator` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `province` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `section` varchar(25) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `start_no` decimal(19,0) DEFAULT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_platform
-- ----------------------------
DROP TABLE IF EXISTS `pay_platform`;
CREATE TABLE `pay_platform` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `platform` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `description` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `status` bigint(10) DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` decimal(22,0) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_provider_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_provider_order`;
CREATE TABLE `pay_provider_order` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `third_trade_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `fee` int(9) DEFAULT NULL,
  `dest_pay_type` decimal(19,0) NOT NULL,
  `partner_id` decimal(19,0) NOT NULL,
  `batch_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `status` int(9) NOT NULL,
  `trade_time` datetime NOT NULL,
  `is_verify` int(9) NOT NULL,
  `verify_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `version` int(9) NOT NULL,
  `corp_id` int(9) DEFAULT NULL,
  `pay_mode` int(10) DEFAULT NULL COMMENT '1.代表测试订单,不进行对账',
  `accesser_id` int(10) DEFAULT NULL COMMENT '接入方id',
  `order_type` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_batch_no` (`batch_no`),
  KEY `idx_order_code` (`order_code`),
  KEY `idx_accesser_id` (`accesser_id`),
  KEY `idx_pay_mode` (`pay_mode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_rate
-- ----------------------------
DROP TABLE IF EXISTS `pay_rate`;
CREATE TABLE `pay_rate` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `rate` decimal(19,0) NOT NULL,
  `start_money` decimal(19,0) NOT NULL,
  `end_money` decimal(19,0) NOT NULL,
  `rate_info_id` int(20) NOT NULL,
  `create_user` decimal(19,0) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_user` decimal(19,0) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(9) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pay_partner_rid_sm_uni_inx` (`rate_info_id`,`start_money`),
  CONSTRAINT `pay_rate_rate_info_id_fk` FOREIGN KEY (`rate_info_id`) REFERENCES `pay_rate_info` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_rate_extend
-- ----------------------------
DROP TABLE IF EXISTS `pay_rate_extend`;
CREATE TABLE `pay_rate_extend` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `currency_id` int(11) DEFAULT NULL COMMENT '币种表主键',
  `name` varchar(255) DEFAULT NULL COMMENT '名称',
  `thirdparty_subchannel` varchar(128) DEFAULT NULL COMMENT '第三方接入子渠道',
  `pid` varchar(64) DEFAULT NULL COMMENT '接入方产品代码',
  `extend_params` varchar(64) DEFAULT NULL COMMENT '扩展参数',
  `service_corp_rate_id` int(20) DEFAULT NULL COMMENT 'pay_service_corp_rate表 id',
  `sign_corp_id` int(20) DEFAULT NULL COMMENT '签约公司id',
  `service_id` int(20) DEFAULT NULL COMMENT '支付服务id',
  `payment_type_id` int(20) DEFAULT NULL COMMENT '支付类型id',
  `rate_info_id` int(11) NOT NULL COMMENT '费率信息表主键',
  `create_user` decimal(19,0) NOT NULL COMMENT '创建用户',
  `update_user` decimal(19,0) DEFAULT NULL COMMENT '更新用户',
  `create_time` datetime NOT NULL COMMENT '创建时间 ',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间 ',
  `version` decimal(22,0) NOT NULL COMMENT '版本号 ',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='费率扩展表';

-- ----------------------------
-- Table structure for pay_rate_info
-- ----------------------------
DROP TABLE IF EXISTS `pay_rate_info`;
CREATE TABLE `pay_rate_info` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `description` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `calculate_type` decimal(19,0) NOT NULL,
  `create_user` decimal(19,0) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_user` decimal(19,0) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(9) NOT NULL,
  `type` bigint(10) DEFAULT NULL,
  `rate` int(9) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pay_rate_info_name_uni_inx` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=263 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_refund_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_refund_order`;
CREATE TABLE `pay_refund_order` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键id,自增长',
  `order_code` varchar(64) DEFAULT NULL COMMENT '订单号',
  `refund_code` varchar(64) DEFAULT NULL COMMENT '支付中心退款号',
  `third_refund_code` varchar(64) DEFAULT NULL COMMENT '第三方退款单号',
  `third_trade_code` varchar(64) DEFAULT NULL COMMENT '第三方交易号',
  `partner_order_no` varchar(64) DEFAULT NULL COMMENT '接入方订单号',
  `partner_refund_no` varchar(64) DEFAULT NULL COMMENT '接入方退款号',
  `dest_pay_type` int(20) DEFAULT NULL COMMENT '支付服务id',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `third_refund_time` datetime DEFAULT NULL COMMENT '第三方系统退款时间',
  `third_create_time` datetime DEFAULT NULL COMMENT '第三方退款创建时间',
  `status` int(11) DEFAULT NULL COMMENT '退款单状态\r\n            1:退款成功\r\n            2:退款处理中\r\n            3:退款失败\r\n            7:退款单关闭\r\n            8:异常退款',
  `fee` int(9) DEFAULT NULL COMMENT '退款金额(单位分)',
  `real_fee` int(9) DEFAULT NULL COMMENT '三方实退金额(单位分)',
  `user_id` varchar(64) DEFAULT NULL COMMENT '用户id',
  `partner_id` int(11) DEFAULT NULL COMMENT '接入方系统id',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '接入方异步回调URL',
  `subject` varchar(62) DEFAULT NULL COMMENT '商品的名称',
  `extend_params` varchar(4000) DEFAULT NULL COMMENT '扩展参数',
  `sign_corp_id` int(11) DEFAULT NULL COMMENT '签约公司id',
  `extra_common_param` varchar(1000) DEFAULT NULL COMMENT '公用回传参数,会将此参数通知给业务方',
  `reason` varchar(200) DEFAULT NULL COMMENT '退款理由',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `refund_mode` int(9) DEFAULT NULL COMMENT '1:测试订单,其他:都是正式订单',
  `error_msg` varchar(400) DEFAULT NULL COMMENT '第三方退款错误信息,退款成功不记录,格式为 错误码:错误内容',
  `account_id` int(11) DEFAULT NULL COMMENT '新的商户号id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `refund_order_refund_code_index` (`refund_code`),
  KEY `refund_order_partner_refund_no_index` (`partner_refund_no`),
  KEY `refund_order_order_code_index` (`order_code`),
  KEY `refund_order_dest_pay_type_index` (`dest_pay_type`),
  KEY `refund_order_partner_id_index` (`partner_id`),
  KEY `refund_order_create_time_index` (`create_time`),
  KEY `refund_order_user_id_index` (`user_id`),
  KEY `refund_order_third_refund_code_index` (`third_refund_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2009775 DEFAULT CHARSET=utf8 COMMENT='退款单表,用于记录退款信息';

-- ----------------------------
-- Table structure for pay_refund_order_1218
-- ----------------------------
DROP TABLE IF EXISTS `pay_refund_order_1218`;
CREATE TABLE `pay_refund_order_1218` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键id,自增长',
  `order_code` varchar(64) DEFAULT NULL COMMENT '订单号',
  `refund_code` varchar(64) DEFAULT NULL COMMENT '支付中心退款号',
  `third_refund_code` varchar(64) DEFAULT NULL COMMENT '第三方退款单号',
  `third_trade_code` varchar(64) DEFAULT NULL COMMENT '第三方交易号',
  `partner_order_no` varchar(64) DEFAULT NULL COMMENT '接入方订单号',
  `partner_refund_no` varchar(64) DEFAULT NULL COMMENT '接入方退款号',
  `dest_pay_type` int(20) DEFAULT NULL COMMENT '支付服务id',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `third_refund_time` datetime DEFAULT NULL COMMENT '第三方系统退款时间',
  `third_create_time` datetime DEFAULT NULL COMMENT '第三方退款创建时间',
  `status` int(11) DEFAULT NULL COMMENT '退款单状态\r\n            1:退款成功\r\n            2:退款处理中\r\n            3:退款失败\r\n            7:退款单关闭\r\n            8:异常退款',
  `fee` int(9) DEFAULT NULL COMMENT '退款金额(单位分)',
  `real_fee` int(9) DEFAULT NULL COMMENT '三方实退金额(单位分)',
  `user_id` varchar(64) DEFAULT NULL COMMENT '用户id',
  `partner_id` int(11) DEFAULT NULL COMMENT '接入方系统id',
  `notify_url` varchar(255) DEFAULT NULL COMMENT '接入方异步回调URL',
  `subject` varchar(62) DEFAULT NULL COMMENT '商品的名称',
  `extend_params` varchar(4000) DEFAULT NULL COMMENT '扩展参数',
  `sign_corp_id` int(11) DEFAULT NULL COMMENT '签约公司id',
  `extra_common_param` varchar(1000) DEFAULT NULL COMMENT '公用回传参数,会将此参数通知给业务方',
  `reason` varchar(200) DEFAULT NULL COMMENT '退款理由',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `refund_mode` int(9) DEFAULT NULL COMMENT '1:测试订单,其他:都是正式订单',
  `error_msg` varchar(400) DEFAULT NULL COMMENT '第三方退款错误信息,退款成功不记录,格式为 错误码:错误内容',
  `account_id` int(11) DEFAULT NULL COMMENT '新的商户号id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `refund_order_refund_code_index` (`refund_code`),
  KEY `refund_order_partner_refund_no_index` (`partner_refund_no`),
  KEY `refund_order_order_code_index` (`order_code`),
  KEY `refund_order_dest_pay_type_index` (`dest_pay_type`),
  KEY `refund_order_partner_id_index` (`partner_id`),
  KEY `refund_order_create_time_index` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=20631 DEFAULT CHARSET=utf8 COMMENT='退款单表,用于记录退款信息';

-- ----------------------------
-- Table structure for pay_route_accesser_service
-- ----------------------------
DROP TABLE IF EXISTS `pay_route_accesser_service`;
CREATE TABLE `pay_route_accesser_service` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `accesser_id` int(20) NOT NULL COMMENT '接入方id',
  `payment_type_id` int(20) NOT NULL COMMENT '支付类型id',
  `sign_corp_id` int(10) NOT NULL COMMENT '签约公司id',
  `service_id` int(20) NOT NULL COMMENT '支付服务id',
  `weight` int(10) DEFAULT '0' COMMENT '权重',
  `status` int(10) DEFAULT '0' COMMENT '状态位,0无效,1有效',
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=440 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_service_account
-- ----------------------------
DROP TABLE IF EXISTS `pay_service_account`;
CREATE TABLE `pay_service_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account` varchar(128) NOT NULL COMMENT '商户号',
  `account_name` varchar(128) DEFAULT NULL COMMENT '商户名称',
  `sign_corp_id` int(10) NOT NULL COMMENT '签约公司',
  `service_id` int(10) NOT NULL COMMENT '支付服务',
  `partner_id` varchar(128) NOT NULL COMMENT '合约方',
  `pay_key` varchar(1024) NOT NULL COMMENT '支付秘钥',
  `notify_key` varchar(1024) DEFAULT NULL COMMENT '通知秘钥',
  `pay_sign_type` varchar(10) DEFAULT NULL COMMENT '支付加密方式',
  `notify_sign_type` varchar(10) DEFAULT NULL COMMENT '通知加密参数',
  `ext1` varchar(1024) DEFAULT NULL COMMENT '扩展参数1',
  `ext2` varchar(1024) DEFAULT NULL COMMENT '扩展参数2',
  `ext3` varchar(1024) DEFAULT NULL COMMENT '扩展参数3',
  `ext4` varchar(1024) DEFAULT NULL COMMENT '扩展参数4',
  `ext5` varchar(1024) DEFAULT NULL COMMENT '扩展参数5',
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8 COMMENT='商户号配置表';

-- ----------------------------
-- Table structure for pay_service_account_route
-- ----------------------------
DROP TABLE IF EXISTS `pay_service_account_route`;
CREATE TABLE `pay_service_account_route` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `account_id` int(10) NOT NULL COMMENT '商户号ID',
  `accesser_id` int(10) NOT NULL COMMENT '接入方id',
  `payment_type_id` int(10) NOT NULL COMMENT '支付类型id',
  `sign_corp_id` int(10) NOT NULL COMMENT '签约公司id',
  `service_id` int(10) NOT NULL COMMENT '支付服务id',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '状态位,0无效,1有效',
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_route_accesser_type` (`accesser_id`,`payment_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=360 DEFAULT CHARSET=utf8 COMMENT='支付服务商户号关联表';

-- ----------------------------
-- Table structure for pay_service_account_serv
-- ----------------------------
DROP TABLE IF EXISTS `pay_service_account_serv`;
CREATE TABLE `pay_service_account_serv` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `account_id` bigint(20) DEFAULT NULL COMMENT '新商户号配置ID',
  `service_id` bigint(20) DEFAULT NULL COMMENT '支付服务ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_accountid_serviceid_uniq` (`account_id`,`service_id`),
  KEY `idx_accountser_accountid` (`account_id`)
) ENGINE=InnoDB AUTO_INCREMENT=151 DEFAULT CHARSET=utf8 COMMENT='支付服务与商户号关联表';

-- ----------------------------
-- Table structure for pay_service_corp_conf
-- ----------------------------
DROP TABLE IF EXISTS `pay_service_corp_conf`;
CREATE TABLE `pay_service_corp_conf` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `conf_name` varchar(64) DEFAULT NULL COMMENT '服务名称',
  `service_id` int(20) NOT NULL COMMENT '支付服务id',
  `sign_corp_id` int(10) NOT NULL COMMENT '签约公司id',
  `account` varchar(128) NOT NULL COMMENT '签约商户id',
  `pay_key` varchar(1024) NOT NULL COMMENT '商户秘钥',
  `pay_sign_type` varchar(10) DEFAULT NULL COMMENT '通知秘钥加密类型：MD5、RSA、DSA',
  `notify_key` varchar(1024) DEFAULT NULL COMMENT '通知秘钥',
  `notify_sign_type` varchar(10) DEFAULT NULL COMMENT '通知秘钥加密类型：MD5、RSA、DSA',
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `ext1` varchar(1024) DEFAULT NULL COMMENT '扩展字段1',
  `ext2` varchar(1024) DEFAULT NULL COMMENT '扩展字段2',
  `ext3` varchar(1024) DEFAULT NULL COMMENT '扩展字段3',
  `ext4` varchar(1024) DEFAULT NULL COMMENT '扩展字段4',
  `ext5` varchar(1024) DEFAULT NULL COMMENT '扩展字段5',
  `rate_info_id` int(11) DEFAULT NULL COMMENT '费率id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `service_id` (`service_id`,`sign_corp_id`)
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_service_corp_rate
-- ----------------------------
DROP TABLE IF EXISTS `pay_service_corp_rate`;
CREATE TABLE `pay_service_corp_rate` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `sign_corp_id` int(10) NOT NULL COMMENT '签约公司id',
  `service_id` int(20) NOT NULL COMMENT '支付服务id',
  `payment_type_id` int(20) DEFAULT NULL COMMENT '支付类型id',
  `rate_info_id` int(11) DEFAULT NULL COMMENT '费率id',
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `rate_info_id2` int(11) DEFAULT NULL COMMENT '费率id_2',
  `is_extended` varchar(4) DEFAULT '0' COMMENT '是否有扩展信息,0:否,1:有',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=339 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_service_product
-- ----------------------------
DROP TABLE IF EXISTS `pay_service_product`;
CREATE TABLE `pay_service_product` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `service_id` int(20) NOT NULL COMMENT '支付服务id',
  `sign_corp_id` int(10) DEFAULT NULL COMMENT '签约公司id',
  `bank_id` int(19) DEFAULT NULL COMMENT '银行、服务机构id',
  `product_no` varchar(32) NOT NULL COMMENT '产品编码',
  `product_name` varchar(64) NOT NULL COMMENT '产品名称',
  `product_price` int(9) NOT NULL DEFAULT '0' COMMENT '产品价格，分',
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=229 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_sign_corporation
-- ----------------------------
DROP TABLE IF EXISTS `pay_sign_corporation`;
CREATE TABLE `pay_sign_corporation` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `corp_name` varchar(128) NOT NULL,
  `create_user` decimal(19,0) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_user` decimal(19,0) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `version` int(9) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_sms_contract
-- ----------------------------
DROP TABLE IF EXISTS `pay_sms_contract`;
CREATE TABLE `pay_sms_contract` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `assign_ratio` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `operater_bad_dept` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `operater_collection` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `operator` decimal(22,0) NOT NULL,
  `proportion_tax` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `unbalance_fee` varchar(60) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `user_id` decimal(19,0) NOT NULL,
  `version` decimal(22,0) NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=266 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_sms_exception_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_sms_exception_order`;
CREATE TABLE `pay_sms_exception_order` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `business_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `fee` int(9) NOT NULL,
  `deduct_time` datetime DEFAULT NULL,
  `partner_id` decimal(19,0) NOT NULL,
  `batch_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `cause_type` int(9) NOT NULL,
  `status` int(9) NOT NULL,
  `memo` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` int(9) NOT NULL,
  `create_user` decimal(19,0) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_user` decimal(19,0) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_sms_order
-- ----------------------------
DROP TABLE IF EXISTS `pay_sms_order`;
CREATE TABLE `pay_sms_order` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `business_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `fee` int(9) NOT NULL,
  `deduct_time` datetime NOT NULL,
  `partner_id` decimal(19,0) NOT NULL,
  `batch_no` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `mobile_status` int(9) NOT NULL,
  `is_verify` int(9) NOT NULL,
  `verify_time` datetime DEFAULT NULL,
  `version` int(9) NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_statement_conf
-- ----------------------------
DROP TABLE IF EXISTS `pay_statement_conf`;
CREATE TABLE `pay_statement_conf` (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键自增长',
  `account` varchar(128) DEFAULT NULL COMMENT '第三方的提供的商户号',
  `pay_key` varchar(1024) DEFAULT NULL COMMENT '密钥',
  `sign_corp_id` int(10) DEFAULT NULL COMMENT '签约公司id',
  `partner_id` int(10) DEFAULT NULL COMMENT '合约方id',
  `type` int(4) DEFAULT NULL COMMENT '对账类型 1:支付宝对账  2:财付通对账',
  `status` int(4) DEFAULT NULL COMMENT '对账开启状态,0:关闭,1:开启',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `service_conf_id` int(10) DEFAULT NULL COMMENT '服务配置信息ID',
  `version` int(10) DEFAULT NULL COMMENT '版本号',
  `account_id` int(10) DEFAULT NULL COMMENT '新的商户号id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8 COMMENT='对账功能配置表';

-- ----------------------------
-- Table structure for pay_trade_exception
-- ----------------------------
DROP TABLE IF EXISTS `pay_trade_exception`;
CREATE TABLE `pay_trade_exception` (
  `id` int(20) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `partner_order_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `third_trade_code` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `source_pay_type` int(9) DEFAULT NULL,
  `dest_pay_type` int(9) DEFAULT NULL,
  `status` int(9) NOT NULL,
  `fee` int(9) NOT NULL,
  `sub_fee` int(9) DEFAULT NULL,
  `user_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `subject` varchar(62) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `version` decimal(22,0) DEFAULT '0',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `partner_id` int(9) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_uid_change
-- ----------------------------
DROP TABLE IF EXISTS `pay_uid_change`;
CREATE TABLE `pay_uid_change` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `old_uid` bigint(19) DEFAULT NULL,
  `new_uid` bigint(19) DEFAULT NULL,
  `status` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1402 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pay_wechat_app
-- ----------------------------
DROP TABLE IF EXISTS `pay_wechat_app`;
CREATE TABLE `pay_wechat_app` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `alias` varchar(64) DEFAULT NULL COMMENT '别名',
  `app_id` varchar(1024) DEFAULT NULL COMMENT '应用id号',
  `app_key` varchar(1024) DEFAULT NULL COMMENT '应用密钥',
  `app_secret` varchar(1024) DEFAULT NULL COMMENT '应用凭证',
  `ext1` varchar(1024) DEFAULT NULL COMMENT '扩展字段1',
  `ext2` varchar(1024) DEFAULT NULL COMMENT '扩展字段2',
  `ext3` varchar(1024) DEFAULT NULL COMMENT '扩展字段3',
  `status` int(4) NOT NULL COMMENT '状态: 0:无效 1:有效',
  `version` int(20) NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `description` varchar(400) DEFAULT NULL COMMENT '描述信息',
  PRIMARY KEY (`id`),
  UNIQUE KEY `pay_wechat_app_alias_unique_index` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='微信app产品配置';

-- ----------------------------
-- Table structure for pay_wechat_msg
-- ----------------------------
DROP TABLE IF EXISTS `pay_wechat_msg`;
CREATE TABLE `pay_wechat_msg` (
  `id` int(20) NOT NULL AUTO_INCREMENT COMMENT '主键 自增长',
  `partner` varchar(32) NOT NULL COMMENT '接入方编码',
  `trans_no` varchar(64) DEFAULT NULL COMMENT '交易编号',
  `extend_params` varchar(4000) DEFAULT NULL COMMENT '扩展参数',
  `open_id` varchar(256) DEFAULT NULL COMMENT '微信openid,每个公众号对应openid的都不同',
  `url` varchar(512) DEFAULT NULL COMMENT '详情页链接',
  `status` int(3) DEFAULT NULL COMMENT '状态: 0:未发送/发送中,-1:请求失败,1:请求成功,-2:发送失败,2:发送成功,-3:用户拒收(发送失败)',
  `template_id` varchar(256) DEFAULT NULL COMMENT '应收金额,支付中心总金额',
  `msg_id` varchar(256) DEFAULT NULL COMMENT 'msgId 微信返回唯一标示',
  `msg_type` varchar(64) DEFAULT NULL COMMENT '消息类型: event:表示模板消息推送',
  `result_desc` varchar(512) DEFAULT NULL COMMENT '结果描述,如果出错显示错误信息 格式 errorCode:errorMsg',
  `content` varchar(4000) DEFAULT NULL COMMENT '消息内容',
  `version` int(10) DEFAULT NULL COMMENT '版本号',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_trans_no` (`trans_no`)
) ENGINE=InnoDB AUTO_INCREMENT=376206 DEFAULT CHARSET=utf8 COMMENT='微信模板信息记录表';

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `order_code` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
