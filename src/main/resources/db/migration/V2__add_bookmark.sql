-- 9. Bookmark
CREATE TABLE IF NOT EXISTS bookmark (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES post (post_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    UNIQUE (post_id, user_id)
    );