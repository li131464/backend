package com.fyp.handsome.dto.video;

/**
 * 视频分析任务信息DTO
 * 用于返回视频分析任务的基本信息
 * @author ziye
 */
public class VideoAnalysisTaskInfo {
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