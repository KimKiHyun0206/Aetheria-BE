DROP TABLE IF EXISTS runner;

CREATE TABLE runner
(
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    kakao_id BIGINT       NOT NULL,
    name     VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);