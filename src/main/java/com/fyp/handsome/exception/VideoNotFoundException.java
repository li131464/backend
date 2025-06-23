package com.fyp.handsome.exception;

/**
 * 视频不存在异常
 * 当查询的视频记录不存在时抛出
 * @author ziye
 */
public class VideoNotFoundException extends VideoBusinessException {

    private static final Integer ERROR_CODE = 404;

    /**
     * 构造函数
     * @param videoId 视频ID
     */
    public VideoNotFoundException(Long videoId) {
        super(ERROR_CODE, "视频信息不存在，ID: " + videoId);
    }

    /**
     * 构造函数
     * @param message 自定义错误信息
     */
    public VideoNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
} 