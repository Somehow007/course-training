package com.ujs.trainingprogram.tp.common.constant;

/**
 * Redis Key 常量类
 */
public class RedisKeyConstant {

    /**
     * 学院名称更新锁 前缀 Key
     */
    public static final String LOCK_COLLEGE_NAME_KEY = "course-training:lock:college-id:%s";

    /**
     * 专业分类类型 前缀 Key
     */
    public static final String MAJOR_CATEGORY_KEY = "major-category_map";
}
