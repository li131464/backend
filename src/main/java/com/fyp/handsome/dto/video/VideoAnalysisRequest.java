package com.fyp.handsome.dto.video;

/**
 * 视频分析请求DTO
 * 用于接收客户端的视频分析请求参数
 * @author ziye
 */
public class VideoAnalysisRequest {
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