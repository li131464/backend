package com.fyp.handsome.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aliyun.sdk.service.quanmiaolightapp20240801.models.SubmitVideoAnalysisTaskResponse;
import com.fyp.handsome.dto.Result;
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
    @GetMapping("/config")
    public Result<Map<String, String>> getConfig() {
        try {
            Map<String, String> config = aliyunVideoAnalysisService.getConfigInfo();
            return Result.success("获取配置信息成功", config);
        } catch (Exception e) {
            log.error("获取配置信息失败，错误：{}", e.getMessage(), e);
            return Result.error("获取配置信息失败：" + e.getMessage());
        }
    }

    /**
     * 视频分析请求DTO
     */
    public static class VideoAnalysisRequest {
        /**
         * 视频URL地址
         */
        private String videoUrl;
        
        /**
         * 自定义分析提示模板（可选）
         */
        private String customPromptTemplate;

        // Getter和Setter方法
        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getCustomPromptTemplate() {
            return customPromptTemplate;
        }

        public void setCustomPromptTemplate(String customPromptTemplate) {
            this.customPromptTemplate = customPromptTemplate;
        }

        @Override
        public String toString() {
            return "VideoAnalysisRequest{" +
                    "videoUrl='" + videoUrl + '\'' +
                    ", customPromptTemplate='" + (customPromptTemplate != null ? "已设置" : "未设置") + '\'' +
                    '}';
        }
    }

    /**
     * 视频分析任务信息DTO
     */
    public static class VideoAnalysisTaskInfo {
        /**
         * 任务ID
         */
        private String taskId;
        
        /**
         * 视频URL
         */
        private String videoUrl;
        
        /**
         * 任务状态
         */
        private String status;
        
        /**
         * 提示消息
         */
        private String message;
        
        /**
         * 提交时间（时间戳）
         */
        private Long submitTime;

        // Getter和Setter方法
        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public void setVideoUrl(String videoUrl) {
            this.videoUrl = videoUrl;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Long getSubmitTime() {
            return submitTime;
        }

        public void setSubmitTime(Long submitTime) {
            this.submitTime = submitTime;
        }

        @Override
        public String toString() {
            return "VideoAnalysisTaskInfo{" +
                    "taskId='" + taskId + '\'' +
                    ", videoUrl='" + videoUrl + '\'' +
                    ", status='" + status + '\'' +
                    ", submitTime=" + submitTime +
                    '}';
        }
    }
} 