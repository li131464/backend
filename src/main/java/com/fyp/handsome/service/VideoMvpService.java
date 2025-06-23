package com.fyp.handsome.service;

import com.fyp.handsome.dto.video.VideoInfoCreateRequest;
import com.fyp.handsome.dto.video.VideoInfoPageResponse;
import com.fyp.handsome.dto.video.VideoInfoQueryRequest;
import com.fyp.handsome.dto.video.VideoInfoResponse;
import com.fyp.handsome.dto.video.VideoInfoUpdateRequest;

/**
 * 视频信息管理MVP服务接口
 * 专注于核心的CRUD功能
 * @author ziye
 */
public interface VideoMvpService {

    /**
     * 创建视频信息
     * @param request 创建请求
     * @return 创建的视频信息
     */
    VideoInfoResponse createVideo(VideoInfoCreateRequest request);

    /**
     * 分页查询视频信息
     * @param request 查询请求
     * @return 分页查询结果
     */
    VideoInfoPageResponse queryVideos(VideoInfoQueryRequest request);

    /**
     * 根据ID查询视频信息
     * @param id 视频ID
     * @return 视频信息
     */
    VideoInfoResponse getVideoById(Long id);

    /**
     * 更新视频信息
     * @param id 视频ID
     * @param request 更新请求
     * @return 更新后的视频信息
     */
    VideoInfoResponse updateVideo(Long id, VideoInfoUpdateRequest request);

    /**
     * 删除视频信息（逻辑删除）
     * @param id 视频ID
     */
    void deleteVideo(Long id);
} 