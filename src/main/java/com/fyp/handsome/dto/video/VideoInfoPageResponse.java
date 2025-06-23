package com.fyp.handsome.dto.video;

import java.util.List;

import lombok.Data;

/**
 * 视频信息分页响应DTO
 * 用于返回分页查询结果
 * @author ziye
 */
@Data
public class VideoInfoPageResponse {

    /**
     * 当前页数据列表
     */
    private List<VideoInfoResponse> content;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 是否为首页
     */
    private Boolean isFirst;

    /**
     * 是否为末页
     */
    private Boolean isLast;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;
} 