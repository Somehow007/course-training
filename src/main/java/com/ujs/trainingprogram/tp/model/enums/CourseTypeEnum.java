package com.ujs.trainingprogram.tp.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 课程类别 0-通识教育 1-学科专业基础 2-专业课程 3-实验实践环节 4-进阶研学
 */
public enum CourseTypeEnum {
    COMMON_COMPULSORY(0, "通识教育"),
    MAJOR_BASE(1, "学科专业基础"),
    MAJOR_COURSE(2, "专业课程"),
    PRACTICE(3, "实验实践环节"),
    ADVANCED(4, "进阶研学"),
    ;

    @EnumValue
    private final int code;
    private final String s;

    CourseTypeEnum(int code, String s) {
        this.code = code;
        this.s = s;
    }

    public static int getCode(String name) {
        for (CourseTypeEnum value : values()) {
            if (value.s.equals(name)) {
                return value.code;
            }
        }
        return -1;
    }

    public static String getName(int code) {
        for (CourseTypeEnum value : values()) {
            if (value.code == code) {
                return value.s;
            }
        }
        return "";
    }

    public static CourseTypeEnum getInstance(String name) {
        for (CourseTypeEnum value : values()) {
            if (value.s.equals(name)) {
                return value;
            }
        }
        return null;
    }

    public static CourseTypeEnum getInstance(Integer code) {
        for (CourseTypeEnum value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return s;
    }
}
