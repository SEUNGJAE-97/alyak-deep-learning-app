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

-- Pill 데이터
-- INSERT INTO pill (pill_id, pill_name, pill_description, user_method, pill_efficacy, pill_warn, pill_caution, pill_interactive, pill_adverse_reaction, pill_manufacturer, pill_img, pill_ingredient) VALUES
-- (1, '타이레놀', '진통 및 해열제', '성인: 1회 1~2정, 1일 3~4회 복용', '두통, 치통, 생리통, 근육통, 관절통, 신경통, 요통, 감기로 인한 발열 및 동통의 완화', '다음 환자에게는 복용하지 말 것: 간장애 환자, 알레르기 체질 환자', '복용 전 의사와 상의할 것: 임신부, 수유부, 고령자', '알코올과 함께 복용 시 간 손상 위험', '드물게 발진, 가려움, 두드러기 등이 나타날 수 있음', '한국얀센제약', NULL, '아세트아미노펜'),
-- (2, '게보린', '두통 및 신경통 완화제', '성인: 1회 1정, 1일 3회 식후 복용', '두통, 치통, 생리통, 근육통, 관절통, 신경통, 요통의 완화', '다음 환자에게는 복용하지 말 것: 위궤양 환자, 심장질환 환자', '복용 전 의사와 상의할 것: 고혈압 환자, 신장질환 환자', '항응고제와 함께 복용 시 출혈 위험 증가', '드물게 위장장애, 어지러움 등이 나타날 수 있음', '동화약품', NULL, '아세틸살리실산, 카페인'),
-- (3, '판콜에이내복액', '감기 증상 완화제', '성인: 1회 1포, 1일 3회 식후 복용', '감기로 인한 발열, 오한, 두통, 콧물, 코막힘, 재채기, 인후통, 기침의 완화', '다음 환자에게는 복용하지 말 것: 간장애 환자, 알레르기 체질 환자', '복용 전 의사와 상의할 것: 임신부, 수유부, 고령자', '수면제, 진정제와 함께 복용 시 졸음 증가', '드물게 발진, 가려움, 어지러움 등이 나타날 수 있음', '동화약품', NULL, '아세트아미노펜, 클로르페니라민말레산염'),
-- (4, '베아제', '소화 효소제', '성인: 1회 1~2정, 1일 3회 식후 복용', '소화불량, 식욕부진, 과식, 체함, 소화촉진', '다음 환자에게는 복용하지 말 것: 알레르기 체질 환자', '복용 전 의사와 상의할 것: 임신부, 수유부', '특별한 상호작용 없음', '드물게 발진, 가려움 등이 나타날 수 있음', '한독약품', NULL, '판크레아틴, 리파제, 아밀라제'),
-- (5, '우루사', '간 기능 개선제', '성인: 1회 1캡슐, 1일 3회 식후 복용', '만성 간염, 간경변증의 보조치료', '다음 환자에게는 복용하지 말 것: 알레르기 체질 환자', '복용 전 의사와 상의할 것: 임신부, 수유부', '특별한 상호작용 없음', '드물게 소화불량, 설사 등이 나타날 수 있음', '대웅제약', NULL, '우르소데옥시콜산')
-- ON DUPLICATE KEY UPDATE pill_name = VALUES(pill_name);
--
-- -- Schedule 데이터 (medication_schedules)
-- -- 오늘부터 7일간의 복약 스케줄 데이터
-- -- 주의: TAKEN 상태(status_id = 2)인 경우 schedule_taken_time 값이 설정됩니다.
-- -- 정상 복용: schedule_time과 동일하거나 약간 늦게
-- -- 지연 복용: schedule_time보다 늦게 (지연 통계에 포함됨)
-- INSERT INTO medication_schedules (user_id, pill_id, status_id, schedule_time, schedule_start_time, schedule_end_time, schedule_dosage, schedule_taken_time) VALUES
-- -- 관리자 (admin@example.com)의 스케줄
-- (1, 5, 1, '2024-12-20 07:30:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 우루사 아침
-- (1, 5, 2, '2024-12-20 19:30:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, '2024-12-20 19:45:00'),  -- 우루사 저녁 (복용 완료, 15분 지연)
-- (1, 2, 1, '2024-12-20 10:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 게보린 오전
-- (1, 2, 1, '2024-12-20 16:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 게보린 오후
-- (1, 4, 1, '2024-12-20 12:30:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 베아제 점심
--
-- -- user1 (김철수)의 스케줄
-- (2, 1, 1, '2024-12-20 08:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 타이레놀 아침
-- (2, 1, 1, '2024-12-20 14:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 타이레놀 점심
-- (2, 1, 2, '2024-12-20 20:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, '2024-12-20 20:00:00'),  -- 타이레놀 저녁 (복용 완료, 정시 복용)
-- (2, 4, 1, '2024-12-20 12:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 2, NULL),  -- 베아제 점심
-- (2, 4, 1, '2024-12-20 18:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 2, NULL),  -- 베아제 저녁
--
-- -- user2 (이영희)의 스케줄
-- (3, 2, 1, '2024-12-20 09:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 게보린 아침
-- (3, 2, 3, '2024-12-20 15:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 게보린 점심 (건너뜀)
-- (3, 2, 1, '2024-12-20 21:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 게보린 저녁
-- (3, 5, 1, '2024-12-20 08:30:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 우루사 아침
-- (3, 5, 1, '2024-12-20 20:30:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 우루사 저녁
--
-- -- user3 (박민수)의 스케줄
-- (4, 3, 1, '2024-12-20 07:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 판콜에이내복액 아침
-- (4, 3, 2, '2024-12-20 13:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, '2024-12-20 13:30:00'),  -- 판콜에이내복액 점심 (복용 완료, 30분 지연)
-- (4, 3, 1, '2024-12-20 19:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL),  -- 판콜에이내복액 저녁
--
-- -- user4 (정수진)의 스케줄
-- (5, 1, 1, '2024-12-20 08:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 2, NULL),  -- 타이레놀 아침
-- (5, 1, 1, '2024-12-20 20:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 2, NULL),  -- 타이레놀 저녁
-- (5, 4, 1, '2024-12-20 12:00:00', '2024-12-20 00:00:00', '2024-12-27 23:59:59', 1, NULL)   -- 베아제 점심
-- ON DUPLICATE KEY UPDATE schedule_time = VALUES(schedule_time);