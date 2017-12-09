
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for entity_user
-- ----------------------------
DROP TABLE IF EXISTS `entity_user`;
CREATE TABLE `entity_user` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `id` bigint(20) NOT NULL COMMENT '用户id',
  `user_name` varchar(128) NOT NULL COMMENT '用户名',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '用户状态',
  `security_phone` varchar(32) DEFAULT NULL COMMENT '安全手机',
  `security_email` varchar(128) DEFAULT NULL COMMENT '安全邮箱',
  `identified` tinyint(4) DEFAULT NULL COMMENT '用户是否已经识别为客户',
  `security_level` tinyint(4) DEFAULT NULL COMMENT '用户安全等级',
  `verified_by_phone` tinyint(4) DEFAULT NULL COMMENT '是否手机号认证',
  `verified_time` datetime DEFAULT NULL COMMENT '手机号认证时间',
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
