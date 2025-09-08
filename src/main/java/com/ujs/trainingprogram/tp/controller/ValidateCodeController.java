package com.ujs.trainingprogram.tp.controller;

import com.ujs.trainingprogram.tp.dao.entity.model.ImageCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@RestController
@Slf4j
public class ValidateCodeController {

    //定义存入session的key
    public static final String SESSION_KEY = "SESSION_IMAGE_CODE";

    @GetMapping("/code/image")
    public void createdCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store");
        response.setContentType("image/jpeg");
        try {
            ImageCode imageCode = createImageCode();
            request.getSession().setAttribute(SESSION_KEY, imageCode);
            ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
            log.info("成功生成验证码");
        } catch (Exception e) {
            log.error("生成验证码失败", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private ImageCode createImageCode() {
        int width = 67;
        int height = 23;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Random random = new Random();
        g.setColor(getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        g.setColor(getRandColor(160, 200));
        for (int i = 0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        String sRand = "";
        for(int i =0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 16);

        }
        g.dispose();
        log.info("生成的验证码为：{}", sRand);
        return new ImageCode(sRand, 60, image);
    }

    /**
     * 生成随机背景条纹
     */
    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt((bc - fc));
        int g = fc + random.nextInt((bc - fc));
        int b = fc + random.nextInt((bc - fc));
        return new Color(r, g, b);
    }
}
