-- 데이터베이스 생성
CREATE DATABASE expandable_application;

-- 데이터베이스 이동
USE expandable_application;

-- 사용자 테이블 생성
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
	withdraw_at DATETIME DEFAULT NULL
);

-- 인증 정보 테이블 생성
CREATE TABLE authentication_details (
    id VARCHAR(50) PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 인증 정보 테이블 생성
CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_value VARCHAR(255) NOT NULL,
    expired_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

-- 게시판 테이블 생성
CREATE TABLE board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    writer_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME,
    last_modified_at DATETIME,
    FOREIGN KEY (writer_id) REFERENCES user(id)
);

-- 게시판 댓글 테이블 생성
CREATE TABLE board_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    writer_id BIGINT NOT NULL,
    board_id BIGINT NOT NULL,
    root_comment_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME,
    last_modified_at DATETIME,
    FOREIGN KEY (writer_id) REFERENCES user(id)
);

-- 사용자 생성 및 권한 부여
CREATE USER 'auth_user'@'%' IDENTIFIED BY '0000';
CREATE USER 'board_user'@'%' IDENTIFIED BY '0000';

GRANT SELECT, INSERT, UPDATE, DELETE ON expandable_application.authentication_details TO 'auth_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON expandable_application.refresh_token TO 'auth_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON expandable_application.board TO 'board_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE ON expandable_application.board_comment TO 'board_user'@'%';

FLUSH PRIVILEGES;
