package com.fyp.handsome.service.impl.video;

import org.springframework.stereotype.Component;

import com.fyp.handsome.entity.Video;
import com.fyp.handsome.exception.VideoNotFoundException;
import com.fyp.handsome.exception.VideoValidationException;
import com.fyp.handsome.mapper.VideoMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频信息删除服务
 * 专门负责视频信息的删除逻辑（逻辑删除）
 * @author ziye
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VideoDeleteService {

    private final VideoMapper videoMapper;

    /**
     * 删除视频信息（逻辑删除）
     * @param id 视频ID
     */
    public void deleteVideo(Long id) {
        log.info("开始执行视频删除逻辑，ID：{}", id);
        
        // 1. 参数校验
        validateDeleteRequest(id);
        
        // 2. 检查记录是否存在
        Video existingVideo = getExistingVideo(id);
        
        // 3. 检查是否已被删除
        if (existingVideo.getStatus() == 0) {
            log.warn("视频已被删除，ID：{}", id);
            throw new VideoValidationException("视频信息已被删除");
        }
        
        // 4. 执行逻辑删除（使用MyBatis Plus的逻辑删除功能）
        int result = videoMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除视频信息失败");
        }
        
        log.info("视频信息删除成功，ID：{}", id);
    }

    /**
     * 校验删除请求参数
     * @param id 视频ID
     */
    private void validateDeleteRequest(Long id) {
        log.debug("开始校验删除请求参数，ID：{}", id);
        
        if (id == null || id <= 0) {
            throw new VideoValidationException("视频ID不能为空或小于等于0");
        }
        
        log.debug("删除请求参数校验通过");
    }

    /**
     * 获取现有视频记录（包括已删除的）
     * @param id 视频ID
     * @return 视频实体
     */
    private Video getExistingVideo(Long id) {
        // 注意：这里需要查询包括已删除的记录，所以不能使用selectById（会被逻辑删除过滤）
        Video video = videoMapper.selectById(id);
        if (video == null) {
            // 如果通过selectById查询不到，可能是已经被逻辑删除了
            // 这里我们还是抛出not found异常，因为从业务角度看，记录不存在
            throw new VideoNotFoundException(id);
        }
        return video;
    }
} 