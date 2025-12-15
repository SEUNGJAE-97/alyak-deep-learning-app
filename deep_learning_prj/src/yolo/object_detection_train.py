from ultralytics import YOLO

def main():
    # 1. 모델 로드 (처음엔 yolo11m.pt 다운로드됨)
    model = YOLO('yolo11s.pt')

    # 2. 학습 시작
    results = model.train(
        data='data.yaml',     # 데이터셋 설정 파일
        epochs=50,           # 학습 횟수
        imgsz=640,           # 이미지 크기 (작은 알약 탐지에 유리)
        batch=16,              # 메모리 터지면 4로 줄이세요
        patience=20,          # 20번 동안 성능 향상 없으면 조기 종료
        device='mps',         # [중요] Mac 사용자용 GPU 가속 옵션 (Windows는 'cuda', 없으면 'cpu')
        project='pill_project', # 결과 저장할 폴더 이름 (runs/detect 대신 이거 씀)
        name='yolo11m_try1',    # 이번 실험의 이름 (결과가 pill_project/yolo11m_try1 에 저장됨)
        exist_ok=True,        # 덮어쓰기 허용 (False면 try2, try3... 계속 생성)
        plots=True,            # 학습 그래프 자동 생성
        workers=4
    )

if __name__ == '__main__':
    main()