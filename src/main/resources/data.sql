DROP TABLE IF EXISTS runner;
DROP TABLE IF EXISTS kakao_token;
DROP TABLE IF EXISTS image;

CREATE TABLE runner
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    kakao_id BIGINT       NOT NULL UNIQUE,
    name     VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE kakao_token
(
    id            BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    runner_id     BIGINT             NOT NULL,
    access_token  VARCHAR(255)       NOT NULL,
    refresh_token VARCHAR(255)       NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE image_metadata
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    runner_id   BIGINT       NOT NULL,
    image_path  VARCHAR(255) NOT NULL,
    title       VARCHAR(255),
    description VARCHAR(255),
    location    VARCHAR(255),
    shape       VARCHAR(255),
    proficiency VARCHAR(255),
    shared      BOOLEAN,
    created_at  DATE,
    modified_at DATE,

    CONSTRAINT fk_runner FOREIGN KEY (runner_id) REFERENCES runner (id)
);