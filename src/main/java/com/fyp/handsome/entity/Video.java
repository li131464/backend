package com.fyp.handsome.entity;

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
 * 视频信息实体类
 * 对应数据库表：video_info
 * @author ziye
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("video_info")
public class Video {

    /**
     * 视频编号（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 视频名称
     */
    @TableField("video_name")
    private String videoName;

    /**
     * 拍摄时间
     */
    @TableField("shooting_time")
    private LocalDateTime shootingTime;

    /**
     * 拍摄地点
     */
    @TableField("shooting_location")
    private String shootingLocation;

    /**
     * 视频来源
     */
    @TableField("video_source")
    private String videoSource;

    /**
     * 视频文件路径
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * 视频时长（秒）
     */
    @TableField("duration")
    private Integer duration;

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