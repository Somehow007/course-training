package com.ujs.trainingprogram.tp.common.constant;

/**
 * Redis Key 常量类
 */
public class RedisKeyConstant {

    /**
     * 学院 ID:名称 缓存 Key
     */
    public static final String COLLEGE_ID_NAME_CACHE_KEY = "college:name:all";

    /**
     * 课程 名称:ID 缓存 Key
     */
    public static final String COURSE_NAME_ID_KEY = "course:name:id:all";

    /**
     * 数据字典 TYPE:名称 缓存 Key
     */
    public static final String SYS_DICT_TYPE_NAME_KEY = "sys-dict:type:name:all";

    /**
     * 获取学院 ID 缓存锁
     */
    public static final String LOCK_COLLEGE_ID_KEY = "lock:college:id";

    /**
     * 获取数据字典 NAME 缓存锁
     */
    public static final String LOCK_SYS_DICT_NAME_KEY = "lock:sys-dict:name";

    /**
     * 获取课程 ID 缓存锁
     */
    public static final String LOCK_COURSE_ID_KEY = "lock:course:id";

}
