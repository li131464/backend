package com.fyp.handsome.dto.video;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 视频信息响应DTO
 * 用于返回视频信息数据
 * @author ziye
 */
@Data
public class VideoInfoResponse {

    /**
     * 视频编号
     */
    private Long id;

    /**
     * 视频名称
     */
    private String videoName;

    /**
     * 拍摄时间
     */
    private LocalDateTime shootingTime;

    /**
     * 拍摄地点
     */
    private String shootingLocation;

    /**
     * 视频来源
     */
    private String videoSource;

    /**
     * 视频文件路径
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 状态：1-正常，0-删除
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 