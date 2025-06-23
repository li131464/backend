package com.fyp.handsome.service.impl.video;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyp.handsome.dto.video.VideoInfoPageResponse;
import com.fyp.handsome.dto.video.VideoInfoQueryRequest;
import com.fyp.handsome.dto.video.VideoInfoResponse;
import com.fyp.handsome.entity.Video;
import com.fyp.handsome.exception.VideoValidationException;
import com.fyp.handsome.mapper.VideoMapper;
import com.fyp.handsome.util.BeanCopyUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频信息查询服务
 * 专门负责视频信息的查询逻辑
 * @author ziye
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VideoQueryService {

    private final VideoMapper videoMapper;

    /**
     * 根据ID查询视频信息
     * @param id 视频ID
     * @return 视频实体
     */
    public Video getVideoById(Long id) {
        log.info("开始根据ID查询视频信息，ID：{}", id);
        
        if (id == null || id <= 0) {
            throw new VideoValidationException("视频ID不能为空或小于等于0");
        }
        
        Video video = videoMapper.selectById(id);
        log.info("根据ID查询视频信息完成，ID：{}，结果：{}", id, video != null ? "找到" : "未找到");
        
        return video;
    }

    /**
     * 分页查询视频信息
     * @param request 查询请求
     * @return 分页查询结果
     */
    public VideoInfoPageResponse queryVideosPage(VideoInfoQueryRequest request) {
        log.info("开始分页查询视频信息，页码：{}，大小：{}", request.getPage(), request.getSize());
        
        // 1. 参数校验
        validateQueryRequest(request);
        
        // 2. 构建分页对象
        Page<Video> page = new Page<>(request.getPage(), request.getSize());
        
        // 3. 构建查询条件
        LambdaQueryWrapper<Video> queryWrapper = buildQueryWrapper(request);
        
        // 4. 执行分页查询
        IPage<Video> result = videoMapper.selectPage(page, queryWrapper);
        
        // 5. 转换为响应DTO
        VideoInfoPageResponse response = convertToPageResponse(result);
        
        log.info("分页查询视频信息完成，总数：{}，当前页：{}", response.getTotal(), response.getPage());
        return response;
    }

    /**
     * 校验查询请求参数
     * @param request 查询请求
     */
    private void validateQueryRequest(VideoInfoQueryRequest request) {
        if (request.getPage() == null || request.getPage() < 1) {
            throw new VideoValidationException("页码必须大于0");
        }
        if (request.getSize() == null || request.getSize() < 1) {
            throw new VideoValidationException("每页大小必须大于0");
        }
        if (request.getSize() > 100) {
            throw new VideoValidationException("每页大小不能超过100");
        }
    }

    /**
     * 构建查询条件
     * @param request 查询请求
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<Video> buildQueryWrapper(VideoInfoQueryRequest request) {
        LambdaQueryWrapper<Video> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询有效记录（未删除的）
        queryWrapper.eq(Video::getStatus, 1);
        
        // 视频名称模糊查询
        if (StringUtils.hasText(request.getVideoName())) {
            queryWrapper.like(Video::getVideoName, request.getVideoName());
        }
        
        // 拍摄地点模糊查询
        if (StringUtils.hasText(request.getShootingLocation())) {
            queryWrapper.like(Video::getShootingLocation, request.getShootingLocation());
        }
        
        // 视频来源精确查询
        if (StringUtils.hasText(request.getVideoSource())) {
            queryWrapper.eq(Video::getVideoSource, request.getVideoSource());
        }
        
        // 时间范围查询
        if (request.getStartTime() != null) {
            queryWrapper.ge(Video::getShootingTime, request.getStartTime());
        }
        if (request.getEndTime() != null) {
            queryWrapper.le(Video::getShootingTime, request.getEndTime());
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(Video::getCreateTime);
        
        return queryWrapper;
    }

    /**
     * 转换分页结果为响应DTO
     * @param page 分页结果
     * @return 分页响应DTO
     */
    private VideoInfoPageResponse convertToPageResponse(IPage<Video> page) {
        VideoInfoPageResponse response = new VideoInfoPageResponse();
        
        // 转换数据列表
        List<VideoInfoResponse> content = BeanCopyUtils.convertToResponseList(page.getRecords());
        response.setContent(content);
        
        // 设置分页信息
        response.setPage((int) page.getCurrent());
        response.setSize((int) page.getSize());
        response.setTotal(page.getTotal());
        response.setTotalPages((int) page.getPages());
        
        // 设置分页状态
        response.setIsFirst(page.getCurrent() == 1);
        response.setIsLast(page.getCurrent() == page.getPages());
        response.setHasPrevious(page.getCurrent() > 1);
        response.setHasNext(page.getCurrent() < page.getPages());
        
        return response;
    }
} 