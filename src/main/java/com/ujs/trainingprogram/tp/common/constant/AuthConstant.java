package com.ujs.trainingprogram.tp.common.constant;

import lombok.RequiredArgsConstructor;

/**
 * 用户权限鉴权枚举
 */
@RequiredArgsConstructor
public class AuthConstant {

    /**
     * 教务处
     */
    public static final String ACADEMIC_AFFAIRS_STAFF = "100";

    /**
     * 系教务
     */
    public static final String DEPARTMENTAL_ACADEMIC_AFFAIRS = "50";

    /**
     * 系主任
     */
    public static final String DEPARTMENT_CHAIR = "10";


}
