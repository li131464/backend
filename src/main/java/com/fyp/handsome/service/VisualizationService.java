package com.fyp.handsome.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 可视化服务接口
 * 负责地图展示、报表生成逻辑
 * @author fyp
 */
public interface VisualizationService {

    // =================== 地图展示 ===================

    /**
     * 获取监控点地图数据
     * @return 监控点地图数据
     */
    Map<String, Object> getMonitorPointMapData();

    /**
     * 获取指定区域的监控点
     * @param minLatitude 最小纬度
     * @param maxLatitude 最大纬度
     * @param minLongitude 最小经度
     * @param maxLongitude 最大经度
     * @return 监控点列表
     */
    List<Map<String, Object>> getMonitorPointsByArea(Double minLatitude, Double maxLatitude, 
                                                      Double minLongitude, Double maxLongitude);

    /**
     * 获取监控点状态统计
     * @return 状态统计数据
     */
    Map<String, Object> getMonitorPointStatusStatistics();

    /**
     * 获取视频热力图数据
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 热力图数据
     */
    Map<String, Object> getVideoHeatmapData(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取事件分布地图数据
     * @param eventType 事件类型（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件分布数据
     */
    Map<String, Object> getEventDistributionMapData(String eventType, LocalDateTime startTime, LocalDateTime endTime);

    // =================== 报表生成 ===================

    /**
     * 生成视频分析报表
     * @param reportType 报表类型（daily、weekly、monthly）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报表数据
     */
    Map<String, Object> generateVideoAnalysisReport(String reportType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生成事件统计报表
     * @param reportType 报表类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报表数据
     */
    Map<String, Object> generateEventStatisticsReport(String reportType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生成人员流量报表
     * @param reportType 报表类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报表数据
     */
    Map<String, Object> generatePersonFlowReport(String reportType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生成车辆流量报表
     * @param reportType 报表类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报表数据
     */
    Map<String, Object> generateVehicleFlowReport(String reportType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 生成系统使用情况报表
     * @param reportType 报表类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 报表数据
     */
    Map<String, Object> generateSystemUsageReport(String reportType, LocalDateTime startTime, LocalDateTime endTime);

    // =================== 实时预览 ===================

    /**
     * 获取实时监控视频列表
     * @return 实时视频列表
     */
    List<Map<String, Object>> getLiveVideoList();

    /**
     * 获取指定监控点的实时视频信息
     * @param monitorPointId 监控点ID
     * @return 实时视频信息
     */
    Map<String, Object> getLiveVideoByMonitorPoint(Long monitorPointId);

    /**
     * 获取实时事件告警
     * @param limit 限制数量
     * @return 实时事件列表
     */
    List<Map<String, Object>> getRealTimeAlerts(Integer limit);

    // =================== 仪表板数据 ===================

    /**
     * 获取仪表板概览数据
     * @return 概览数据
     */
    Map<String, Object> getDashboardOverview();

    /**
     * 获取视频统计图表数据
     * @param chartType 图表类型（line、bar、pie等）
     * @param timeRange 时间范围
     * @return 图表数据
     */
    Map<String, Object> getVideoStatisticsChartData(String chartType, String timeRange);

    /**
     * 获取分析结果统计图表数据
     * @param chartType 图表类型
     * @param timeRange 时间范围
     * @return 图表数据
     */
    Map<String, Object> getAnalysisStatisticsChartData(String chartType, String timeRange);

    /**
     * 获取用户活动统计图表数据
     * @param chartType 图表类型
     * @param timeRange 时间范围
     * @return 图表数据
     */
    Map<String, Object> getUserActivityChartData(String chartType, String timeRange);

    // =================== 数据导出 ===================

    /**
     * 导出报表为PDF
     * @param reportType 报表类型
     * @param reportData 报表数据
     * @return PDF文件字节数组
     */
    byte[] exportReportToPdf(String reportType, Map<String, Object> reportData);

    /**
     * 导出报表为Excel
     * @param reportType 报表类型
     * @param reportData 报表数据
     * @return Excel文件字节数组
     */
    byte[] exportReportToExcel(String reportType, Map<String, Object> reportData);

    /**
     * 导出图表为图片
     * @param chartType 图表类型
     * @param chartData 图表数据
     * @param format 图片格式（png、jpg等）
     * @return 图片文件字节数组
     */
    byte[] exportChartToImage(String chartType, Map<String, Object> chartData, String format);

    // =================== 数据分析 ===================

    /**
     * 获取趋势分析数据
     * @param dataType 数据类型（video、analysis、event等）
     * @param timeRange 时间范围
     * @return 趋势分析数据
     */
    Map<String, Object> getTrendAnalysisData(String dataType, String timeRange);

    /**
     * 获取对比分析数据
     * @param dataType 数据类型
     * @param compareType 对比类型（period、location等）
     * @param timeRange 时间范围
     * @return 对比分析数据
     */
    Map<String, Object> getComparisonAnalysisData(String dataType, String compareType, String timeRange);

    /**
     * 获取异常检测数据
     * @param dataType 数据类型
     * @param timeRange 时间范围
     * @return 异常检测数据
     */
    Map<String, Object> getAnomalyDetectionData(String dataType, String timeRange);

    // =================== 配置管理 ===================

    /**
     * 获取可视化配置
     * @param configType 配置类型
     * @return 配置信息
     */
    Map<String, Object> getVisualizationConfig(String configType);

    /**
     * 更新可视化配置
     * @param configType 配置类型
     * @param configData 配置数据
     * @return 是否成功
     */
    boolean updateVisualizationConfig(String configType, Map<String, Object> configData);

    /**
     * 获取支持的报表类型
     * @return 报表类型列表
     */
    List<String> getSupportedReportTypes();

    /**
     * 获取支持的图表类型
     * @return 图表类型列表
     */
    List<String> getSupportedChartTypes();
} 