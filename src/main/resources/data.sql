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
    shape       ENUM ('CIRCLE', 'SQUARE', 'TRIANGLE', 'HEXAGON') NOT NULL,
    proficiency ENUM ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') NOT NULL,
    shared      BOOLEAN,
    created_at  DATE,
    modified_at DATE,

    CONSTRAINT fk_runner FOREIGN KEY (runner_id) REFERENCES runner (id)
);

-- 이미지 조회가 가능한지 테스트하기 위해 미리 삽입해주는 이미지 메타데이터
INSERT INTO image_metadata VALUES (1, 1, 'sample_image.png', 'title1', 'description1', 'location1', 'CIRCLE', 'BEGINNER', false, '2021-01-01', '2021-01-01');