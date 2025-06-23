package com.fyp.handsome.service.impl.video;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fyp.handsome.dto.video.VideoInfoCreateRequest;
import com.fyp.handsome.entity.Video;
import com.fyp.handsome.exception.VideoNameDuplicateException;
import com.fyp.handsome.exception.VideoValidationException;
import com.fyp.handsome.mapper.VideoMapper;
import com.fyp.handsome.util.BeanCopyUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频信息创建服务
 * 专门负责视频信息的创建逻辑
 * @author ziye
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VideoCreateService {

    private final VideoMapper videoMapper;

    /**
     * 创建视频信息
     */
    public Video createVideo(VideoInfoCreateRequest request) {
        log.info("开始执行视频创建逻辑，视频名称：{}", request.getVideoName());
        
        // 1. 参数校验
        validateCreateRequest(request);
        
        // 2. 检查视频名称唯一性
        checkVideoNameUnique(request.getVideoName());
        
        // 3. 转换为实体对象
        Video video = BeanCopyUtils.convertToEntity(request);
        
        // 4. 设置创建时间
        video.setCreateTime(LocalDateTime.now());
        video.setUpdateTime(LocalDateTime.now());
        
        // 5. 保存到数据库
        int result = videoMapper.insert(video);
        if (result <= 0) {
            throw new RuntimeException("保存视频信息失败");
        }
        
        log.info("视频信息创建成功，ID：{}，名称：{}", video.getId(), video.getVideoName());
        return video;
    }

    /**
     * 校验创建请求参数
     */
    private void validateCreateRequest(VideoInfoCreateRequest request) {
        // 检查必填字段
        if (!StringUtils.hasText(request.getVideoName())) {
            throw new VideoValidationException("视频名称不能为空");
        }
        if (request.getShootingTime() == null) {
            throw new VideoValidationException("拍摄时间不能为空");
        }
        
        // 检查拍摄时间不能大于当前时间
        if (request.getShootingTime().isAfter(LocalDateTime.now())) {
            throw new VideoValidationException("拍摄时间不能大于当前时间");
        }
    }

    /**
     * 检查视频名称唯一性
     */
    private void checkVideoNameUnique(String videoName) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Video::getVideoName, videoName)
                   .eq(Video::getStatus, 1);
        
        long count = videoMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new VideoNameDuplicateException(videoName);
        }
    }
} 