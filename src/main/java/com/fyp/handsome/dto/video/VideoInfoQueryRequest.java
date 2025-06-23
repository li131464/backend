package com.fyp.handsome.dto.video;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 视频信息查询请求DTO
 * 用于分页查询视频信息的请求参数
 * @author ziye
 */
@Data
public class VideoInfoQueryRequest {

    /**
     * 页码（从1开始，默认为1）
     */
    private Integer page = 1;

    /**
     * 每页大小（默认为10，最大100）
     */
    private Integer size = 10;

    /**
     * 视频名称（模糊查询）
     */
    private String videoName;

    /**
     * 拍摄地点（模糊查询）
     */
    private String shootingLocation;

    /**
     * 视频来源（精确查询）
     */
    private String videoSource;

    /**
     * 拍摄开始时间
     */
    private LocalDateTime startTime;

    /**
     * 拍摄结束时间
     */
    private LocalDateTime endTime;
} 