-- Role 데이터
INSERT INTO role (role_id, role_name)
VALUES (1, 'ADMIN'),
       (2, 'USER');

-- User 데이터 (role_id FK 사용)
INSERT INTO users (email, password, name, role_id)
VALUES ('admin@example.com', 'password123', '관리자', 1),
       ('kim@example.com', 'password123', '김철수', 2),
       ('lee@example.com', 'password123', '이영희', 2),
       ('park@example.com', 'password123', '박민수', 2);

-- Provider 데이터 (복합키: provider_name + user_id)
INSERT INTO provider (provider_name, user_id)
VALUES ('LOCAL', 1),
       ('LOCAL', 2),
       ('KAKAO', 3);

-- Families 데이터 (auto increment 사용)
INSERT INTO families (family_id) VALUES (1), (2);

-- Family members (복합키 family_id + user_id)
INSERT INTO family_members (family_id, user_id)
VALUES (1, 2),
       (1, 3),
       (2, 4);

-- Pill Shape 데이터
INSERT INTO pill_shape (shape_id, shape_name)
VALUES (1, '원형'),
       (2, '타원형');

-- Pill Color 데이터
INSERT INTO pill_color (color_id, color_name)
VALUES (1, '백색'),
       (2, '청색');

-- Pill 데이터
INSERT INTO pills (pill_name, pill_description, use_method, efcy, warn, atpn, intrc, se, manufacturer, pill_img)
VALUES ('타이레놀 500mg', '해열진통제', '경구복용', '발열, 통증 완화', '과다 복용 주의', '간 질환 환자 복용 주의', '와파린과 상호작용 가능', '드물게 발진', '존슨앤존슨', NULL);

-- Pill Attribute 데이터
INSERT INTO fill_attribute (pill_id, shape_id, color_id, detail, front, back, classification, pill_type)
VALUES (1, 1, 1, '원형 백색 정제', 'TYLENOL', NULL, '일반의약품', false);

-- Status 데이터
INSERT INTO status (status_id, statue_name)
VALUES (1, '예정'),
       (2, '복용완료'),
       (3, '미복용');

-- Medication Schedule 데이터
INSERT INTO medication_schedules (user_id, pill_id, status_id, schedule_time, schedule_start_time, schedule_end_time, schedule_dosage)
VALUES (2, 1, 1, '2025-01-01 08:00:00', '2025-01-01 08:00:00', '2025-01-07 08:00:00', 1);
