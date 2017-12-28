
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `login_cert_type` tinyint(4) DEFAULT NULL COMMENT '登录标识类型',
  `login_cert_code` varchar(50) DEFAULT NULL COMMENT '登录时使用的凭证（手机号、邮箱、昵称）',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `version` int(11) NOT NULL DEFAULT '1' COMMENT '版本号',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登陆标识';
