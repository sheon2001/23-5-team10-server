-- nickname UNIQUE 제약 추가
ALTER TABLE users
ADD CONSTRAINT uk_users_nickname UNIQUE (nickname);

-- name 컬럼 추가
ALTER TABLE users
ADD COLUMN name VARCHAR(50);