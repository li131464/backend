package com.fyp.handsome.service.impl.video;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aliyun.sdk.service.quanmiaolightapp20240801.models.GetVideoAnalysisTaskResponse;
import com.aliyun.sdk.service.quanmiaolightapp20240801.models.SubmitVideoAnalysisTaskResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fyp.handsome.dto.video.VideoInfoCreateRequest;
import com.fyp.handsome.entity.Video;
import com.fyp.handsome.entity.VideoAnalysisResult;
import com.fyp.handsome.exception.VideoNameDuplicateException;
import com.fyp.handsome.exception.VideoValidationException;
import com.fyp.handsome.mapper.VideoAnalysisResultMapper;
import com.fyp.handsome.mapper.VideoMapper;
import com.fyp.handsome.service.impl.analysis.AliyunVideoAnalysisService;
import com.fyp.handsome.util.BeanCopyUtils;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频信息创建服务
 * 专门负责视频信息的创建逻辑，支持自动视频分析
 * @author ziye
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class VideoCreateService {

    private final VideoMapper videoMapper;
    private final VideoAnalysisResultMapper videoAnalysisResultMapper;
    private final AliyunVideoAnalysisService aliyunVideoAnalysisService;
    private final Gson gson = new Gson();

    /**
     * 创建视频信息
     * 如果提供了filePath，将自动触发视频分析任务
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
        
        // 6. 如果有文件路径，异步触发视频分析
        if (StringUtils.hasText(request.getFilePath())) {
            log.info("检测到视频文件路径：{}，开始异步执行视频分析", request.getFilePath());
            triggerVideoAnalysisAsync(video.getId(), request.getFilePath());
        }
        
        log.info("视频信息创建成功，ID：{}，名称：{}", video.getId(), video.getVideoName());
        return video;
    }

    /**
     * 异步触发视频分析任务
     * @param videoId 视频ID
     * @param filePath 视频文件路径
     */
    @Async("videoAnalysisExecutor")
    public void triggerVideoAnalysisAsync(Long videoId, String filePath) {
        try {
            log.info("开始为视频ID：{} 提交分析任务，文件路径：{}", videoId, filePath);
            
            // 提交视频分析任务
            SubmitVideoAnalysisTaskResponse response = aliyunVideoAnalysisService.submitVideoAnalysisTask(filePath);
            String taskId = response.getBody().getData().getTaskId();
            
            log.info("视频分析任务提交成功，videoId：{}，taskId：{}", videoId, taskId);
            
            // 开始轮询查询分析结果
            startPollingAnalysisResult(videoId, taskId, filePath);
            
        } catch (Exception e) {
            log.error("提交视频分析任务失败，videoId：{}，filePath：{}，错误：{}", 
                     videoId, filePath, e.getMessage(), e);
        }
    }

    /**
     * 轮询查询视频分析结果
     * 先等待10秒，然后每隔1秒查询一次，30分钟超时
     * @param videoId 视频ID
     * @param taskId 分析任务ID
     * @param filePath 视频文件路径
     */
    @Async("videoAnalysisExecutor")
    public void startPollingAnalysisResult(Long videoId, String taskId, String filePath) {
        CompletableFuture.runAsync(() -> {
            try {
                log.info("开始轮询视频分析结果，videoId：{}，taskId：{}", videoId, taskId);
                
                // 先等待10秒
                log.info("等待10秒后开始轮询，videoId：{}，taskId：{}", videoId, taskId);
                TimeUnit.SECONDS.sleep(10);
                
                // 设置超时时间为30分钟（1800秒）
                long timeoutMillis = 30 * 60 * 1000; // 30分钟
                long startTime = System.currentTimeMillis();
                long endTime = startTime + timeoutMillis;
                
                log.info("开始轮询，超时时间：30分钟，videoId：{}，taskId：{}", videoId, taskId);
                
                int pollingCount = 0; // 轮询计数，用于日志记录
                
                while (System.currentTimeMillis() < endTime) {
                    try {
                        pollingCount++;
                        long remainingTime = (endTime - System.currentTimeMillis()) / 1000; // 剩余秒数
                        
                        log.info("第{}次轮询分析结果，剩余时间：{}秒，videoId：{}，taskId：{}", 
                                pollingCount, remainingTime, videoId, taskId);
                        
                        // 查询分析结果
                        GetVideoAnalysisTaskResponse queryResponse = aliyunVideoAnalysisService.getVideoAnalysisTask(taskId);
                        String taskStatus = queryResponse.getBody().getData().getTaskStatus();
                        
                        log.info("查询到任务状态，videoId：{}，taskId：{}，状态：{}", videoId, taskId, taskStatus);
                        
                        if ("SUCCESSED".equals(taskStatus)) {
                            // 分析完成
                            log.info("视频分析任务完成，videoId：{}，taskId：{}，总轮询次数：{}", videoId, taskId, pollingCount);
                            
                            // 提取分析结果
                            Object analysisResult = aliyunVideoAnalysisService.extractAnalysisResult(queryResponse);
                            
                            if (analysisResult != null) {
                                // 保存分析结果到数据库
                                saveAnalysisResultToDatabase(videoId, taskId, analysisResult);
                            } else {
                                log.warn("分析结果为空，videoId：{}，taskId：{}", videoId, taskId);
                            }
                            
                            break; // 任务完成，退出轮询
                            
                        } else if ("FAILED".equals(taskStatus)) {
                            // 分析失败
                            log.error("视频分析任务失败，videoId：{}，taskId：{}，总轮询次数：{}", videoId, taskId, pollingCount);
                            break; // 任务失败，退出轮询
                            
                        } else if ("RUNNING".equals(taskStatus)) {
                            // 任务还在运行中，继续轮询
                            log.info("视频分析任务运行中，videoId：{}，taskId：{}，继续轮询", videoId, taskId);
                            
                        } else {
                            // 其他状态
                            log.warn("未知的任务状态，videoId：{}，taskId：{}，状态：{}", videoId, taskId, taskStatus);
                        }
                        
                        // 等待1秒后继续轮询
                        TimeUnit.SECONDS.sleep(1);
                        
                    } catch (Exception e) {
                        log.error("轮询分析结果时发生异常，videoId：{}，taskId：{}，错误：{}", 
                                 videoId, taskId, e.getMessage(), e);
                        
                        // 继续轮询，不因为单次异常而停止
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                
                // 检查是否超时
                if (System.currentTimeMillis() >= endTime) {
                    log.warn("轮询超时（30分钟），停止轮询，videoId：{}，taskId：{}，总轮询次数：{}", 
                            videoId, taskId, pollingCount);
                }
                
            } catch (InterruptedException e) {
                log.error("轮询被中断，videoId：{}，taskId：{}", videoId, taskId);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("轮询过程中发生异常，videoId：{}，taskId：{}，错误：{}", 
                         videoId, taskId, e.getMessage(), e);
            }
        });
    }

    /**
     * 保存分析结果到数据库
     * @param videoId 视频ID
     * @param taskId 任务ID
     * @param analysisResult 分析结果对象
     */
    private void saveAnalysisResultToDatabase(Long videoId, String taskId, Object analysisResult) {
        try {
            log.info("开始保存分析结果到数据库，videoId：{}，taskId：{}", videoId, taskId);
            
            // 将分析结果对象转换为JSON字符串
            String analysisResultJson = gson.toJson(analysisResult);
            
            // 检查该视频是否已经有分析结果
            LambdaQueryWrapper<VideoAnalysisResult> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VideoAnalysisResult::getVideoId, videoId)
                       .eq(VideoAnalysisResult::getAnalysisType, "comprehensive_analysis") // 综合分析类型
                       .eq(VideoAnalysisResult::getStatus, 1);
            
            VideoAnalysisResult existingResult = videoAnalysisResultMapper.selectOne(queryWrapper);
            
            if (existingResult != null) {
                // 更新现有记录
                existingResult.setAnalysisResult(analysisResultJson);
                existingResult.setAnalysisTime(LocalDateTime.now());
                existingResult.setConfidenceScore(new BigDecimal("0.95")); // 设置默认置信度
                existingResult.setUpdateTime(LocalDateTime.now());
                
                int updateResult = videoAnalysisResultMapper.updateById(existingResult);
                if (updateResult > 0) {
                    log.info("更新分析结果成功，videoId：{}，taskId：{}，resultId：{}", 
                            videoId, taskId, existingResult.getId());
                } else {
                    log.error("更新分析结果失败，videoId：{}，taskId：{}", videoId, taskId);
                }
                
            } else {
                // 创建新记录
                VideoAnalysisResult newResult = new VideoAnalysisResult();
                newResult.setVideoId(videoId);
                newResult.setAnalysisType("comprehensive_analysis"); // 综合分析类型
                newResult.setAnalysisResult(analysisResultJson);
                newResult.setConfidenceScore(new BigDecimal("0.95")); // 设置默认置信度
                newResult.setAnalysisTime(LocalDateTime.now());
                newResult.setStatus(1);
                newResult.setCreateTime(LocalDateTime.now());
                newResult.setUpdateTime(LocalDateTime.now());
                
                int insertResult = videoAnalysisResultMapper.insert(newResult);
                if (insertResult > 0) {
                    log.info("保存分析结果成功，videoId：{}，taskId：{}，resultId：{}", 
                            videoId, taskId, newResult.getId());
                } else {
                    log.error("保存分析结果失败，videoId：{}，taskId：{}", videoId, taskId);
                }
            }
            
        } catch (Exception e) {
            log.error("保存分析结果到数据库时发生异常，videoId：{}，taskId：{}，错误：{}", 
                     videoId, taskId, e.getMessage(), e);
        }
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