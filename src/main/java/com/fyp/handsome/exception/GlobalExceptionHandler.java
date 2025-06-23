package com.fyp.handsome.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fyp.handsome.dto.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理器
 * 统一处理各种异常，返回规范的错误响应
 * @author ziye
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理视频不存在异常
     * @param e 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(VideoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleVideoNotFoundException(VideoNotFoundException e) {
        log.warn("视频信息不存在：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理视频名称重复异常
     * @param e 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(VideoNameDuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<Void> handleVideoNameDuplicateException(VideoNameDuplicateException e) {
        log.warn("视频名称重复：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理视频参数校验异常
     * @param e 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(VideoValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleVideoValidationException(VideoValidationException e) {
        log.warn("视频参数校验失败：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理视频业务异常
     * @param e 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(VideoBusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleVideoBusinessException(VideoBusinessException e) {
        log.warn("视频业务异常：{}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理非法参数异常
     * @param e 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("非法参数异常：{}", e.getMessage());
        return Result.error(400, e.getMessage());
    }

    /**
     * 处理运行时异常
     * @param e 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：{}", e.getMessage(), e);
        return Result.error(500, "系统内部错误：" + e.getMessage());
    }

    /**
     * 处理其他未知异常
     * @param e 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("未知异常：{}", e.getMessage(), e);
        return Result.error(500, "系统内部错误");
    }
} 