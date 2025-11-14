package com.ujs.trainingprogram.tp.common.enums;

/**
 * 课程性质枚举类
 */
public enum CourseTypeEnum {

    OPTIONAL(0, "选修"),
    REQUIRED(1, "必修");

    private final int code;

    private final String dictName;

    CourseTypeEnum(int code, String dictName) {
        this.code = code;
        this.dictName = dictName;
    }

    public static String getDictName(int code) {
        for (CourseTypeEnum type : values()) {
            if (type.code == code) {
                return type.dictName;
            }
        }
        return "未知";
    }
}
