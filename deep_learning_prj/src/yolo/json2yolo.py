import json
import os
from glob import glob
from tqdm import tqdm

Label_Dir = "./datasets/labels/train/"
Output_Dir = "./datasets/labels/train/"

### Convert COCO bbox to YOLO format
def convert_box(size, box):
    dw = 1. / size[0]
    dh = 1. / size[1]
    x, y, w, h = box[0], box[1], box[2], box[3]
    x_center = (x + w / 2.0) * dw
    y_center = (y + h / 2.0) * dh
    w *= dw
    h *= dh
    return (x_center, y_center, w, h)

def process_recursive(root_dir, output_txt_dir):

    if not os.path.exists(output_txt_dir):
        os.makedirs(output_txt_dir)
    
    print(f" 검색 시작: {root_dir} (하위 폴더 포함)")
    json_files = glob(os.path.join(root_dir, '**', '*.json'), recursive=True)
    
    print(f"   ㄴ 총 {len(json_files)}개의 JSON 파일을 발견했습니다. 변환을 시작합니다.")

    for json_file in tqdm(json_files):
        try:
            with open(json_file, 'r', encoding='utf-8') as f:
                data = json.load(f)
                
            img_w = data['images'][0]['width']
            img_h = data['images'][0]['height']
            bbox = data['annotations'][0]['bbox']
            
            yolo_bbox = convert_box((img_w, img_h), bbox)
            
            file_name = os.path.basename(json_file).replace('.json', '.txt')
            output_path = os.path.join(output_txt_dir, file_name)
            
            with open(output_path, 'w') as out_f:
                out_f.write(f"0 {yolo_bbox[0]:.6f} {yolo_bbox[1]:.6f} {yolo_bbox[2]:.6f} {yolo_bbox[3]:.6f}\n")
                
        except Exception as e:
            pass

def main():
    
    SRC_TRAIN_ROOT = './datasets/training/labels/train/' 
    SRC_VAL_ROOT   = './datasets/training/labels/val/'
    DST_TRAIN_LABEL = './datasets/labels/train/'
    DST_VAL_LABEL   = './datasets/labels/val/'

    print(" [Training] 데이터 전체 변환...")
    process_recursive(SRC_TRAIN_ROOT, DST_TRAIN_LABEL)
    
    print("\n [Validation] 데이터 전체 변환...")
    process_recursive(SRC_VAL_ROOT, DST_VAL_LABEL)
    
    print("\n 모든 작업 완료!")

if __name__ == '__main__':
    main()