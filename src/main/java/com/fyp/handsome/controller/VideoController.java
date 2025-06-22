package com.fyp.handsome.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyp.handsome.dto.Result;
import com.fyp.handsome.dto.ResultCode;
import com.fyp.handsome.entity.Video;
import com.fyp.handsome.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频信息管理控制器
 * @author ziye
 */
@Slf4j
@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoController {

    private final VideoService videoService;

    // =================== 视频基础CRUD ===================

    /**
     * 新增视频信息
     */
    @PostMapping
    public Result<Video> addVideo(@RequestBody Video video) {
        try {
            log.info("新增视频信息：{}", video.getVideoName());
            
            if (videoService.addVideo(video)) {
                return Result.success("视频信息添加成功", video);
            } else {
                return Result.error(ResultCode.VIDEO_ADD_FAILED);
            }
            
        } catch (Exception e) {
            log.error("新增视频信息失败：{}", e.getMessage(), e);
            return Result.error("新增视频信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取视频信息
     */
    @GetMapping("/{id}")
    public Result<Video> getVideoById(@PathVariable Long id) {
        try {
            Video video = videoService.getById(id);
            if (video != null) {
                return Result.success(video);
            } else {
                return Result.error(ResultCode.VIDEO_NOT_FOUND);
            }
            
        } catch (Exception e) {
            log.error("获取视频信息失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("获取视频信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新视频信息
     */
    @PutMapping("/{id}")
    public Result<Void> updateVideo(@PathVariable Long id, @RequestBody Video video) {
        try {
            video.setId(id);
            
            if (videoService.updateVideo(video)) {
                return Result.success();
            } else {
                return Result.error(ResultCode.VIDEO_UPDATE_FAILED);
            }
            
        } catch (Exception e) {
            log.error("更新视频信息失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("更新视频信息失败：" + e.getMessage());
        }
    }

    /**
     * 删除视频信息
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteVideo(@PathVariable Long id) {
        try {
            if (videoService.deleteVideo(id)) {
                return Result.success();
            } else {
                return Result.error(ResultCode.VIDEO_DELETE_FAILED);
            }
            
        } catch (Exception e) {
            log.error("删除视频信息失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("删除视频信息失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除视频信息
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteVideos(@RequestBody List<Long> ids) {
        try {
            if (videoService.deleteVideos(ids)) {
                return Result.success();
            } else {
                return Result.error("批量删除视频信息失败");
            }
            
        } catch (Exception e) {
            log.error("批量删除视频信息失败，ids：{}，错误：{}", ids, e.getMessage(), e);
            return Result.error("批量删除视频信息失败：" + e.getMessage());
        }
    }

    // =================== 视频查询 ===================

    /**
     * 分页查询视频信息
     */
    @GetMapping("/page")
    public Result<IPage<Video>> getVideosPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String videoName,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            Page<Video> page = new Page<>(current, size);
            IPage<Video> result = videoService.getVideosPage(page, videoName, location, source, startTime, endTime);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询视频信息失败，错误：{}", e.getMessage(), e);
            return Result.error("分页查询视频信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据时间范围查询视频
     */
    @GetMapping("/time-range")
    public Result<List<Video>> getVideosByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            List<Video> videos = videoService.getVideosByTimeRange(startTime, endTime);
            return Result.success(videos);
            
        } catch (Exception e) {
            log.error("根据时间范围查询视频失败，错误：{}", e.getMessage(), e);
            return Result.error("根据时间范围查询视频失败：" + e.getMessage());
        }
    }

    /**
     * 根据地点查询视频
     */
    @GetMapping("/location/{location}")
    public Result<List<Video>> getVideosByLocation(@PathVariable String location) {
        try {
            List<Video> videos = videoService.getVideosByLocation(location);
            return Result.success(videos);
            
        } catch (Exception e) {
            log.error("根据地点查询视频失败，location：{}，错误：{}", location, e.getMessage(), e);
            return Result.error("根据地点查询视频失败：" + e.getMessage());
        }
    }

    /**
     * 根据来源查询视频
     */
    @GetMapping("/source/{source}")
    public Result<List<Video>> getVideosBySource(@PathVariable String source) {
        try {
            List<Video> videos = videoService.getVideosBySource(source);
            return Result.success(videos);
            
        } catch (Exception e) {
            log.error("根据来源查询视频失败，source：{}，错误：{}", source, e.getMessage(), e);
            return Result.error("根据来源查询视频失败：" + e.getMessage());
        }
    }

    // =================== 视频统计 ===================

    /**
     * 获取视频统计概览
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getVideoStatistics() {
        try {
            Map<String, Object> statistics = videoService.getVideoStatistics();
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取视频统计概览失败，错误：{}", e.getMessage(), e);
            return Result.error("获取视频统计概览失败：" + e.getMessage());
        }
    }

    /**
     * 获取视频总数
     */
    @GetMapping("/count")
    public Result<Long> getVideoTotalCount() {
        try {
            Long count = videoService.count();
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("获取视频总数失败，错误：{}", e.getMessage(), e);
            return Result.error("获取视频总数失败：" + e.getMessage());
        }
    }
} 