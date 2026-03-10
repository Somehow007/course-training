package com.ujs.trainingprogram.tp.common.enums;

import lombok.Getter;

import java.util.Objects;

/**
 * 课程性质枚举类
 */
public enum CourseNatureEnum {

    OPTIONAL(0, "选修"),
    REQUIRED(1, "必修");

    @Getter
    private final int type;

    @Getter
    private final String value;

    CourseNatureEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * 根据 type 找 value
     *
     * @param type  要查找的类型代码
     * @return  对应的描述值，如果没有找到就抛异常
     */
    public static String findValueByType(int type) {
        for (CourseNatureEnum target : values()) {
            if (target.getType() == type) {
                return target.value;
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * 根据 value 找 type
     *
     * @param value  要查找的描述值
     * @return  对应的type，如果没有找到就抛异常
     */
    public static int findTypeByValue(String value) {
        for (CourseNatureEnum target : values()) {
            if (Objects.equals(target.getValue(), value)) {
                return target.getType();
            }
        }
        throw new IllegalArgumentException();
    }
}
