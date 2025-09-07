package com.ujs.trainingprogram.tp.model;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * 验证码
 */
@Data
public class ImageCode {
    private String code;
    // 设置过期时长
    private LocalDateTime expiredTime;
    // 图形验证码
    private BufferedImage image;

    public ImageCode(String code, int expiredIn, BufferedImage image) {
        this.code = code;
        this.expiredTime = LocalDateTime.now().plusSeconds(expiredIn);
        this.image = image;
    }

    // 判断验证码是否过期
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredTime);
    }
}
