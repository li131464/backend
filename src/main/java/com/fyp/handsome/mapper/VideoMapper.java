package com.fyp.handsome.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyp.handsome.entity.Video;

/**
 * 视频信息Mapper接口
 * @author fyp
 */
public interface VideoMapper extends BaseMapper<Video> {

    /**
     * 根据视频名称模糊查询视频列表
     * @param videoName 视频名称
     * @return 视频列表
     */
    List<Video> selectByVideoNameLike(@Param("videoName") String videoName);

    /**
     * 根据拍摄地点查询视频列表
     * @param shootingLocation 拍摄地点
     * @return 视频列表
     */
    @Select("SELECT * FROM video_info WHERE shooting_location = #{shootingLocation} ORDER BY create_time DESC")
    List<Video> selectByShootingLocation(@Param("shootingLocation") String shootingLocation);

    /**
     * 根据视频来源查询视频列表
     * @param videoSource 视频来源
     * @return 视频列表
     */
    @Select("SELECT * FROM video_info WHERE video_source = #{videoSource} ORDER BY create_time DESC")
    List<Video> selectByVideoSource(@Param("videoSource") String videoSource);

    /**
     * 根据时间范围查询视频列表 - 复杂查询，使用XML实现
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 视频列表
     */
    List<Video> selectByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询视频信息（带条件）- 复杂动态SQL，使用XML实现
     * @param page 分页参数
     * @param videoName 视频名称（可选）
     * @param shootingLocation 拍摄地点（可选）
     * @param videoSource 视频来源（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<Video> selectPageWithConditions(Page<Video> page,
                                          @Param("videoName") String videoName,
                                          @Param("shootingLocation") String shootingLocation,
                                          @Param("videoSource") String videoSource,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 统计视频总数
     * @return 视频总数
     */
    @Select("SELECT COUNT(*) FROM video_info")
    Long countTotal();

    /**
     * 统计各来源视频数量
     * @return 统计结果列表
     */
    @Select("SELECT video_source, COUNT(*) as count FROM video_info GROUP BY video_source")
    List<Object> countByVideoSource();
} 