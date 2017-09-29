
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for pay_account_0
-- ----------------------------
DROP TABLE IF EXISTS `pay_account_0`;
CREATE TABLE `pay_account_0` (
  `key` bigint(20) NOT NULL COMMENT '账户ID，内部主键',
  `id` bigint(20) NOT NULL COMMENT '账户号，实际主键',
  `owner_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '签约并支付时关联的订单号',
  `type` tinyint(2) NOT NULL DEFAULT '2' COMMENT '账户类型',
  `account_title` bigint(20) NOT NULL DEFAULT '0' COMMENT '会计科目代码',
  `fee_unit` tinyint(4) NOT NULL DEFAULT '1' COMMENT '货币类型 1、人民币 2、积分 3、代金券 4、美元 5、台币',
  `third_type` int(8) NOT NULL DEFAULT '0' COMMENT '第三方渠道ID，pay_partner表维护',
  `third_account` varchar(32) NOT NULL DEFAULT '' COMMENT '第三方的用户账户',
  `third_param` varchar(1024) NOT NULL DEFAULT '' COMMENT '第三方凭证信息，通用字段，每个渠道区别维护',
  `balance` bigint(12) NOT NULL DEFAULT '0' COMMENT '余额',
  `frozen` bigint(12) NOT NULL DEFAULT '0' COMMENT '冻结金额',
  `income` bigint(12) NOT NULL DEFAULT '0' COMMENT '入账总额',
  `outcome` bigint(12) NOT NULL DEFAULT '0' COMMENT '出账总额',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态：0：创建；1、激活；2：冻结；3：销毁',
  `rank` tinyint(2) NOT NULL DEFAULT '0' COMMENT '账户等级',
  `notification` tinyint(2) NOT NULL DEFAULT '0' COMMENT '通知方式',
  `permissions` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '账户权限，可以bit位扩展',
  `risk_level` tinyint(2) NOT NULL DEFAULT '0' COMMENT '安全等级',
  `sandbox` tinyint(2) NOT NULL DEFAULT '0' COMMENT '沙盒账户：0：否；1：是',
	
  `version` bigint(12) NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   PRIMARY KEY (`key`),
  UNIQUE KEY `idx_account_id` (`id`) USING BTREE,
  KEY `idx_account_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `pay_account_1`;
CREATE TABLE `pay_account_1` like `pay_account_0` ;

DROP TABLE IF EXISTS `pay_account_2`;
CREATE TABLE `pay_account_2` like `pay_account_0` ;

DROP TABLE IF EXISTS `pay_account_3`;
CREATE TABLE `pay_account_3` like `pay_account_0` ;

DROP TABLE IF EXISTS `pay_account_4`;
CREATE TABLE `pay_account_4` like `pay_account_0` ;

DROP TABLE IF EXISTS `pay_account_5`;
CREATE TABLE `pay_account_5` like `pay_account_0` ;

DROP TABLE IF EXISTS `pay_account_6`;
CREATE TABLE `pay_account_6` like `pay_account_0` ;

DROP TABLE IF EXISTS `pay_account_7`;
CREATE TABLE `pay_account_7` like `pay_account_0` ;

DROP TABLE IF EXISTS `pay_account_8`;
CREATE TABLE `pay_account_8` like `pay_account_0` ;

DROP TABLE IF EXISTS `pay_account_9`;
CREATE TABLE `pay_account_9` like `pay_account_0` ;
