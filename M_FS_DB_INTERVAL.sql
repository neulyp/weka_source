/*
Navicat Oracle Data Transfer
Oracle Client Version : 12.1.0.2.0

Source Server         : localhost_1521_ORCL
Source Server Version : 120100
Source Host           : localhost:1521
Source Schema         : C##TEST

Target Server Type    : ORACLE
Target Server Version : 120100
File Encoding         : 65001

Date: 2016-10-24 13:00:58
*/


-- ----------------------------
-- Table structure for M_FS_DB_INTERVAL
-- ----------------------------
DROP TABLE "C##TEST"."M_FS_DB_INTERVAL";
CREATE TABLE "C##TEST"."M_FS_DB_INTERVAL" (
"CST_ID" NUMBER(9) NOT NULL ,
"CST_NAME" VARCHAR2(50 BYTE) NULL ,
"MDI_READING" NUMBER(15,4) NULL ,
"MDI_TS" NUMBER(18) NULL 
)
LOGGING
NOCOMPRESS
NOCACHE

;

-- ----------------------------
-- Indexes structure for table M_FS_DB_INTERVAL
-- ----------------------------
CREATE BITMAP INDEX "C##TEST"."id"
ON "C##TEST"."M_FS_DB_INTERVAL" ("CST_ID" ASC)
LOGGING
VISIBLE;
CREATE BITMAP INDEX "C##TEST"."name"
ON "C##TEST"."M_FS_DB_INTERVAL" ("CST_NAME" ASC)
LOGGING
VISIBLE;
CREATE BITMAP INDEX "C##TEST"."time"
ON "C##TEST"."M_FS_DB_INTERVAL" ("MDI_TS" ASC)
LOGGING
VISIBLE;

-- ----------------------------
-- Checks structure for table M_FS_DB_INTERVAL
-- ----------------------------
ALTER TABLE "C##TEST"."M_FS_DB_INTERVAL" ADD CHECK ("CST_ID" IS NOT NULL);
