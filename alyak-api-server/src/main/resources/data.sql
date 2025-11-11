-- Role 데이터
INSERT INTO role (role_id, role_name) VALUES
(1, 'ADMIN'),
(2, 'USER')
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- User 데이터
-- 주의: 비밀번호는 BCrypt로 암호화된 값입니다.
-- 모든 사용자의 비밀번호는 'password123'입니다.
-- 실제 운영 환경에서는 더 강력한 비밀번호를 사용해야 합니다.
-- BCrypt 해시는 Spring Security의 BCryptPasswordEncoder로 생성되었습니다.
INSERT INTO users (email, password, name) VALUES
('admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '관리자'),
('user1@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '김철수'),
('user2@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '이영희'),
('user3@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '박민수'),
('user4@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '정수진')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- UserRole 데이터 (복합키: user_id + role_id)
-- 관리자는 ADMIN 역할, 나머지는 USER 역할
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1),  -- admin@example.com -> ADMIN
(2, 2),  -- user1@example.com -> USER
(3, 2),  -- user2@example.com -> USER
(4, 2),  -- user3@example.com -> USER
(5, 2)   -- user4@example.com -> USER
ON DUPLICATE KEY UPDATE user_id = VALUES(user_id);

-- Provider 데이터 (복합키: provider_name + user_id)
-- 각 사용자의 로그인 제공자 정보
INSERT INTO provider (provider_name, user_id) VALUES
('LOCAL', 1),   -- admin@example.com - 로컬 가입
('LOCAL', 2),   -- user1@example.com - 로컬 가입
('LOCAL', 3),   -- user2@example.com - 로컬 가입
('KAKAO', 4),   -- user3@example.com - 카카오 로그인
('GOOGLE', 5)   -- user4@example.com - 구글 로그인
ON DUPLICATE KEY UPDATE provider_name = VALUES(provider_name);
