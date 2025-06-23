package com.fyp.handsome.dto.video;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 视频信息更新请求DTO
 * 用于更新视频信息的请求参数（所有字段都是可选的）
 * @author ziye
 */
@Data
public class VideoInfoUpdateRequest {

    /**
     * 视频名称（可选）
     */
    private String videoName;

    /**
     * 拍摄时间（可选）
     */
    private LocalDateTime shootingTime;

    /**
     * 拍摄地点（可选）
     */
    private String shootingLocation;

    /**
     * 视频来源（可选）
     */
    private String videoSource;

    /**
     * 视频文件路径（可选）
     */
    private String filePath;

    /**
     * 文件大小（可选，单位：字节）
     */
    private Long fileSize;

    /**
     * 视频时长（可选，单位：秒）
     */
    private Integer duration;
} 