package com.fyp.handsome.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fyp.handsome.dto.Result;
import com.fyp.handsome.dto.video.VideoInfoCreateRequest;
import com.fyp.handsome.dto.video.VideoInfoPageResponse;
import com.fyp.handsome.dto.video.VideoInfoQueryRequest;
import com.fyp.handsome.dto.video.VideoInfoResponse;
import com.fyp.handsome.dto.video.VideoInfoUpdateRequest;
import com.fyp.handsome.service.VideoMvpService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频信息管理MVP控制器
 * 提供RESTful API接口，专注于核心CRUD功能
 * @author ziye
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/videos")
@RequiredArgsConstructor
public class VideoMvpController {

    private final VideoMvpService videoMvpService;

    /**
     * 创建视频信息
     * POST /api/v1/videos
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Result<VideoInfoResponse> createVideo(@RequestBody VideoInfoCreateRequest request) {
        log.info("接收到创建视频信息请求，视频名称：{}", request.getVideoName());
        
        try {
            VideoInfoResponse response = videoMvpService.createVideo(request);
            return Result.success("视频信息创建成功", response);
            
        } catch (Exception e) {
            log.error("创建视频信息失败，视频名称：{}，错误：{}", request.getVideoName(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 分页查询视频信息
     * GET /api/v1/videos
     */
    @GetMapping
    public Result<VideoInfoPageResponse> queryVideos(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String videoName,
            @RequestParam(required = false) String shootingLocation,
            @RequestParam(required = false) String videoSource,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        
        log.info("接收到分页查询视频信息请求，页码：{}，大小：{}", page, size);
        
        try {
            // 构建查询请求
            VideoInfoQueryRequest request = new VideoInfoQueryRequest();
            request.setPage(page);
            request.setSize(size);
            request.setVideoName(videoName);
            request.setShootingLocation(shootingLocation);
            request.setVideoSource(videoSource);
            
            // TODO: 处理时间参数的字符串转换
            // 这里暂时不处理时间参数，在后续版本中完善
            
            VideoInfoPageResponse response = videoMvpService.queryVideos(request);
            return Result.success("查询成功", response);
            
        } catch (Exception e) {
            log.error("分页查询视频信息失败，错误：{}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 根据ID查询视频信息
     * GET /api/v1/videos/{id}
     */
    @GetMapping("/{id}")
    public Result<VideoInfoResponse> getVideoById(@PathVariable Long id) {
        log.info("接收到根据ID查询视频信息请求，ID：{}", id);
        
        try {
            VideoInfoResponse response = videoMvpService.getVideoById(id);
            return Result.success("查询成功", response);
            
        } catch (Exception e) {
            log.error("根据ID查询视频信息失败，ID：{}，错误：{}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 更新视频信息
     * PUT /api/v1/videos/{id}
     */
    @PutMapping("/{id}")
    public Result<VideoInfoResponse> updateVideo(
            @PathVariable Long id,
            @RequestBody VideoInfoUpdateRequest request) {
        
        log.info("接收到更新视频信息请求，ID：{}", id);
        
        try {
            VideoInfoResponse response = videoMvpService.updateVideo(id, request);
            return Result.success("更新成功", response);
            
        } catch (Exception e) {
            log.error("更新视频信息失败，ID：{}，错误：{}", id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 删除视频信息
     * DELETE /api/v1/videos/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteVideo(@PathVariable Long id) {
        log.info("接收到删除视频信息请求，ID：{}", id);
        
        try {
            videoMvpService.deleteVideo(id);
            return Result.<Void>success("删除成功", null);
            
        } catch (Exception e) {
            log.error("删除视频信息失败，ID：{}，错误：{}", id, e.getMessage(), e);
            throw e;
        }
    }
} 