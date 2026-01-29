--  Search
CREATE TABLE IF NOT EXISTS search_history (
    search_id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_user_id   BIGINT NOT NULL,
    to_user_id     BIGINT NOT NULL,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted_at     DATETIME NULL,
    FOREIGN KEY (from_user_id) REFERENCES users (user_id),
    FOREIGN KEY (to_user_id) REFERENCES users (user_id),
    INDEX idx_search_from_created (from_user_id, created_at),
    INDEX idx_search_from_to_created (from_user_id, to_user_id, created_at)
    );