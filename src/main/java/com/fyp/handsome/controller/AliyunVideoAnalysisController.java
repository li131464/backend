package com.fyp.handsome.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aliyun.sdk.service.quanmiaolightapp20240801.models.GetVideoAnalysisTaskResponse;
import com.aliyun.sdk.service.quanmiaolightapp20240801.models.SubmitVideoAnalysisTaskResponse;
import com.fyp.handsome.dto.Result;
import com.fyp.handsome.dto.video.TaskQueryRequest;
import com.fyp.handsome.dto.video.VideoAnalysisRequest;
import com.fyp.handsome.dto.video.VideoAnalysisTaskInfo;
import com.fyp.handsome.service.impl.analysis.AliyunVideoAnalysisService;

import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云视频分析控制器
 * 提供阿里云全貌轻应用视频分析相关API接口
 * @author ziye
 */
@Slf4j
@RestController
@RequestMapping("/api/aliyun/video-analysis")
public class AliyunVideoAnalysisController {

    @Autowired
    private AliyunVideoAnalysisService aliyunVideoAnalysisService;
    
    /**
     * 提交视频分析任务（异步）
     * @param request 视频分析请求
     * @return Result<VideoAnalysisTaskInfo> 任务提交结果，包含taskId
     */
    @PostMapping("/submit-async")
    public Result<VideoAnalysisTaskInfo> submitVideoAnalysisTaskAsync(@RequestBody VideoAnalysisRequest request) {
        try {
            log.info("收到异步视频分析请求，videoUrl：{}", request.getVideoUrl());
            
            // 验证请求参数
            if (request.getVideoUrl() == null || request.getVideoUrl().trim().isEmpty()) {
                return Result.error("视频URL不能为空");
            }
            
            // 同步提交分析任务以获取taskId
            SubmitVideoAnalysisTaskResponse response;
            if (request.getCustomPromptTemplate() != null && !request.getCustomPromptTemplate().trim().isEmpty()) {
                response = aliyunVideoAnalysisService.submitVideoAnalysisTask(request.getVideoUrl().trim(), request.getCustomPromptTemplate().trim());
            } else {
                response = aliyunVideoAnalysisService.submitVideoAnalysisTask(request.getVideoUrl().trim());
            }
            
            String taskId = response.getBody().getData().getTaskId();
            log.info("异步视频分析任务提交成功，taskId：{}", taskId);
            
            // 创建返回的任务信息
            VideoAnalysisTaskInfo taskInfo = new VideoAnalysisTaskInfo();
            taskInfo.setTaskId(taskId);
            taskInfo.setVideoUrl(request.getVideoUrl().trim());
            taskInfo.setStatus("submitted");
            taskInfo.setMessage("异步视频分析任务已提交成功");
            taskInfo.setSubmitTime(System.currentTimeMillis());
            
            return Result.success("异步视频分析任务提交成功", taskInfo);
            
        } catch (Exception e) {
            log.error("提交异步视频分析任务失败，错误：{}", e.getMessage(), e);
            return Result.error("提交异步视频分析任务失败：" + e.getMessage());
        }
    }

    /**
     * 获取视频分析结果（POST方式，简化版）
     * @param request 查询请求
     * @return Result<Object> 简化的分析结果
     */
    @PostMapping("/result/query")
    public Result<Object> queryVideoAnalysisResult(@RequestBody TaskQueryRequest request) {
        try {
            log.info("收到获取视频分析结果请求，taskId：{}", request.getTaskId());
            
            // 验证请求参数
            if (request.getTaskId() == null || request.getTaskId().trim().isEmpty()) {
                return Result.error("任务ID不能为空");
            }
            
            // 查询任务结果
            GetVideoAnalysisTaskResponse response = aliyunVideoAnalysisService.getVideoAnalysisTask(request.getTaskId().trim());
            String taskStatus = response.getBody().getData().getTaskStatus();
            
            log.info("查询视频分析任务状态，taskId：{}，状态：{}", request.getTaskId(), taskStatus);
            
            // 根据任务状态返回不同结果
            if ("RUNNING".equals(taskStatus)) {
                return Result.success("视频分析还在处理中", "视频分析还在处理中");
            } else if ("SUCCESSED".equals(taskStatus)) {
                // 调用service层的方法提取核心分析结果
                Object analysisResult = aliyunVideoAnalysisService.extractAnalysisResult(response);
                if (analysisResult != null) {
                    return Result.success("视频分析完成", analysisResult);
                } else {
                    return Result.error("分析结果为空");
                }
            } else {
                return Result.error("任务状态异常：" + taskStatus);
            }
            
        } catch (Exception e) {
            log.error("获取视频分析结果失败，taskId：{}，错误：{}", request.getTaskId(), e.getMessage(), e);
            return Result.error("获取视频分析结果失败：" + e.getMessage());
        }
    }
} 