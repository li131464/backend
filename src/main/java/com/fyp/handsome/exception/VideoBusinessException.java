package com.fyp.handsome.exception;

import lombok.Getter;

/**
 * 视频业务异常基类
 * 用于视频相关的业务异常处理
 * @author ziye
 */
@Getter
public class VideoBusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 构造函数
     * @param code 错误码
     * @param message 错误信息
     */
    public VideoBusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 构造函数
     * @param code 错误码
     * @param message 错误信息
     * @param cause 原始异常
     */
    public VideoBusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
} 