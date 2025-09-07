package com.ujs.trainingprogram.tp.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum MajorTypeEnum {
    ENGINEERING(0, "工学"),
    SCIENCE(1, "理学"),
    LIBERALARTS(2, "文科"),
    ;

    @EnumValue
    private final int code;
    private final String s;

    MajorTypeEnum(int code, String s) {
        this.code = code;
        this.s = s;
    }
}
