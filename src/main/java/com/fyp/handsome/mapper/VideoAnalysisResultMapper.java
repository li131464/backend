package com.fyp.handsome.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyp.handsome.entity.VideoAnalysisResult;

/**
 * 视频分析结果Mapper接口
 * @author ziye
 */
public interface VideoAnalysisResultMapper extends BaseMapper<VideoAnalysisResult> {

    /**
     * 根据视频ID查询分析结果列表
     * @param videoId 视频ID
     * @return 分析结果列表
     */
    @Select("SELECT * FROM video_analysis_result WHERE video_id = #{videoId} ORDER BY analysis_time DESC")
    List<VideoAnalysisResult> selectByVideoId(@Param("videoId") Long videoId);

    /**
     * 根据分析类型查询分析结果列表
     * @param analysisType 分析类型
     * @return 分析结果列表
     */
    @Select("SELECT * FROM video_analysis_result WHERE analysis_type = #{analysisType} ORDER BY analysis_time DESC")
    List<VideoAnalysisResult> selectByAnalysisType(@Param("analysisType") String analysisType);

    /**
     * 根据视频ID和分析类型查询分析结果
     * @param videoId 视频ID
     * @param analysisType 分析类型
     * @return 分析结果列表
     */
    @Select("SELECT * FROM video_analysis_result WHERE video_id = #{videoId} AND analysis_type = #{analysisType} ORDER BY analysis_time DESC")
    List<VideoAnalysisResult> selectByVideoIdAndType(@Param("videoId") Long videoId, 
                                                      @Param("analysisType") String analysisType);

    /**
     * 根据时间范围查询分析结果 - 复杂查询，使用XML实现
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分析结果列表
     */
    List<VideoAnalysisResult> selectByAnalysisTimeRange(@Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 分页查询分析结果（带条件）- 复杂动态SQL，使用XML实现
     * @param page 分页参数
     * @param videoId 视频ID（可选）
     * @param analysisType 分析类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<VideoAnalysisResult> selectPageWithConditions(Page<VideoAnalysisResult> page,
                                                         @Param("videoId") Long videoId,
                                                         @Param("analysisType") String analysisType,
                                                         @Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各分析类型数量
     * @return 统计结果列表
     */
    @Select("SELECT analysis_type, COUNT(*) as count FROM video_analysis_result GROUP BY analysis_type")
    List<Object> countByAnalysisType();

    /**
     * 统计指定时间范围内的分析数量 - 复杂查询，使用XML实现
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分析数量
     */
    Long countByTimeRange(@Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    /**
     * 查询最新的分析结果
     * @param limit 限制数量
     * @return 最新分析结果列表
     */
    @Select("SELECT * FROM video_analysis_result ORDER BY analysis_time DESC LIMIT #{limit}")
    List<VideoAnalysisResult> selectLatestResults(@Param("limit") Integer limit);
} 