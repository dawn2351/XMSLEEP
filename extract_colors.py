#!/usr/bin/env python3
"""
从背景封面图中提取主题色
"""
from PIL import Image
import collections
import os

def get_dominant_color(image_path, num_colors=5):
    """提取图片的主导颜色"""
    img = Image.open(image_path)
    
    # 缩小图片以加快处理速度
    img = img.resize((150, 150))
    
    # 转换为 RGB
    img = img.convert('RGB')
    
    # 获取所有像素
    pixels = list(img.getdata())
    
    # 统计颜色出现次数
    color_counts = collections.Counter(pixels)
    
    # 获取最常见的颜色
    most_common = color_counts.most_common(num_colors)
    
    print(f"\n{os.path.basename(image_path)}:")
    print(f"  最常见的 {num_colors} 种颜色:")
    for i, (color, count) in enumerate(most_common, 1):
        r, g, b = color
        hex_color = f"#{r:02X}{g:02X}{b:02X}"
        percentage = (count / len(pixels)) * 100
        print(f"    {i}. {hex_color} (RGB: {r}, {g}, {b}) - {percentage:.2f}%")
    
    # 返回最常见的颜色
    return most_common[0][0]

# 处理所有背景封面图
drawable_path = "app/src/main/res/drawable"
png_files = [
    "bg_animation_1_thumb_png.png",
    "bg_animation_2_thumb_png.png",
    "bg_animation_3_thumb_png.png",
    "bg_animation_4_thumb_png.png",
    "bg_animation_5_thumb_png.png"
]

print("=" * 60)
print("提取背景封面图主题色")
print("=" * 60)

results = {}
for png_file in png_files:
    file_path = os.path.join(drawable_path, png_file)
    if os.path.exists(file_path):
        dominant_color = get_dominant_color(file_path)
        results[png_file] = dominant_color
    else:
        print(f"\n文件不存在: {file_path}")

print("\n" + "=" * 60)
print("Kotlin 代码格式:")
print("=" * 60)
for i, (png_file, color) in enumerate(results.items(), 1):
    r, g, b = color
    hex_color = f"0xFF{r:02X}{g:02X}{b:02X}"
    print(f"Background{i}: Color({hex_color})  // RGB: {r}, {g}, {b}")
