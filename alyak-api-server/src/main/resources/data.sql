-- Insert dummy users data
INSERT INTO users (name, email, password, phone_number, gender, resident_registration_number)
VALUES ('김철수', 'kim.chulsoo@email.com', 'password123', '010-1234-5678', 'M', '900101-1234567'),
       ('이영희', 'lee.younghee@email.com', 'password123', '010-2345-6789', 'F', '920305-2345678'),
       ('박민수', 'park.minsu@email.com', 'password123', '010-3456-7890', 'M', '880715-3456789'),
       ('최수진', 'choi.sujin@email.com', 'password123', '010-4567-8901', 'F', '950822-4567890'),
       ('정태호', 'jung.taeho@email.com', 'password123', '010-5678-9012', 'M', '870312-5678901'),
       ('한미영', 'han.miyeong@email.com', 'password123', '010-6789-0123', 'F', '930628-6789012'),
       ('송재현', 'song.jaehyun@email.com', 'password123', '010-7890-1234', 'M', '910419-7890123'),
       ('윤소영', 'yoon.soyoung@email.com', 'password123', '010-8901-2345', 'F', '940711-8901234'),
       ('임동현', 'lim.donghyun@email.com', 'password123', '010-9012-3456', 'M', '860925-9012345'),
       ('강지은', 'kang.jieun@email.com', 'password123', '010-0123-4567', 'F', '960314-0123456'),
       ('김민준', 'kim.minjun@email.com', 'password123', '010-1111-2222', 'M', '001215-1111111'),
       ('이서연', 'lee.seoyeon@email.com', 'password123', '010-2222-3333', 'F', '020708-2222222'),
       ('박준호', 'park.junho@email.com', 'password123', '010-3333-4444', 'M', '030321-3333333'),
       ('최예은', 'choi.yeeun@email.com', 'password123', '010-4444-5555', 'F', '041112-4444444'),
       ('정현우', 'jung.hyunwoo@email.com', 'password123', '010-5555-6666', 'M', '050603-5555555'),
       ('한소희', 'han.sohee@email.com', 'password123', '010-6666-7777', 'F', '060929-6666666'),
       ('송도현', 'song.dohyun@email.com', 'password123', '010-7777-8888', 'M', '070417-7777777'),
       ('윤지원', 'yoon.jiwon@email.com', 'password123', '010-8888-9999', 'F', '080825-8888888'),
       ('임승우', 'lim.seungwoo@email.com', 'password123', '010-9999-0000', 'M', '090512-9999999');

-- Insert dummy families data
INSERT INTO families (family_name, description)
VALUES ('김가족', '김철수 가족 - 4인 가족'),
       ('이가족', '이영희 가족 - 3인 가족'),
       ('박가족', '박민수 가족 - 5인 가족'),
       ('최가족', '최수진 가족 - 2인 가족'),
       ('정가족', '정태호 가족 - 4인 가족'),
       ('한가족', '한미영 가족 - 3인 가족'),
       ('송가족', '송재현 가족 - 6인 가족'),
       ('윤가족', '윤소영 가족 - 2인 가족'),
       ('임가족', '임동현 가족 - 4인 가족'),
       ('강가족', '강지은 가족 - 3인 가족');

-- Insert dummy family_members data (assumes fresh DB with auto-increment starting at 1)
-- 김가족 (Family ID: 1)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (1, 1, '부모'),  -- 김철수
       (1, 11, '자녀'), -- 김민준
       (1, 12, '자녀'), -- 이서연 (김가족의 자녀)
       (1, 13, '자녀'); -- 박준호 (김가족의 자녀)

-- 이가족 (Family ID: 2)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (2, 2, '부모'),  -- 이영희
       (2, 14, '자녀'), -- 최예은
       (2, 15, '자녀'); -- 정현우

-- 박가족 (Family ID: 3)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (3, 3, '부모'),  -- 박민수
       (3, 16, '자녀'), -- 한소희
       (3, 17, '자녀'), -- 송도현
       (3, 18, '자녀'), -- 윤지원
       (3, 19, '자녀'); -- 임승우

-- 최가족 (Family ID: 4)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (4, 4, '부모'), -- 최수진
       (4, 20, '자녀'); -- 강하은

-- 정가족 (Family ID: 5)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (5, 5, '부모'), -- 정태호
       (5, 6, '부모'), -- 한미영
       (5, 7, '자녀'), -- 송재현
       (5, 8, '자녀'); -- 윤소영

-- 한가족 (Family ID: 6)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (6, 9, '부모'),  -- 임동현
       (6, 10, '부모'), -- 강지은
       (6, 11, '자녀'); -- 김민준 (한가족의 자녀)

-- 송가족 (Family ID: 7)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (7, 12, '부모'), -- 이서연
       (7, 13, '부모'), -- 박준호
       (7, 14, '자녀'), -- 최예은
       (7, 15, '자녀'), -- 정현우
       (7, 16, '자녀'), -- 한소희
       (7, 17, '자녀'); -- 송도현

-- 윤가족 (Family ID: 8)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (8, 18, '부모'), -- 윤지원
       (8, 19, '자녀'); -- 임승우

-- 임가족 (Family ID: 9)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (9, 20, '부모'), -- 강하은
       (9, 1, '자녀'),  -- 김철수 (임가족의 자녀)
       (9, 2, '자녀'),  -- 이영희 (임가족의 자녀)
       (9, 3, '자녀'); -- 박민수 (임가족의 자녀)

-- 강가족 (Family ID: 10)
INSERT INTO family_members (family_id, user_id, relationship)
VALUES (10, 4, '부모'), -- 최수진
       (10, 5, '부모'), -- 정태호
       (10, 6, '자녀'); -- 한미영
