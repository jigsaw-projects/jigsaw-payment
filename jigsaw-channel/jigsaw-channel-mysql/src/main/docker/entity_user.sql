
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for entity_user
-- ----------------------------
DROP TABLE IF EXISTS `entity_user`;
CREATE TABLE `entity_user` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `id` bigint(20) NOT NULL COMMENT '用户id',
  `user_name` varchar(128) NOT NULL COMMENT '用户名',
  `nickname` varchar(128)  COMMENT '昵称', 
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '用户状态',
  `security_phone` varchar(32) DEFAULT NULL COMMENT '安全手机',
  `security_email` varchar(128) DEFAULT NULL COMMENT '安全邮箱',
  `identified` tinyint(4) DEFAULT 0 COMMENT '用户是否已经识别为客户',
  `security_level` tinyint(4) DEFAULT 0 COMMENT '用户安全等级',
  `is_trusted` tinyint(4) DEFAULT 0 COMMENT '是否托管账户',
  `customer_id` bigint(20) COMMENT '对应的客户id', 
  `register_date` datetime DEFAULT NULL COMMENT '开户日期',
  `activated_date` datetime DEFAULT NULL COMMENT '激活日期',
  `cancelled_date` datetime DEFAULT NULL COMMENT '销户日期',
  `cancelled_reason` varchar(255) DEFAULT NULL COMMENT '销户原因',
  `register_channel` tinyint(4) DEFAULT NULL COMMENT '注册渠道',
  `register_info` varchar(255) DEFAULT NULL COMMENT '预留信息',
  `last_log_ip` varchar(32) DEFAULT NULL COMMENT '最后登录IP',
  `last_log_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_user_id` (`id`) USING BTREE,
  UNIQUE KEY `idx_username` (`user_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户基本信息(实体)';

-- ----------------------------
-- Table structure for user_payment_password
-- ----------------------------
DROP TABLE IF EXISTS `user_payment_password`;
CREATE TABLE `user_payment_password` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `password_type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '密码类型',
  `locked_time` datetime  COMMENT '密码被锁定时间', 
  `locked_reason` varchar(128)  COMMENT '锁定原因',
  `salt` char(8) DEFAULT NULL COMMENT '密码加密用的盐值',
  `password` char(32) NOT NULL COMMENT '加密后密码',
  `is_init` char(1) DEFAULT 0 COMMENT '是否初始密码',
  `is_changed` char(1) DEFAULT 0 COMMENT '是否变更了密码',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_upp_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户支付密码';

-- ----------------------------
-- Table structure for user_login_password
-- ----------------------------
DROP TABLE IF EXISTS `user_login_password`;
CREATE TABLE `user_login_password` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `password_type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '密码类型',
  `locked_time` datetime  COMMENT '密码被锁定时间', 
  `locked_reason` varchar(128)  COMMENT '锁定原因',
  `salt` char(8) DEFAULT NULL COMMENT '密码加密用的盐值',
  `password` char(32) NOT NULL COMMENT '加密后密码',
  `is_init` char(1) DEFAULT 0 COMMENT '是否初始密码',
  `is_changed` char(1) DEFAULT 0 COMMENT '是否变更了密码',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_upp_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登录密码';