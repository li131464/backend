package com.fyp.handsome.dto.video;

/**
 * 任务查询请求DTO
 * 用于查询视频分析任务状态和结果
 * @author ziye
 */
public class TaskQueryRequest {
    /**
     * 任务ID
     */
    private String taskId;

    // Getter和Setter方法
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "TaskQueryRequest{" +
                "taskId='" + taskId + '\'' +
                '}';
    }
} 