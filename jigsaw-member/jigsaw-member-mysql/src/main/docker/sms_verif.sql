
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sms_verif
-- ----------------------------
DROP TABLE IF EXISTS `sms_verif`;
CREATE TABLE `sms_verif` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `id` bigint(20) NOT NULL COMMENT '短息验证码信息ID ',
  `receiver_no` char(11) NOT NULL COMMENT '目标手机号码',
  `receipt_code` char(6) NOT NULL COMMENT '验证码回执编号',
  `verif_code` char(6) NOT NULL COMMENT '短信验证码',
  `auth_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '鉴权状态',
  `pre_auth_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '预鉴权状态',
  `expiration` datetime NOT NULL COMMENT '验证码的有效截止时间',
  `msg_type` tinyint(4) NOT NULL COMMENT '短信类型',
  `sys_id` tinyint(4) NOT NULL COMMENT '请求系统编码',
  `version` int(11) DEFAULT '1' COMMENT '版本号',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信验证码信息表';
