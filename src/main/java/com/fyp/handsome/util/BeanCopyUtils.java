package com.fyp.handsome.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.fyp.handsome.dto.video.VideoInfoCreateRequest;
import com.fyp.handsome.dto.video.VideoInfoResponse;
import com.fyp.handsome.dto.video.VideoInfoUpdateRequest;
import com.fyp.handsome.entity.Video;

/**
 * 对象转换工具类
 * 用于实体类与DTO之间的转换
 * @author ziye
 */
public class BeanCopyUtils {

    /**
     * 将创建请求DTO转换为Video实体
     * @param request 创建请求DTO
     * @return Video实体
     */
    public static Video convertToEntity(VideoInfoCreateRequest request) {
        if (request == null) {
            return null;
        }

        Video video = new Video();
        video.setVideoName(request.getVideoName());
        video.setShootingTime(request.getShootingTime());
        video.setShootingLocation(request.getShootingLocation());
        video.setVideoSource(request.getVideoSource());
        video.setFilePath(request.getFilePath());
        video.setFileSize(request.getFileSize());
        video.setDuration(request.getDuration());
        video.setStatus(1); // 默认状态为正常
        return video;
    }

    /**
     * 将Video实体转换为响应DTO
     * @param video Video实体
     * @return 响应DTO
     */
    public static VideoInfoResponse convertToResponse(Video video) {
        if (video == null) {
            return null;
        }

        VideoInfoResponse response = new VideoInfoResponse();
        response.setId(video.getId());
        response.setVideoName(video.getVideoName());
        response.setShootingTime(video.getShootingTime());
        response.setShootingLocation(video.getShootingLocation());
        response.setVideoSource(video.getVideoSource());
        response.setFilePath(video.getFilePath());
        response.setFileSize(video.getFileSize());
        response.setDuration(video.getDuration());
        response.setStatus(video.getStatus());
        response.setCreateTime(video.getCreateTime());
        response.setUpdateTime(video.getUpdateTime());
        return response;
    }

    /**
     * 将Video实体列表转换为响应DTO列表
     * @param videos Video实体列表
     * @return 响应DTO列表
     */
    public static List<VideoInfoResponse> convertToResponseList(List<Video> videos) {
        if (videos == null) {
            return null;
        }
        return videos.stream()
                    .map(BeanCopyUtils::convertToResponse)
                    .collect(Collectors.toList());
    }

    /**
     * 将更新请求DTO的非空字段复制到Video实体
     * @param request 更新请求DTO
     * @param video 目标Video实体
     */
    public static void copyNonNullProperties(VideoInfoUpdateRequest request, Video video) {
        if (request == null || video == null) {
            return;
        }

        // 只复制非空字段
        if (request.getVideoName() != null) {
            video.setVideoName(request.getVideoName());
        }
        if (request.getShootingTime() != null) {
            video.setShootingTime(request.getShootingTime());
        }
        if (request.getShootingLocation() != null) {
            video.setShootingLocation(request.getShootingLocation());
        }
        if (request.getVideoSource() != null) {
            video.setVideoSource(request.getVideoSource());
        }
        if (request.getFilePath() != null) {
            video.setFilePath(request.getFilePath());
        }
        if (request.getFileSize() != null) {
            video.setFileSize(request.getFileSize());
        }
        if (request.getDuration() != null) {
            video.setDuration(request.getDuration());
        }
        
        // 更新修改时间
        video.setUpdateTime(LocalDateTime.now());
    }
} 