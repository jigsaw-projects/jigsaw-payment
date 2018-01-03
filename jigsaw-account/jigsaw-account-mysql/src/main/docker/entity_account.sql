
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for entity_account
-- ----------------------------
DROP TABLE IF EXISTS `entity_account`;
CREATE TABLE `entity_customer` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `id` bigint(20) NOT NULL COMMENT '客户ID',
  `full_name` varchar(50) DEFAULT NULL,
  `gender` tinyint(4) DEFAULT NULL COMMENT '性别代码',
  `auth_state` tinyint(4) DEFAULT NULL COMMENT '实名状态',
  `auth_time` datetime DEFAULT NULL COMMENT '实名认证时间 (最后一次客户认证时间)',
  `canceled_date` datetime DEFAULT NULL COMMENT '销户日期',
  `canceled_reason` varchar(255) DEFAULT NULL COMMENT '销户原因',
  `user_count` int(11) DEFAULT '0' COMMENT '用户数',
  `valid_user_count` int(11) DEFAULT '0' COMMENT '有效用户数',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_cust_id` (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='个人客户基本信息(实体)';
