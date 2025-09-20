DROP TABLE IF EXISTS runner;
DROP TABLE IF EXISTS kakao_token;
DROP TABLE IF EXISTS image;

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

CREATE TABLE image
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    runner_id   BIGINT,
    image_path  TEXT,
    description VARCHAR(255),
    location    VARCHAR(255),
    shape       ENUM ('CIRCLE', 'SQUARE', 'TRIANGLE', 'HEXAGON'),
    proficiency ENUM ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')
);