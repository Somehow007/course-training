package com.ujs.trainingprogram.tp.common.web;

import com.ujs.trainingprogram.tp.common.errorcode.BaseErrorCode;
import com.ujs.trainingprogram.tp.common.result.Result;

/**
 * 构建全局返回对象构造器
 */
public final class Results {

    /**
     * 构建成功响应
     */
    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(Result.SUCCESS_CODE);
    }

    /**
     * 构建带返回数据的成功响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setData(data);
    }

    /**
     * 构建服务器失败响应
     */
    protected static Result<Void> failure() {
        return new Result<Void>()
                .setCode(BaseErrorCode.SERVICE_ERROR.code())
                .setMessage(BaseErrorCode.SERVICE_ERROR.message());
    }



}
