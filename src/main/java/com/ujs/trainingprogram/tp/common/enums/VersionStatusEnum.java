package com.ujs.trainingprogram.tp.common.enums;

import lombok.Getter;

@Getter
public enum VersionStatusEnum {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    ARCHIVED(2, "已归档"),
    ROLLED_BACK(3, "已回滚");

    private final Integer code;
    private final String desc;

    VersionStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static VersionStatusEnum getByCode(Integer code) {
        for (VersionStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
