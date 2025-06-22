package com.fyp.handsome.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fyp.handsome.dto.Result;
import com.fyp.handsome.service.VisualizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 可视化展示控制器
 * @author ziye
 */
@Slf4j
@RestController
@RequestMapping("/api/visualization")
@RequiredArgsConstructor
public class VisualizationController {

    private final VisualizationService visualizationService;

    // =================== 地图展示 ===================

    /**
     * 获取监控点地图数据
     */
    @GetMapping("/map/monitor-points")
    public Result<Map<String, Object>> getMonitorPointMapData() {
        try {
            Map<String, Object> mapData = visualizationService.getMonitorPointMapData();
            return Result.success(mapData);
            
        } catch (Exception e) {
            log.error("获取监控点地图数据失败，错误：{}", e.getMessage(), e);
            return Result.error("获取监控点地图数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定区域的监控点
     */
    @GetMapping("/map/monitor-points/area")
    public Result<List<Map<String, Object>>> getMonitorPointsByArea(
            @RequestParam Double minLatitude,
            @RequestParam Double maxLatitude,
            @RequestParam Double minLongitude,
            @RequestParam Double maxLongitude) {
        try {
            List<Map<String, Object>> points = visualizationService.getMonitorPointsByArea(
                    minLatitude, maxLatitude, minLongitude, maxLongitude);
            return Result.success(points);
            
        } catch (Exception e) {
            log.error("获取指定区域监控点失败，错误：{}", e.getMessage(), e);
            return Result.error("获取指定区域监控点失败：" + e.getMessage());
        }
    }

    /**
     * 获取监控点状态统计
     */
    @GetMapping("/map/monitor-points/statistics")
    public Result<Map<String, Object>> getMonitorPointStatusStatistics() {
        try {
            Map<String, Object> statistics = visualizationService.getMonitorPointStatusStatistics();
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取监控点状态统计失败，错误：{}", e.getMessage(), e);
            return Result.error("获取监控点状态统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取视频热力图数据
     */
    @GetMapping("/map/heatmap")
    public Result<Map<String, Object>> getVideoHeatmapData(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            Map<String, Object> heatmapData = visualizationService.getVideoHeatmapData(startTime, endTime);
            return Result.success(heatmapData);
            
        } catch (Exception e) {
            log.error("获取视频热力图数据失败，错误：{}", e.getMessage(), e);
            return Result.error("获取视频热力图数据失败：" + e.getMessage());
        }
    }

    // =================== 报表生成 ===================

    /**
     * 生成视频分析报表
     */
    @PostMapping("/report/video-analysis")
    public Result<Map<String, Object>> generateVideoAnalysisReport(@RequestBody Map<String, Object> params) {
        try {
            String reportType = (String) params.get("reportType");
            LocalDateTime startTime = LocalDateTime.parse((String) params.get("startTime"));
            LocalDateTime endTime = LocalDateTime.parse((String) params.get("endTime"));
            
            Map<String, Object> report = visualizationService.generateVideoAnalysisReport(
                    reportType, startTime, endTime);
            return Result.success("视频分析报表生成成功", report);
            
        } catch (Exception e) {
            log.error("生成视频分析报表失败，错误：{}", e.getMessage(), e);
            return Result.error("生成视频分析报表失败：" + e.getMessage());
        }
    }

    // =================== 实时预览 ===================

    /**
     * 获取实时监控视频列表
     */
    @GetMapping("/live/videos")
    public Result<List<Map<String, Object>>> getLiveVideoList() {
        try {
            List<Map<String, Object>> videos = visualizationService.getLiveVideoList();
            return Result.success(videos);
            
        } catch (Exception e) {
            log.error("获取实时监控视频列表失败，错误：{}", e.getMessage(), e);
            return Result.error("获取实时监控视频列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定监控点的实时视频
     */
    @GetMapping("/live/video/{monitorPointId}")
    public Result<Map<String, Object>> getLiveVideoByMonitorPoint(@PathVariable Long monitorPointId) {
        try {
            Map<String, Object> video = visualizationService.getLiveVideoByMonitorPoint(monitorPointId);
            return Result.success(video);
            
        } catch (Exception e) {
            log.error("获取指定监控点实时视频失败，monitorPointId：{}，错误：{}", 
                     monitorPointId, e.getMessage(), e);
            return Result.error("获取指定监控点实时视频失败：" + e.getMessage());
        }
    }

    /**
     * 获取实时事件告警
     */
    @GetMapping("/live/alerts")
    public Result<List<Map<String, Object>>> getRealTimeAlerts(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<Map<String, Object>> alerts = visualizationService.getRealTimeAlerts(limit);
            return Result.success(alerts);
            
        } catch (Exception e) {
            log.error("获取实时事件告警失败，limit：{}，错误：{}", limit, e.getMessage(), e);
            return Result.error("获取实时事件告警失败：" + e.getMessage());
        }
    }

    // =================== 仪表板数据 ===================

    /**
     * 获取仪表板概览数据
     */
    @GetMapping("/dashboard/overview")
    public Result<Map<String, Object>> getDashboardOverview() {
        try {
            Map<String, Object> overview = visualizationService.getDashboardOverview();
            return Result.success(overview);
            
        } catch (Exception e) {
            log.error("获取仪表板概览数据失败，错误：{}", e.getMessage(), e);
            return Result.error("获取仪表板概览数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取视频统计图表数据
     */
    @GetMapping("/dashboard/chart/video-statistics")
    public Result<Map<String, Object>> getVideoStatisticsChartData(
            @RequestParam String chartType,
            @RequestParam String timeRange) {
        try {
            Map<String, Object> chartData = visualizationService.getVideoStatisticsChartData(chartType, timeRange);
            return Result.success(chartData);
            
        } catch (Exception e) {
            log.error("获取视频统计图表数据失败，chartType：{}，timeRange：{}，错误：{}", 
                     chartType, timeRange, e.getMessage(), e);
            return Result.error("获取视频统计图表数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取分析结果统计图表数据
     */
    @GetMapping("/dashboard/chart/analysis-statistics")
    public Result<Map<String, Object>> getAnalysisStatisticsChartData(
            @RequestParam String chartType,
            @RequestParam String timeRange) {
        try {
            Map<String, Object> chartData = visualizationService.getAnalysisStatisticsChartData(chartType, timeRange);
            return Result.success(chartData);
            
        } catch (Exception e) {
            log.error("获取分析结果统计图表数据失败，chartType：{}，timeRange：{}，错误：{}", 
                     chartType, timeRange, e.getMessage(), e);
            return Result.error("获取分析结果统计图表数据失败：" + e.getMessage());
        }
    }

    // =================== 配置管理 ===================

    /**
     * 获取可视化配置
     */
    @GetMapping("/config/{configType}")
    public Result<Map<String, Object>> getVisualizationConfig(@PathVariable String configType) {
        try {
            Map<String, Object> config = visualizationService.getVisualizationConfig(configType);
            return Result.success(config);
            
        } catch (Exception e) {
            log.error("获取可视化配置失败，configType：{}，错误：{}", configType, e.getMessage(), e);
            return Result.error("获取可视化配置失败：" + e.getMessage());
        }
    }

    /**
     * 更新可视化配置
     */
    @PutMapping("/config/{configType}")
    public Result<Void> updateVisualizationConfig(
            @PathVariable String configType,
            @RequestBody Map<String, Object> configData) {
        try {
            if (visualizationService.updateVisualizationConfig(configType, configData)) {
                return Result.success();
            } else {
                return Result.error("可视化配置更新失败");
            }
            
        } catch (Exception e) {
            log.error("更新可视化配置失败，configType：{}，错误：{}", configType, e.getMessage(), e);
            return Result.error("更新可视化配置失败：" + e.getMessage());
        }
    }

    /**
     * 获取支持的报表类型
     */
    @GetMapping("/supported-report-types")
    public Result<List<String>> getSupportedReportTypes() {
        try {
            List<String> types = visualizationService.getSupportedReportTypes();
            return Result.success(types);
            
        } catch (Exception e) {
            log.error("获取支持的报表类型失败，错误：{}", e.getMessage(), e);
            return Result.error("获取支持的报表类型失败：" + e.getMessage());
        }
    }

    /**
     * 获取支持的图表类型
     */
    @GetMapping("/supported-chart-types")
    public Result<List<String>> getSupportedChartTypes() {
        try {
            List<String> types = visualizationService.getSupportedChartTypes();
            return Result.success(types);
            
        } catch (Exception e) {
            log.error("获取支持的图表类型失败，错误：{}", e.getMessage(), e);
            return Result.error("获取支持的图表类型失败：" + e.getMessage());
        }
    }
} 