package com.fyp.handsome.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频分析结果实体类
 * 对应数据库表：video_analysis_result
 * @author fyp
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("video_analysis_result")
public class VideoAnalysisResult {

    /**
     * 分析结果编号（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 视频编号（外键）
     */
    @TableField("video_id")
    private Long videoId;

    /**
     * 分析类型：face_recognition-人脸识别，behavior_analysis-行为分析，event_detection-事件检测
     */
    @TableField("analysis_type")
    private String analysisType;

    /**
     * 分析结果（JSON格式存储）
     */
    @TableField("analysis_result")
    private String analysisResult;

    /**
     * 置信度分数（0-1）
     */
    @TableField("confidence_score")
    private BigDecimal confidenceScore;

    /**
     * 分析时间
     */
    @TableField("analysis_time")
    private LocalDateTime analysisTime;

    /**
     * 状态：1-正常，0-删除
     */
    @TableField("status")
    @TableLogic(value = "1", delval = "0")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 