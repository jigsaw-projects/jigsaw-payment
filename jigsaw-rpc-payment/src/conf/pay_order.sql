SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pay_order_0
-- ----------------------------
DROP TABLE IF EXISTS `pay_order_0`;
CREATE TABLE `pay_order_0` (
  `key` bigint(20) NOT NULL COMMENT '内部主键，自增型',
  `id` bigint(20) NOT NULL COMMENT '交易流水号，用于分表分库',
  `version` bigint(20) NOT NULL DEFAULT '0',
  `app_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '发起交易的app id',
  `pay_scenarios` int(4) NOT NULL COMMENT '发起交易的场景',
  `notify_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '异步通知url',
  `return_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '同步通知url',
  `status` int(9) NOT NULL COMMENT '订单状态',
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `pay_time` datetime NOT NULL COMMENT '支付时间',
  `error_code` varchar(32) NOT NULL COMMENT '如果支付失败，这里需记录错误码',
  `error_detail` varchar(128) NOT NULL COMMENT '详细错误信息，如果支付失败，需要在这里记录错误信息',
  `partner_order_no` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '接入方订单号',
  `pay_mode` tinyint(2) NOT NULL COMMENT '0.正常订单，1.代表测试订单',
  `order_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '订单ID，记录用户购买的产品信息（需使用产品查询接口）',
  `order_title` varchar(62) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '订单名称',
  `order_detail` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '订单描述',
  `order_show_url` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '订单显示的url地址',
  `fee` int(9) NOT NULL COMMENT '预期金额',
  `fee_real` int(9) NOT NULL COMMENT '实际支付总金额',
  `fee_unit` tinyint(2) NOT NULL DEFAULT '1' COMMENT '货币类型 ',
  `sub_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '交易主体不仅仅是用户，也可以是商户',
  `sub_type` int(9) NOT NULL COMMENT '交易主体类型',
  `sub_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '交易主体名称',
  `sub_account_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '交易主体账号',
  `sub_account_type` int(10) NOT NULL COMMENT '交易主体使用的账号类型；',
  `sub_ip` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '交易主体ip地址',
  `sub_mobile` varchar(32) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '交易主体使用的手机号',
  `sub_client_code` varchar(16) NOT NULL COMMENT '交易主体的客户端代码，发起交易的平台代码，参考公司标准代码规范',
  `sub_device_id` varchar(32) NOT NULL COMMENT '交易主体使用的设备',
  `sub_email` varchar(128) NOT NULL COMMENT '交易主体的邮箱【预留】',
  `sub_location` varchar(128) NOT NULL COMMENT '交易主体在交易发生时所在的位置【预留】',
  `sub_country_code` varchar(128) NOT NULL COMMENT '交易主体所在的国家代码，默认为中国【预留】',
  `partner_id` int(9) NOT NULL COMMENT '交易对手的Id,接入方ID',
  `partner_type` int(9) NOT NULL COMMENT '交易对手的类型',
  `partner_account_id` int(9) NOT NULL COMMENT '交易对手的账号,商户号pay_service_account的ID',
  `partner_name` varchar(32) NOT NULL COMMENT '交易对手的名称',
  `source_pay_type` int(9) NOT NULL COMMENT '用户选择的交易渠道实体编码',
  `dest_pay_type` int(9) NOT NULL COMMENT '实际执行的交易渠道虚拟账户编码',
  `third_create_time` datetime NOT NULL COMMENT '第三方创建时间',
  `third_pay_time` datetime NOT NULL COMMENT '第三方创建时间',
  `third_trade_code` varchar(256) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '第三方交易订单号',
  `current_key` int(9) NOT NULL,
  PRIMARY KEY (`key`),
  UNIQUE KEY `idx_pay_order_id` (`id`) USING BTREE,
  KEY `idx_pay_order_sub_id` (`sub_id`),
  KEY `idx_pay_order_partner_id` (`partner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `pay_order_1`;
CREATE TABLE `pay_order_1` like `pay_order_0` ;

DROP TABLE IF EXISTS `pay_order_2`;
CREATE TABLE `pay_order_2` like `pay_order_0` ;

DROP TABLE IF EXISTS `pay_order_3`;
CREATE TABLE `pay_order_3` like `pay_order_0` ;

DROP TABLE IF EXISTS `pay_order_4`;
CREATE TABLE `pay_order_4` like `pay_order_0` ;

DROP TABLE IF EXISTS `pay_order_5`;
CREATE TABLE `pay_order_5` like `pay_order_0` ;

DROP TABLE IF EXISTS `pay_order_6`;
CREATE TABLE `pay_order_6` like `pay_order_0` ;

DROP TABLE IF EXISTS `pay_order_7`;
CREATE TABLE `pay_order_7` like `pay_order_0` ;

DROP TABLE IF EXISTS `pay_order_8`;
CREATE TABLE `pay_order_8` like `pay_order_0` ;

DROP TABLE IF EXISTS `pay_order_9`;
CREATE TABLE `pay_order_9` like `pay_order_0` ;
