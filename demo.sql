/*
 Navicat Premium Data Transfer

 Source Server         : demo
 Source Server Type    : MySQL
 Source Server Version : 50741
 Source Host           : localhost:3306
 Source Schema         : demo

 Target Server Type    : MySQL
 Target Server Version : 50741
 File Encoding         : 65001

 Date: 25/04/2024 11:01:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hardwaresetting
-- ----------------------------
DROP TABLE IF EXISTS `hardwaresetting`;
CREATE TABLE `hardwaresetting`  (
  `HardwareSettingId` int(11) NOT NULL AUTO_INCREMENT,
  `HardwareName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`HardwareSettingId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of hardwaresetting
-- ----------------------------
INSERT INTO `hardwaresetting` VALUES (1, '摄像头,烟雾传感器,驱动系统');
INSERT INTO `hardwaresetting` VALUES (2, '仅摄像头');

-- ----------------------------
-- Table structure for importantpic
-- ----------------------------
DROP TABLE IF EXISTS `importantpic`;
CREATE TABLE `importantpic`  (
  `ImportantPicId` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NULL DEFAULT NULL,
  `Message` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `isRead` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `setTime` timestamp NULL DEFAULT NULL,
  `picId` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ImportantPicId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of importantpic
-- ----------------------------
INSERT INTO `importantpic` VALUES (19, 2, NULL, '0', '2024-04-09 20:15:20', 6);
INSERT INTO `importantpic` VALUES (20, 2, NULL, '0', '2024-04-09 20:15:22', 7);
INSERT INTO `importantpic` VALUES (21, 2, NULL, '0', '2024-04-09 20:15:47', 11);

-- ----------------------------
-- Table structure for screenshotrecord
-- ----------------------------
DROP TABLE IF EXISTS `screenshotrecord`;
CREATE TABLE `screenshotrecord`  (
  `ScreenshotRecordId` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NULL DEFAULT NULL,
  `TerritoryId` int(11) NULL DEFAULT NULL,
  `ScreenshotName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ScreenshotPath` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `IsImportant` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ScreenshotRecordId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 24 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of screenshotrecord
-- ----------------------------
INSERT INTO `screenshotrecord` VALUES (1, 2, 9, 'smoke_face_9_1712219880.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-4\\smoke_face_9_1712219880.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (2, 2, 9, 'smoke_face_9_1712220161.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-4\\smoke_face_9_1712220161.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (3, 2, 9, 'smoke_face_9_1712220238.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-4\\smoke_face_9_1712220238.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (4, 2, 9, 'smoke_face_9_1712220564.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-4\\smoke_face_9_1712220564.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (5, 2, 9, 'smoke_face_9_1712220652.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-4\\smoke_face_9_1712220652.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (6, 2, 9, 'smoke_face_9_1712225042.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-4\\smoke_face_9_1712225042.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (7, 2, 9, 'smoke_face_9_1712225153.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-4\\smoke_face_9_1712225153.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (8, 2, 9, 'smoke_face_9_1712225327.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-4\\smoke_face_9_1712225327.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (9, 2, 9, 'smoke_face_9_1712391856.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-6\\smoke_face_9_1712391856.jpg', 'true');
INSERT INTO `screenshotrecord` VALUES (10, 2, 9, 'smoke_face_9_1712392292.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-6\\smoke_face_9_1712392292.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (11, 2, 9, 'smoke_face_9_1712392442.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-6\\smoke_face_9_1712392442.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (12, 2, 9, 'smoke_face_9_1712396131.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-6\\smoke_face_9_1712396131.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (13, 2, 9, 'smoke_face_9_1712398436.jpg', 'D:/Smoke/PictureHome\\9\\2024\\4-6\\smoke_face_9_1712398436.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (14, 2, 9, 'smoke_face_9_0.75439453125_1712728916.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-10\\smoke_face_9_0.75439453125_1712728916.jpg', 'true');
INSERT INTO `screenshotrecord` VALUES (15, 2, 9, 'smoke_face_9_0.716796875_1712733770.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-10\\smoke_face_9_0.716796875_1712733770.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (16, 2, 9, 'smoke_face_9_0.78466796875_1712898150.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-12\\smoke_face_9_0.78466796875_1712898150.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (17, 2, 9, 'smoke_face_9_0.791015625_1712902984.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-12\\smoke_face_9_0.791015625_1712902984.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (18, 2, 9, 'smoke_face_9_0.9072265625_1712903887.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-12\\smoke_face_9_0.9072265625_1712903887.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (19, 2, 9, 'smoke_face_9_0.875_1712904356.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-12\\smoke_face_9_0.875_1712904356.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (20, 2, 9, 'smoke_face_9_0.892578125_1713016553.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-13\\smoke_face_9_0.892578125_1713016553.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (21, 2, 9, 'smoke_face_9_0.828125_1713022500.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-13\\smoke_face_9_0.828125_1713022500.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (22, 2, 9, 'smoke_face_9_0.82666015625_1713022548.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-13\\smoke_face_9_0.82666015625_1713022548.jpg', 'false');
INSERT INTO `screenshotrecord` VALUES (23, 2, 9, 'smoke_face_9_0.8291015625_1713167073.jpg', 'D:\\Smoke\\PictureHome\\9\\2024\\4-15\\smoke_face_9_0.8291015625_1713167073.jpg', 'false');

-- ----------------------------
-- Table structure for smokingrecord
-- ----------------------------
DROP TABLE IF EXISTS `smokingrecord`;
CREATE TABLE `smokingrecord`  (
  `RecordId` int(11) NOT NULL AUTO_INCREMENT,
  `TerritoryId` int(11) NOT NULL,
  `SmokeStartTime` timestamp NULL DEFAULT NULL,
  `ConfidenceLevel` double(5, 2) NULL DEFAULT NULL,
  `ScreenshotRecordId` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`RecordId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of smokingrecord
-- ----------------------------
INSERT INTO `smokingrecord` VALUES (1, 9, '2024-04-04 16:50:52', 0.88, 5);
INSERT INTO `smokingrecord` VALUES (2, 9, '2024-04-04 18:04:02', 0.81, 6);
INSERT INTO `smokingrecord` VALUES (3, 9, '2024-04-04 18:05:53', 0.87, 7);
INSERT INTO `smokingrecord` VALUES (4, 9, '2024-04-04 18:08:47', 0.90, 8);
INSERT INTO `smokingrecord` VALUES (5, 9, '2024-04-06 16:24:16', 0.89, 9);
INSERT INTO `smokingrecord` VALUES (6, 9, '2024-04-06 16:31:32', 0.75, 10);
INSERT INTO `smokingrecord` VALUES (7, 9, '2024-04-06 16:34:02', 0.80, 11);
INSERT INTO `smokingrecord` VALUES (8, 9, '2024-04-06 17:35:31', 0.74, 12);
INSERT INTO `smokingrecord` VALUES (9, 9, '2024-04-06 18:13:56', 0.76, 13);
INSERT INTO `smokingrecord` VALUES (10, 9, '2024-04-08 14:50:04', 0.81, 14);
INSERT INTO `smokingrecord` VALUES (11, 9, '2024-04-10 14:01:56', 0.75, 14);
INSERT INTO `smokingrecord` VALUES (12, 9, '2024-04-10 15:22:50', 0.72, 15);
INSERT INTO `smokingrecord` VALUES (13, 9, '2024-04-12 13:02:30', 0.78, 16);
INSERT INTO `smokingrecord` VALUES (14, 9, '2024-04-12 14:23:04', 0.79, 17);
INSERT INTO `smokingrecord` VALUES (15, 9, '2024-04-12 14:38:07', 0.91, 18);
INSERT INTO `smokingrecord` VALUES (16, 9, '2024-04-12 14:45:56', 0.88, 19);
INSERT INTO `smokingrecord` VALUES (17, 9, '2024-04-13 21:55:53', 0.89, 20);
INSERT INTO `smokingrecord` VALUES (18, 9, '2024-04-13 23:35:00', 0.83, 21);
INSERT INTO `smokingrecord` VALUES (19, 9, '2024-04-13 23:35:48', 0.83, 22);
INSERT INTO `smokingrecord` VALUES (20, 9, '2024-04-15 15:44:33', 0.83, 23);

-- ----------------------------
-- Table structure for territory
-- ----------------------------
DROP TABLE IF EXISTS `territory`;
CREATE TABLE `territory`  (
  `TerritoryId` int(11) NOT NULL AUTO_INCREMENT,
  `TerritoryName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `HardwareSettingId` int(11) NULL DEFAULT NULL,
  `TerritoryConfigurationId` int(11) NULL DEFAULT NULL,
  `StorageSize` double NULL DEFAULT NULL,
  `ConfidenceLevel` double NULL DEFAULT NULL,
  PRIMARY KEY (`TerritoryId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of territory
-- ----------------------------
INSERT INTO `territory` VALUES (8, '主房间', 2, 1, 100, 0.6);
INSERT INTO `territory` VALUES (9, '附卧室', 2, 1, 100, 0.65);
INSERT INTO `territory` VALUES (10, '二期', 1, 1, 50, 0.6);
INSERT INTO `territory` VALUES (11, '三期', 1, 1, 30, 0.75);
INSERT INTO `territory` VALUES (12, '宿舍', 1, 1, 30, 0.6);
INSERT INTO `territory` VALUES (13, '食堂', 1, 1, 30, 0.6);

-- ----------------------------
-- Table structure for territorychangerequest
-- ----------------------------
DROP TABLE IF EXISTS `territorychangerequest`;
CREATE TABLE `territorychangerequest`  (
  `ChangeRequestId` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NOT NULL COMMENT '用户id',
  `RequestedTerritoryId` int(11) NOT NULL COMMENT '需求的id',
  `RequestDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '请求开始时间',
  `ApprovalDate` timestamp NULL DEFAULT NULL COMMENT '批准日期',
  `ApproverId` int(11) NULL DEFAULT NULL COMMENT '批准者id',
  `Remarks` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '备注',
  `RequestStatus` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态',
  `territoryConfigurationId` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`ChangeRequestId`) USING BTREE,
  INDEX `idx_userid`(`UserId`) USING BTREE,
  INDEX `idx_requestedterritoryid`(`RequestedTerritoryId`) USING BTREE,
  INDEX `idx_approverid`(`ApproverId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of territorychangerequest
-- ----------------------------
INSERT INTO `territorychangerequest` VALUES (1, 2, 8, '2024-03-13 12:01:04', '2024-03-14 00:00:00', 1, NULL, 'refuse', 1);
INSERT INTO `territorychangerequest` VALUES (2, 2, 10, '2024-03-15 15:48:19', '2024-03-15 00:00:00', 1, NULL, 'agree', 2);
INSERT INTO `territorychangerequest` VALUES (3, 2, 9, '2024-03-15 15:58:03', '2024-03-15 00:00:00', 1, NULL, 'agree', 2);
INSERT INTO `territorychangerequest` VALUES (4, 2, 11, '2024-03-17 14:43:23', '2024-03-17 00:00:00', 1, NULL, 'agree', 1);
INSERT INTO `territorychangerequest` VALUES (5, 2, 10, '2024-03-17 15:36:24', '2024-03-17 00:00:00', 1, NULL, 'refuse', 2);
INSERT INTO `territorychangerequest` VALUES (6, 2, 10, '2024-03-17 15:39:16', '2024-03-17 00:00:00', 1, NULL, 'agree', 2);
INSERT INTO `territorychangerequest` VALUES (7, 2, 12, '2024-03-17 15:42:55', '2024-03-17 00:00:00', 1, NULL, 'refuse', 2);
INSERT INTO `territorychangerequest` VALUES (8, 4, 10, '2024-04-07 12:38:07', NULL, NULL, '112', 'pending', 1);

-- ----------------------------
-- Table structure for territoryconfiguration
-- ----------------------------
DROP TABLE IF EXISTS `territoryconfiguration`;
CREATE TABLE `territoryconfiguration`  (
  `TerritoryConfigurationId` int(11) NOT NULL AUTO_INCREMENT,
  `Action` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`TerritoryConfigurationId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of territoryconfiguration
-- ----------------------------
INSERT INTO `territoryconfiguration` VALUES (1, '静默记录');
INSERT INTO `territoryconfiguration` VALUES (2, '仅通知管理员');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `UserID` int(11) NOT NULL AUTO_INCREMENT,
  `Username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Role` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Salt` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `Telephone` varchar(14) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `regTime` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `enabled` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`UserID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'wzy', '1', '1', '1', '114', '2024-04-02 18:36:56', '1');
INSERT INTO `user` VALUES (2, 'wzy1', '0', '1', '123', '114', '2024-02-14 21:27:36', '1');
INSERT INTO `user` VALUES (3, 'wzy1213', '0', '12', '123', '114', '2024-02-14 21:31:42', '1');
INSERT INTO `user` VALUES (4, 'wzy11', '0', '1', '123', '13052765120', NULL, '1');

-- ----------------------------
-- Table structure for userfavorite
-- ----------------------------
DROP TABLE IF EXISTS `userfavorite`;
CREATE TABLE `userfavorite`  (
  `FavoriteId` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NULL DEFAULT NULL,
  `ScreenshotRecordId` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`FavoriteId`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of userfavorite
-- ----------------------------

-- ----------------------------
-- Table structure for userterritory
-- ----------------------------
DROP TABLE IF EXISTS `userterritory`;
CREATE TABLE `userterritory`  (
  `Id` int(11) NOT NULL AUTO_INCREMENT,
  `UserId` int(11) NULL DEFAULT NULL,
  `TerritoryId` int(11) NULL DEFAULT NULL,
  PRIMARY KEY (`Id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of userterritory
-- ----------------------------
INSERT INTO `userterritory` VALUES (10, 2, 9);
INSERT INTO `userterritory` VALUES (11, 2, 11);

SET FOREIGN_KEY_CHECKS = 1;
