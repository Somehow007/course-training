package com.ujs.trainingprogram.tp.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 专业类型枚举类
 */
public enum MajorTypeEnum {
    ENGINEERING(0, "工学"),
    SCIENCE(1, "理学"),
    LIBERALARTS(2, "文科"),
    ;

    @EnumValue
    @Getter
    private final int type;
    @Getter
    private final String value;

    MajorTypeEnum(int type, String value) {
        this.type = type;
        this.value = value;
    }
}
