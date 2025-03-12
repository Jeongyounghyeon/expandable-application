-- 데이터베이스 생성
CREATE DATABASE expandable_appliation;

-- 데이터베이스 이동
USE expandable_appliation;

-- 사용자 테이블 생성
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	withdraw_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 인증 정보 테이블 생성
CREATE TABLE authentication_details (
    id VARCHAR(50) PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 사용자 생성 및 권한 부여
CREATE USER 'auth_user'@'%' IDENTIFIED BY '0000';
GRANT ALL PRIVILEGES ON expandable_appliation.authentication_details TO 'auth_user'@'%';
FLUSH PRIVILEGES;