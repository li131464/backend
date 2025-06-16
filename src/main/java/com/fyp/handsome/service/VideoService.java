package com.fyp.handsome.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fyp.handsome.entity.Video;

/**
 * 视频服务接口
 * 负责视频CRUD、备份恢复逻辑
 * @author fyp
 */
public interface VideoService extends IService<Video> {

    // =================== 基础CRUD操作 ===================

    /**
     * 新增视频信息
     * @param video 视频信息
     * @return 是否成功
     */
    boolean addVideo(Video video);

    /**
     * 根据ID删除视频信息（逻辑删除）
     * @param videoId 视频ID
     * @return 是否成功
     */
    boolean deleteVideo(Long videoId);

    /**
     * 批量删除视频信息
     * @param videoIds 视频ID列表
     * @return 是否成功
     */
    boolean deleteVideos(List<Long> videoIds);

    /**
     * 更新视频信息
     * @param video 视频信息
     * @return 是否成功
     */
    boolean updateVideo(Video video);

    /**
     * 根据ID查询视频信息
     * @param videoId 视频ID
     * @return 视频信息
     */
    Video getVideoById(Long videoId);

    // =================== 查询操作 ===================

    /**
     * 根据视频名称模糊查询
     * @param videoName 视频名称
     * @return 视频列表
     */
    List<Video> getVideosByName(String videoName);

    /**
     * 根据拍摄地点查询
     * @param shootingLocation 拍摄地点
     * @return 视频列表
     */
    List<Video> getVideosByLocation(String shootingLocation);

    /**
     * 根据视频来源查询
     * @param videoSource 视频来源
     * @return 视频列表
     */
    List<Video> getVideosBySource(String videoSource);

    /**
     * 根据时间范围查询
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 视频列表
     */
    List<Video> getVideosByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询视频信息（带条件）
     * @param page 分页参数
     * @param videoName 视频名称（可选）
     * @param shootingLocation 拍摄地点（可选）
     * @param videoSource 视频来源（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<Video> getVideosPage(Page<Video> page, String videoName, String shootingLocation, 
                               String videoSource, LocalDateTime startTime, LocalDateTime endTime);

    // =================== 统计操作 ===================

    /**
     * 统计视频总数
     * @return 视频总数
     */
    Long getTotalCount();

    /**
     * 统计各来源视频数量
     * @return 统计结果
     */
    Map<String, Long> getCountBySource();

    /**
     * 统计各地点视频数量
     * @return 统计结果
     */
    Map<String, Long> getCountByLocation();

    /**
     * 获取视频统计概览
     * @return 统计概览信息
     */
    Map<String, Object> getVideoStatistics();

    // =================== 备份恢复操作 ===================

    /**
     * 备份视频数据
     * @param backupPath 备份路径
     * @return 是否成功
     */
    boolean backupVideoData(String backupPath);

    /**
     * 恢复视频数据
     * @param backupPath 备份文件路径
     * @return 是否成功
     */
    boolean restoreVideoData(String backupPath);

    /**
     * 导出视频信息到Excel
     * @param videoIds 视频ID列表（可选，为空则导出全部）
     * @return Excel文件字节数组
     */
    byte[] exportToExcel(List<Long> videoIds);

    /**
     * 从Excel导入视频信息
     * @param excelBytes Excel文件字节数组
     * @return 导入结果
     */
    Map<String, Object> importFromExcel(byte[] excelBytes);

    // =================== 批量操作 ===================

    /**
     * 批量新增视频信息
     * @param videos 视频列表
     * @return 是否成功
     */
    boolean addVideoBatch(List<Video> videos);

    /**
     * 批量更新视频信息
     * @param videos 视频列表
     * @return 是否成功
     */
    boolean updateVideoBatch(List<Video> videos);
} 