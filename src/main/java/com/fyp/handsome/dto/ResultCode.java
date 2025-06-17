package com.fyp.handsome.dto;

import lombok.Getter;

/**
 * 统一响应状态码枚举
 * @author fyp
 */
@Getter
public enum ResultCode {
    
    // =================== 成功状态码 ===================
    SUCCESS(200, "操作成功"),
    
    // =================== 客户端错误状态码 4xx ===================
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    CONFLICT(409, "数据冲突"),
    VALIDATION_ERROR(422, "参数校验失败"),
    INVALID_PARAM(400, "参数无效"),
    
    // =================== 服务端错误状态码 5xx ===================
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),
    
    // =================== 业务错误状态码 1xxx ===================
    // 用户相关错误 10xx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    USERNAME_OR_PASSWORD_ERROR(1003, "用户名或密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    PASSWORD_ERROR(1005, "密码错误"),
    
    // 视频相关错误 11xx
    VIDEO_NOT_FOUND(1101, "视频不存在"),
    VIDEO_ALREADY_EXISTS(1102, "视频已存在"),
    VIDEO_UPLOAD_ERROR(1103, "视频上传失败"),
    VIDEO_FORMAT_ERROR(1104, "视频格式不支持"),
    VIDEO_SIZE_EXCEEDED(1105, "视频文件过大"),
    VIDEO_ADD_FAILED(1106, "视频添加失败"),
    VIDEO_UPDATE_FAILED(1107, "视频更新失败"),
    VIDEO_DELETE_FAILED(1108, "视频删除失败"),
    
    // 分析相关错误 12xx
    ANALYSIS_NOT_FOUND(1201, "分析结果不存在"),
    ANALYSIS_FAILED(1202, "视频分析失败"),
    ANALYSIS_IN_PROGRESS(1203, "视频正在分析中"),
    ANALYSIS_TYPE_NOT_SUPPORTED(1204, "分析类型不支持"),
    
    // 权限相关错误 13xx
    PERMISSION_DENIED(1301, "权限不足"),
    ROLE_NOT_FOUND(1302, "角色不存在"),
    PERMISSION_NOT_FOUND(1303, "权限不存在"),
    ROLE_ALREADY_EXISTS(1304, "角色已存在"),
    
    // 监控点相关错误 14xx
    MONITOR_POINT_NOT_FOUND(1401, "监控点不存在"),
    MONITOR_POINT_OFFLINE(1402, "监控点离线"),
    MONITOR_POINT_ALREADY_EXISTS(1403, "监控点已存在"),
    
    // 系统相关错误 15xx
    SYSTEM_BUSY(1501, "系统繁忙，请稍后重试"),
    DATA_BACKUP_FAILED(1502, "数据备份失败"),
    DATA_RESTORE_FAILED(1503, "数据恢复失败"),
    CONFIG_ERROR(1504, "系统配置错误");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 状态消息
     */
    private final String message;
    
    /**
     * 构造函数
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
} 