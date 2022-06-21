--liquibase formatted sql
--changeset JasonYe:Release0100-1
DROP TABLE IF EXISTS `manage_env`;
CREATE TABLE `manage_env`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `env_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '显示环境名称',
  `k8s_config` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'kubectl操作所需文件内容',
  `is_prod` int(11) NULL DEFAULT NULL COMMENT '是否为生产环境',
  `create_time` timestamp(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` timestamp(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `env_name_unique`(`env_name`) USING BTREE COMMENT '环境名称不允许重复'
) ENGINE = InnoDB AUTO_INCREMENT=1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;


-- SET FOREIGN_KEY_CHECKS = 1;
