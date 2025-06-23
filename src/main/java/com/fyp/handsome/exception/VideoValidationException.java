package com.fyp.handsome.exception;

/**
 * 视频参数校验异常
 * 当视频信息参数校验失败时抛出
 * @author ziye
 */
public class VideoValidationException extends VideoBusinessException {

    private static final Integer ERROR_CODE = 400;

    /**
     * 构造函数
     * @param message 校验失败信息
     */
    public VideoValidationException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * 构造函数
     * @param message 校验失败信息
     * @param cause 原始异常
     */
    public VideoValidationException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
} 