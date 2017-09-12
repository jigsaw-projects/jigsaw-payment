
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for entity_user
-- ----------------------------
DROP TABLE IF EXISTS `entity_user`;
CREATE TABLE `entity_user` (
 `key` 			bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `id` 			bigint(20) NOT NULL COMMENT '用户id',
 
  `username`	varchar(128) NOT NULL COMMENT '用户名',
  `password`	varchar(128) NOT NULL COMMENT '密码',
    `version` bigint(12) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_user_id` (`id`) USING BTREE,
  UNIQUE KEY `idx_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
