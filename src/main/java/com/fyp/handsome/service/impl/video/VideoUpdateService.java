package com.fyp.handsome.service.impl.video;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fyp.handsome.dto.video.VideoInfoUpdateRequest;
import com.fyp.handsome.entity.Video;
import com.fyp.handsome.exception.VideoNameDuplicateException;
import com.fyp.handsome.exception.VideoNotFoundException;
import com.fyp.handsome.exception.VideoValidationException;
import com.fyp.handsome.mapper.VideoMapper;
import com.fyp.handsome.util.BeanCopyUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频信息更新服务
 * 专门负责视频信息的更新逻辑
 * @author ziye
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VideoUpdateService {

    private final VideoMapper videoMapper;

    /**
     * 更新视频信息
     * @param id 视频ID
     * @param request 更新请求
     * @return 更新后的视频实体
     */
    public Video updateVideo(Long id, VideoInfoUpdateRequest request) {
        log.info("开始执行视频更新逻辑，ID：{}", id);
        
        // 1. 参数校验
        validateUpdateRequest(id, request);
        
        // 2. 查询原始记录
        Video existingVideo = getExistingVideo(id);
        
        // 3. 检查视频名称唯一性（如果要更新名称）
        if (StringUtils.hasText(request.getVideoName()) && 
            !request.getVideoName().equals(existingVideo.getVideoName())) {
            checkVideoNameUniqueForUpdate(request.getVideoName(), id);
        }
        
        // 4. 复制更新字段
        BeanCopyUtils.copyNonNullProperties(request, existingVideo);
        
        // 5. 执行更新
        int result = videoMapper.updateById(existingVideo);
        if (result <= 0) {
            throw new RuntimeException("更新视频信息失败");
        }
        
        log.info("视频信息更新成功，ID：{}，名称：{}", id, existingVideo.getVideoName());
        return existingVideo;
    }

    /**
     * 校验更新请求参数
     * @param id 视频ID
     * @param request 更新请求
     */
    private void validateUpdateRequest(Long id, VideoInfoUpdateRequest request) {
        log.debug("开始校验更新请求参数，ID：{}", id);
        
        // 检查ID
        if (id == null || id <= 0) {
            throw new VideoValidationException("视频ID不能为空或小于等于0");
        }
        
        // 检查是否有更新内容
        if (isEmptyUpdateRequest(request)) {
            throw new VideoValidationException("更新请求不能为空，至少需要一个更新字段");
        }
        
        // 检查字段长度
        if (request.getVideoName() != null && request.getVideoName().length() > 255) {
            throw new VideoValidationException("视频名称长度不能超过255字符");
        }
        if (request.getShootingLocation() != null && request.getShootingLocation().length() > 255) {
            throw new VideoValidationException("拍摄地点长度不能超过255字符");
        }
        if (request.getVideoSource() != null && request.getVideoSource().length() > 255) {
            throw new VideoValidationException("视频来源长度不能超过255字符");
        }
        if (request.getFilePath() != null && request.getFilePath().length() > 500) {
            throw new VideoValidationException("文件路径长度不能超过500字符");
        }
        
        // 检查拍摄时间不能大于当前时间
        if (request.getShootingTime() != null && request.getShootingTime().isAfter(LocalDateTime.now())) {
            throw new VideoValidationException("拍摄时间不能大于当前时间");
        }
        
        // 检查数值范围
        if (request.getFileSize() != null && request.getFileSize() < 0) {
            throw new VideoValidationException("文件大小必须大于等于0");
        }
        if (request.getDuration() != null && request.getDuration() < 0) {
            throw new VideoValidationException("视频时长必须大于等于0");
        }
        
        log.debug("更新请求参数校验通过");
    }

    /**
     * 检查更新请求是否为空
     * @param request 更新请求
     * @return 是否为空
     */
    private boolean isEmptyUpdateRequest(VideoInfoUpdateRequest request) {
        return request.getVideoName() == null &&
               request.getShootingTime() == null &&
               request.getShootingLocation() == null &&
               request.getVideoSource() == null &&
               request.getFilePath() == null &&
               request.getFileSize() == null &&
               request.getDuration() == null;
    }

    /**
     * 获取现有视频记录
     * @param id 视频ID
     * @return 视频实体
     */
    private Video getExistingVideo(Long id) {
        Video video = videoMapper.selectById(id);
        if (video == null) {
            throw new VideoNotFoundException(id);
        }
        return video;
    }

    /**
     * 检查视频名称唯一性（更新时排除自身）
     * @param videoName 视频名称
     * @param excludeId 排除的ID
     */
    private void checkVideoNameUniqueForUpdate(String videoName, Long excludeId) {
        log.debug("开始检查视频名称唯一性（更新时），视频名称：{}，排除ID：{}", videoName, excludeId);
        
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getVideoName, videoName)
                   .eq(Video::getStatus, 1)
                   .ne(Video::getId, excludeId);
        
        long count = videoMapper.selectCount(queryWrapper);
        if (count > 0) {
            log.warn("视频名称已存在（更新时）：{}", videoName);
            throw new VideoNameDuplicateException(videoName);
        }
        
        log.debug("视频名称唯一性检查通过（更新时）");
    }
} 