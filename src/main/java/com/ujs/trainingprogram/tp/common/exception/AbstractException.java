package com.ujs.trainingprogram.tp.common.exception;

import lombok.Getter;

/**
 * 抽象项目中的三类异常体系
 */
@Getter
public class AbstractException extends RuntimeException {
    public AbstractException(String message) {
        super(message);
    }
}
