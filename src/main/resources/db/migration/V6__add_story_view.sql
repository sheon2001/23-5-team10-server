-- 8. story view
CREATE TABLE IF NOT EXISTS story_view (
    view_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    story_id   BIGINT NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (story_id) REFERENCES story (story_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id),

    UNIQUE (story_id, user_id)
    );
