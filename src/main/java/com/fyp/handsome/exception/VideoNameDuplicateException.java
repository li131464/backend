package com.fyp.handsome.exception;

/**
 * 视频名称重复异常
 * 当视频名称已存在时抛出
 * @author ziye
 */
public class VideoNameDuplicateException extends VideoBusinessException {

    private static final Integer ERROR_CODE = 409;

    /**
     * 构造函数
     * @param videoName 重复的视频名称
     */
    public VideoNameDuplicateException(String videoName) {
        super(ERROR_CODE, "视频名称已存在: " + videoName);
    }


} 