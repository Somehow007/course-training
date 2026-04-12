package com.ujs.trainingprogram.tp.common.enums;

import lombok.Getter;

@Getter
public enum ChangeTypeEnum {
    ADD(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除");

    private final Integer code;
    private final String desc;

    ChangeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ChangeTypeEnum getByCode(Integer code) {
        for (ChangeTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
