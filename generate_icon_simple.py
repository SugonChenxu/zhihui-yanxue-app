#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成App图标：红色背景 + 金色文字"e启航"
简化版本
"""

from PIL import Image, ImageDraw, ImageFont
import os

def generate_icon(output_path, size=512):
    """生成单个尺寸的图标"""
    
    # 创建图像（RGBA模式支持透明度）
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # 中国红背景 #C41E3A
    bg_color = (196, 30, 58)
    
    # 绘制圆角矩形背景（圆角约15%）
    corner_radius = int(size * 0.15)
    draw.rounded_rectangle(
        [(0, 0), (size, size)],
        radius=corner_radius,
        fill=bg_color
    )
    
    # 加载字体
    font_size = int(size * 0.35)  # 文字占图标35%
    
    # 尝试加载系统字体
    font_paths = [
        "C:/Windows/Fonts/simkai.ttf",  # 楷体（书法风格）
        "C:/Windows/Fonts/simhei.ttf",  # 黑体（备选）
        "C:/Windows/Fonts/msyh.ttc",    # 微软雅黑（备选）
    ]
    
    font = None
    for fp in font_paths:
        if os.path.exists(fp):
            try:
                font = ImageFont.truetype(fp, font_size)
                print(f"使用字体: {fp}")
                break
            except:
                continue
    
    if font is None:
        font = ImageFont.load_default()
        print("使用默认字体")
    
    # 文字内容
    text = "e启航"
    
    # 计算文字边界框
    bbox = draw.textbbox((0, 0), text, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    
    # 居中位置
    x = (size - text_width) // 2
    y = (size - text_height) // 2
    
    # 鎏金色彩（金色渐变效果用纯金色代替）
    gold_color = (255, 215, 0)  # 金色
    
    # 绘制文字
    draw.text((x, y), text, font=font, fill=gold_color)
    
    # 保存
    img.save(output_path, 'PNG')
    print(f"生成图标: {output_path} ({size}x{size})")

def generate_all_icons(output_dir="app_icons"):
    """生成所有密度的图标"""
    
    # 创建输出目录
    os.makedirs(output_dir, exist_ok=True)
    
    # Android图标尺寸
    icons = {
        'mdpi': 48,
        'hdpi': 72,
        'xhdpi': 96,
        'xxhdpi': 144,
        'xxxhdpi': 192,
        'web': 512,  # Google Play要求的512x512
    }
    
    for name, size in icons.items():
        output_path = os.path.join(output_dir, f'ic_launcher_{name}.png')
        generate_icon(output_path, size)
    
    print(f"\n✅ 所有图标已生成到: {output_dir}")

if __name__ == '__main__':
    print("🎨 开始生成App图标...")
    print("风格: 中国红背景 + 金色文字'e启航'")
    print("参考: 《学习强国》官方图标风格\n")
    
    generate_all_icons('app_icons')
    
    print("\n📝 下一步:")
    print("1. 将生成的图标复制到Android项目的res/mipmap目录")
    print("2. 修改AndroidManifest.xml中的icon和roundIcon属性")
    print("3. 修改strings.xml中的app_name为'智慧研学'")
