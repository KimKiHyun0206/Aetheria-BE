DROP TABLE IF EXISTS runner;
DROP TABLE IF EXISTS kakao_token;

CREATE TABLE runner
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    kakao_id BIGINT       NOT NULL UNIQUE,
    name     VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE kakao_token
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    runner_id     BIGINT       NOT NULL,
    access_token  VARCHAR(255) NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);