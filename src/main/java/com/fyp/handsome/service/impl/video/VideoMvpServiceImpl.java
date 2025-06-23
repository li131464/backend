package com.fyp.handsome.service.impl.video;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyp.handsome.dto.video.VideoInfoCreateRequest;
import com.fyp.handsome.dto.video.VideoInfoPageResponse;
import com.fyp.handsome.dto.video.VideoInfoQueryRequest;
import com.fyp.handsome.dto.video.VideoInfoResponse;
import com.fyp.handsome.dto.video.VideoInfoUpdateRequest;
import com.fyp.handsome.entity.Video;
import com.fyp.handsome.exception.VideoNotFoundException;
import com.fyp.handsome.service.VideoMvpService;
import com.fyp.handsome.util.BeanCopyUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频信息管理MVP服务实现类
 * 专注于核心CRUD功能的实现
 * @author ziye
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VideoMvpServiceImpl implements VideoMvpService {

    // 依赖注入其他服务组件
    private final VideoCreateService videoCreateService;
    private final VideoQueryService videoQueryService;
    private final VideoUpdateService videoUpdateService;
    private final VideoDeleteService videoDeleteService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoInfoResponse createVideo(VideoInfoCreateRequest request) {
        log.info("开始创建视频信息，视频名称：{}", request.getVideoName());
        
        try {
            // 委托给专门的创建服务
            Video video = videoCreateService.createVideo(request);
            
            // 转换为响应DTO
            VideoInfoResponse response = BeanCopyUtils.convertToResponse(video);
            
            log.info("视频信息创建成功，ID：{}，名称：{}", response.getId(), response.getVideoName());
            return response;
            
        } catch (Exception e) {
            log.error("创建视频信息失败，视频名称：{}，错误：{}", request.getVideoName(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public VideoInfoPageResponse queryVideos(VideoInfoQueryRequest request) {
        log.info("开始分页查询视频信息，页码：{}，大小：{}", request.getPage(), request.getSize());
        
        try {
            // 委托给专门的查询服务
            VideoInfoPageResponse response = videoQueryService.queryVideosPage(request);
            
            log.info("分页查询视频信息成功，总数：{}，当前页：{}", response.getTotal(), response.getPage());
            return response;
            
        } catch (Exception e) {
            log.error("分页查询视频信息失败，错误：{}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public VideoInfoResponse getVideoById(Long id) {
        log.info("开始根据ID查询视频信息，ID：{}", id);
        
        try {
            // 委托给专门的查询服务
            Video video = videoQueryService.getVideoById(id);
            if (video == null) {
                throw new VideoNotFoundException(id);
            }
            
            // 转换为响应DTO
            VideoInfoResponse response = BeanCopyUtils.convertToResponse(video);
            
            log.info("根据ID查询视频信息成功，ID：{}，名称：{}", id, response.getVideoName());
            return response;
            
        } catch (Exception e) {
            log.error("根据ID查询视频信息失败，ID：{}，错误：{}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VideoInfoResponse updateVideo(Long id, VideoInfoUpdateRequest request) {
        log.info("开始更新视频信息，ID：{}", id);
        
        try {
            // 委托给专门的更新服务
            Video video = videoUpdateService.updateVideo(id, request);
            
            // 转换为响应DTO
            VideoInfoResponse response = BeanCopyUtils.convertToResponse(video);
            
            log.info("视频信息更新成功，ID：{}，名称：{}", id, response.getVideoName());
            return response;
            
        } catch (Exception e) {
            log.error("更新视频信息失败，ID：{}，错误：{}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVideo(Long id) {
        log.info("开始删除视频信息，ID：{}", id);
        
        try {
            // 委托给专门的删除服务
            videoDeleteService.deleteVideo(id);
            
            log.info("视频信息删除成功，ID：{}", id);
            
        } catch (Exception e) {
            log.error("删除视频信息失败，ID：{}，错误：{}", id, e.getMessage(), e);
            throw e;
        }
    }
} 