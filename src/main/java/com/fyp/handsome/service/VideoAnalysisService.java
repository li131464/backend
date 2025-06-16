package com.fyp.handsome.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fyp.handsome.entity.VideoAnalysisResult;

/**
 * 视频分析服务接口
 * 负责视频分析、统计逻辑
 * @author fyp
 */
public interface VideoAnalysisService extends IService<VideoAnalysisResult> {

    // =================== 视频分析操作 ===================

    /**
     * 执行视频内容分析（人脸识别、车牌识别、物体识别等）
     * @param videoId 视频ID
     * @param analysisType 分析类型
     * @return 分析结果
     */
    VideoAnalysisResult analyzeVideoContent(Long videoId, String analysisType);

    /**
     * 执行行为分析（奔跑、摔倒、徘徊等）
     * @param videoId 视频ID
     * @return 分析结果
     */
    VideoAnalysisResult analyzeBehavior(Long videoId);

    /**
     * 执行事件检测（火灾、盗窃、打架等）
     * @param videoId 视频ID
     * @return 分析结果
     */
    VideoAnalysisResult detectEvents(Long videoId);

    /**
     * 批量分析视频
     * @param videoIds 视频ID列表
     * @param analysisType 分析类型
     * @return 分析结果列表
     */
    List<VideoAnalysisResult> batchAnalyze(List<Long> videoIds, String analysisType);

    // =================== 查询分析结果 ===================

    /**
     * 根据视频ID查询分析结果
     * @param videoId 视频ID
     * @return 分析结果列表
     */
    List<VideoAnalysisResult> getAnalysisResultsByVideoId(Long videoId);

    /**
     * 根据分析类型查询结果
     * @param analysisType 分析类型
     * @return 分析结果列表
     */
    List<VideoAnalysisResult> getAnalysisResultsByType(String analysisType);

    /**
     * 根据视频ID和分析类型查询结果
     * @param videoId 视频ID
     * @param analysisType 分析类型
     * @return 分析结果列表
     */
    List<VideoAnalysisResult> getAnalysisResults(Long videoId, String analysisType);

    /**
     * 根据时间范围查询分析结果
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分析结果列表
     */
    List<VideoAnalysisResult> getAnalysisResultsByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 分页查询分析结果（带条件）
     * @param page 分页参数
     * @param videoId 视频ID（可选）
     * @param analysisType 分析类型（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     */
    IPage<VideoAnalysisResult> getAnalysisResultsPage(Page<VideoAnalysisResult> page, Long videoId, 
                                                       String analysisType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询最新的分析结果
     * @param limit 限制数量
     * @return 最新分析结果列表
     */
    List<VideoAnalysisResult> getLatestAnalysisResults(Integer limit);

    // =================== 数据分析和统计 ===================

    /**
     * 统计各分析类型数量
     * @return 统计结果
     */
    Map<String, Long> getCountByAnalysisType();

    /**
     * 统计指定时间范围内的分析数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分析数量
     */
    Long getCountByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取分析统计概览
     * @return 统计概览信息
     */
    Map<String, Object> getAnalysisStatistics();

    /**
     * 人员流量统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 人员流量统计结果
     */
    Map<String, Object> getPersonFlowStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 车辆流量统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 车辆流量统计结果
     */
    Map<String, Object> getVehicleFlowStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 异常事件统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 异常事件统计结果
     */
    Map<String, Object> getAbnormalEventStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生成分析报表
     * @param reportType 报表类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报表数据
     */
    Map<String, Object> generateAnalysisReport(String reportType, LocalDateTime startTime, LocalDateTime endTime);

    // =================== 分析结果管理 ===================

    /**
     * 删除分析结果
     * @param resultId 结果ID
     * @return 是否成功
     */
    boolean deleteAnalysisResult(Long resultId);

    /**
     * 批量删除分析结果
     * @param resultIds 结果ID列表
     * @return 是否成功
     */
    boolean deleteAnalysisResults(List<Long> resultIds);

    /**
     * 根据视频ID删除相关分析结果
     * @param videoId 视频ID
     * @return 是否成功
     */
    boolean deleteAnalysisResultsByVideoId(Long videoId);

    /**
     * 导出分析结果到Excel
     * @param resultIds 结果ID列表（可选）
     * @return Excel文件字节数组
     */
    byte[] exportAnalysisResultsToExcel(List<Long> resultIds);

    // =================== 算法配置 ===================

    /**
     * 获取支持的分析类型列表
     * @return 分析类型列表
     */
    List<String> getSupportedAnalysisTypes();

    /**
     * 检查分析算法状态
     * @param analysisType 分析类型
     * @return 算法是否可用
     */
    boolean isAnalysisAlgorithmAvailable(String analysisType);
} 