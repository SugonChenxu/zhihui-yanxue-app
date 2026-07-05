#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
生成App图标：红色背景 + 金色文字"e启航"
对标《学习强国》风格
"""

from PIL import Image, ImageDraw, ImageFont, ImageFilter
import math
import os

def create_app_icon(output_path="ic_launcher.png", size=512):
    """生成App图标"""
    
    # 创建图像（正方形）
    img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    
    # 红色背景 #C41E3A
    bg_color = (196, 30, 58)  # #C41E3A
    
    # 绘制圆角矩形背景
    radius = int(size * 0.15)  # 圆角半径（15%）
    draw.rounded_rectangle(
        [(0, 0), (size-1, size-1)],
        radius=radius,
        fill=bg_color
    )
    
    # 加载字体
    try:
        # 尝试加载楷体（书法风格）
        font_path = "C:/Windows/Fonts/simkai.ttf"
        if not os.path.exists(font_path):
            font_path = "C:/Windows/Fonts/simhei.ttf"  # 备选黑体
        
        # 文字大小（占图标的40%）
        font_size = int(size * 0.4)
        font = ImageFont.truetype(font_path, font_size)
        
        # 字母e的字体（稍微不同的字体）
        font_e_path = "C:/Windows/Fonts/simhei.ttf"
        font_e = ImageFont.truetype(font_e_path, font_size)
        
    except Exception as e:
        print(f"加载字体失败: {e}")
        # 使用默认字体
        font = ImageFont.load_default()
        font_e = font
    
    # 文字内容
    text_e = "e"
    text_cn = "启航"
    
    # 计算文字位置（居中）
    # 先测量文字宽度
    bbox_e = draw.textbbox((0, 0), text_e, font=font_e)
    bbox_cn = draw.textbbox((0, 0), text_cn, font=font)
    
    width_e = bbox_e[2] - bbox_e[0]
    width_cn = bbox_cn[2] - bbox_cn[0]
    height_e = bbox_e[3] - bbox_e[1]
    height_cn = bbox_cn[3] - bbox_cn[1]
    
    # 总宽度（带间距）
    spacing = int(size * 0.02)
    total_width = width_e + width_cn + spacing
    
    # 起始位置（居中）
    start_x = (size - total_width) // 2
    center_y = (size - max(height_e, height_cn)) // 2
    
    # 金色渐变颜色
    gold_light = (255, 215, 0)  # 亮金色
    gold_dark = (218, 165, 32)   # 暗金色
    
    # 绘制字母"e"（倾斜5度）
    # 创建临时图像绘制"e"
    img_e = Image.new('RGBA', (width_e + 20, height_e + 20), (0, 0, 0, 0))
    draw_e = ImageDraw.Draw(img_e)
    draw_e.text((10, 10), text_e, font=font_e, fill=gold_light)
    
    # 倾斜5度（向右）
    angle = 5
    img_e = img_e.rotate(-angle, resample=Image.BICUBIC, center=(width_e//2, height_e//2))
    
    # 粘贴到主图像
    e_x = start_x
    e_y = center_y - 10
    img.paste(img_e, (e_x, e_y), img_e)
    
    # 绘制"启航"（书法字体）
    text_x = start_x + width_e + spacing
    text_y = center_y
    
    # 绘制文字阴影（轻微浮雕效果）
    shadow_offset = 2
    draw.text((text_x + shadow_offset, text_y + shadow_offset), text_cn, font=font, fill=(180, 130, 20))
    
    # 绘制主文字（金色）
    draw.text((text_x, text_y), text_cn, font=font, fill=gold_light)
    
    # 添加光泽效果（高光）
    # 在文字左上角添加高光
    highlight_color = (255, 235, 100, 128)
    
    # 保存图标
    img.save(output_path, 'PNG')
    print(f"图标已保存: {output_path}")
    
    return img

def create_adaptive_icon(output_dir="app_icon"):
    """生成各种密度的图标"""
    
    # 创建输出目录
    os.makedirs(output_dir, exist_ok=True)
    
    # 各种密度对应的尺寸
    densities = {
        'mdpi': 48,
        'hdpi': 72,
        'xhdpi': 96,
        'xxhdpi': 144,
        'xxxhdpi': 192,
    }
    
    # 生成512x512的主图标
    icon_512 = create_app_icon(os.path.join(output_dir, 'ic_launcher_512.png'), 512)
    
    # 生成各种密度的图标
    for density, size in densities.items():
        output_path = os.path.join(output_dir, f'ic_launcher_{density}.png')
        create_app_icon(output_path, size)
    
    # 生成圆形图标（适配Android Oreo+）
    create_round_icon(os.path.join(output_dir, 'ic_launcher_round_512.png'), 512)
    
    print(f"\n所有图标已生成到目录: {output_dir}")

def create_round_icon(output_path, size=512):
    """生成圆形图标（适配Android Oreo+）"""
    
    # 创建圆形蒙版
    mask = Image.new('L', (size, size), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.ellipse((0, 0, size-1, size-1), fill=255)
    
    # 生成方形图标
    img = create_app_icon_temp(size)
    
    # 应用圆形蒙版
    img.putalpha(mask)
    
    # 保存
    img.save(output_path, 'PNG')
    print(f"圆形图标已保存: {output_path}")

def create_app_icon_temp(size=512):
    """生成临时图标图像"""
    
    img = Image.new('RGBA', (size, size), (196, 30, 58, 255))
    
    # 添加文字
    draw = ImageDraw.Draw(img)
    
    try:
        font_path = "C:/Windows/Fonts/simkai.ttf"
        font = ImageFont.truetype(font_path, int(size * 0.4))
    except:
        font = ImageFont.load_default()
    
    text = "e启航"
    bbox = draw.textbbox((0, 0), text, font=font)
    text_width = bbox[2] - bbox[0]
    text_height = bbox[3] - bbox[1]
    
    x = (size - text_width) // 2
    y = (size - text_height) // 2
    
    draw.text((x, y), text, font=font, fill=(255, 215, 0, 255))
    
    return img

if __name__ == '__main__':
    print("开始生成App图标...")
    create_adaptive_icon('app_icon')
    print("完成！")
