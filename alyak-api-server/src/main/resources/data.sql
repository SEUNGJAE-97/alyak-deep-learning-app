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

-- Family 데이터 및 사용자-가족 매핑
INSERT INTO family (family_id) VALUES
(1),
(2),
(3)
ON DUPLICATE KEY UPDATE family_id = VALUES(family_id);

UPDATE users SET family_id = 1 WHERE user_id IN (1, 2);
UPDATE users SET family_id = 2 WHERE user_id IN (3, 4);
UPDATE users SET family_id = 3 WHERE user_id = 5;

-- Schedule Status 데이터
INSERT INTO status (status_id, status_name) VALUES
(1, 'SCHEDULED'),
(2, 'TAKEN'),
(3, 'SKIPPED'),
(4, 'CANCELLED')
ON DUPLICATE KEY UPDATE status_name = VALUES(status_name);

-- PillShape 데이터
INSERT INTO pill_shape (shape_id, shape_name) VALUES
(1, '원형'),
(2, '타원형'),
(3, '장방형'),
(4, '반원형'),
(5, '삼각형'),
(6, '사각형'),
(7, '마름모형'),
(8, '오각형'),
(9, '육각형'),
(10, '팔각형'),
(11, '기타'),
(12, '8자형'),
(13, '강낭콩형'),
(14, '과일모양'),
(15, '구형'),
(16, '나비넥타이형'),
(17, '나비모양'),
(18, '다이아몬드형'),
(19, '도넛형')
ON DUPLICATE KEY UPDATE shape_name = VALUES(shape_name);

-- PillColor 데이터
INSERT INTO pill_color (color_id, color_name) VALUES
(1, '하양'),
(2, '노랑'),
(3, '주황'),
(4, '분홍'),
(5, '빨강'),
(6, '갈색'),
(7, '연두'),
(8, '초록'),
(9, '청록'),
(10, '파랑'),
(11, '남색'),
(12, '자주'),
(13, '보라'),
(14, '회색'),
(15, '검정'),
(16, '투명')
ON DUPLICATE KEY UPDATE color_name = VALUES(color_name);
