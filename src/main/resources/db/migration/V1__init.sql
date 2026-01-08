-- 1. Users
CREATE TABLE IF NOT EXISTS users (
    user_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    email             VARCHAR(255) NOT NULL UNIQUE,
    password          VARCHAR(255),
    nickname          VARCHAR(50) NOT NULL,
    profile_image_url VARCHAR(255),
    bio               VARCHAR(255),
    role              VARCHAR(20) DEFAULT 'USER',
    provider          VARCHAR(20),
    provider_id       VARCHAR(255),
    created_at        DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

-- 2. Follow
CREATE TABLE IF NOT EXISTS follow (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id   BIGINT NOT NULL,
    to_user_id     BIGINT NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user_id) REFERENCES users (user_id),
    FOREIGN KEY (to_user_id) REFERENCES users (user_id),
    UNIQUE (from_user_id, to_user_id)
    );

-- 3. Album
CREATE TABLE IF NOT EXISTS album (
    album_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    title      VARCHAR(50) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    );

-- 4. Post
CREATE TABLE IF NOT EXISTS post (
    post_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    album_id   BIGINT,
    content    TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (album_id) REFERENCES album (album_id)
    );

-- 5. Post Image)]
CREATE TABLE IF NOT EXISTS post_image (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    sort_order INT NOT NULL,
    FOREIGN KEY (post_id) REFERENCES post (post_id)
    );

-- 6. Comment
CREATE TABLE IF NOT EXISTS comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    content    VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post (post_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    );

-- 7. Post Like
CREATE TABLE IF NOT EXISTS post_like (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post (post_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    UNIQUE (post_id, user_id)
    );

-- 8. Story
CREATE TABLE IF NOT EXISTS story (
    story_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
    );

