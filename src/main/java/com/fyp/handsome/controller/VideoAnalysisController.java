package com.fyp.handsome.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyp.handsome.dto.Result;
import com.fyp.handsome.dto.ResultCode;
import com.fyp.handsome.entity.VideoAnalysisResult;
import com.fyp.handsome.service.VideoAnalysisService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 视频分析控制器
 * @author ziye
 */
@Slf4j
@RestController
@RequestMapping("/api/video-analysis")
@RequiredArgsConstructor
public class VideoAnalysisController {

    private final VideoAnalysisService videoAnalysisService;

    // =================== 视频分析操作 ===================

    /**
     * 执行视频内容分析
     */
    @PostMapping("/analyze/{videoId}")
    public Result<VideoAnalysisResult> analyzeVideoContent(
            @PathVariable Long videoId,
            @RequestParam String analysisType) {
        try {
            log.info("开始执行视频分析，videoId：{}，analysisType：{}", videoId, analysisType);
            
            VideoAnalysisResult result = videoAnalysisService.analyzeVideoContent(videoId, analysisType);
            if (result != null) {
                return Result.success("视频分析完成", result);
            } else {
                return Result.error(ResultCode.ANALYSIS_FAILED);
            }
            
        } catch (Exception e) {
            log.error("视频分析失败，videoId：{}，analysisType：{}，错误：{}", 
                     videoId, analysisType, e.getMessage(), e);
            return Result.error("视频分析失败：" + e.getMessage());
        }
    }

    /**
     * 执行行为分析
     */
    @PostMapping("/behavior/{videoId}")
    public Result<VideoAnalysisResult> analyzeBehavior(@PathVariable Long videoId) {
        try {
            log.info("开始执行行为分析，videoId：{}", videoId);
            
            VideoAnalysisResult result = videoAnalysisService.analyzeBehavior(videoId);
            if (result != null) {
                return Result.success("行为分析完成", result);
            } else {
                return Result.error(ResultCode.ANALYSIS_FAILED);
            }
            
        } catch (Exception e) {
            log.error("行为分析失败，videoId：{}，错误：{}", videoId, e.getMessage(), e);
            return Result.error("行为分析失败：" + e.getMessage());
        }
    }

    /**
     * 执行事件检测
     */
    @PostMapping("/event/{videoId}")
    public Result<VideoAnalysisResult> detectEvents(@PathVariable Long videoId) {
        try {
            log.info("开始执行事件检测，videoId：{}", videoId);
            
            VideoAnalysisResult result = videoAnalysisService.detectEvents(videoId);
            if (result != null) {
                return Result.success("事件检测完成", result);
            } else {
                return Result.error(ResultCode.ANALYSIS_FAILED);
            }
            
        } catch (Exception e) {
            log.error("事件检测失败，videoId：{}，错误：{}", videoId, e.getMessage(), e);
            return Result.error("事件检测失败：" + e.getMessage());
        }
    }

    /**
     * 批量分析视频
     */
    @PostMapping("/batch-analyze")
    public Result<List<VideoAnalysisResult>> batchAnalyze(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> videoIds = (List<Long>) params.get("videoIds");
            String analysisType = (String) params.get("analysisType");
            
            log.info("开始批量分析视频，videoIds：{}，analysisType：{}", videoIds, analysisType);
            
            List<VideoAnalysisResult> results = videoAnalysisService.batchAnalyze(videoIds, analysisType);
            return Result.success("批量分析完成", results);
            
        } catch (Exception e) {
            log.error("批量分析失败，错误：{}", e.getMessage(), e);
            return Result.error("批量分析失败：" + e.getMessage());
        }
    }

    // =================== 查询分析结果 ===================

    /**
     * 获取指定视频的分析结果
     */
    @GetMapping("/video/{videoId}")
    public Result<List<VideoAnalysisResult>> getAnalysisResultsByVideoId(@PathVariable Long videoId) {
        try {
            List<VideoAnalysisResult> results = videoAnalysisService.getAnalysisResultsByVideoId(videoId);
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("获取视频分析结果失败，videoId：{}，错误：{}", videoId, e.getMessage(), e);
            return Result.error("获取视频分析结果失败：" + e.getMessage());
        }
    }

    /**
     * 根据分析类型获取分析结果
     */
    @GetMapping("/type/{analysisType}")
    public Result<List<VideoAnalysisResult>> getAnalysisResultsByType(@PathVariable String analysisType) {
        try {
            List<VideoAnalysisResult> results = videoAnalysisService.getAnalysisResultsByType(analysisType);
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("根据分析类型获取结果失败，analysisType：{}，错误：{}", analysisType, e.getMessage(), e);
            return Result.error("根据分析类型获取结果失败：" + e.getMessage());
        }
    }

    /**
     * 根据视频ID和分析类型获取分析结果
     */
    @GetMapping("/video/{videoId}/type/{analysisType}")
    public Result<List<VideoAnalysisResult>> getAnalysisResults(
            @PathVariable Long videoId,
            @PathVariable String analysisType) {
        try {
            List<VideoAnalysisResult> results = videoAnalysisService.getAnalysisResults(videoId, analysisType);
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("获取指定视频和类型的分析结果失败，videoId：{}，analysisType：{}，错误：{}", 
                     videoId, analysisType, e.getMessage(), e);
            return Result.error("获取分析结果失败：" + e.getMessage());
        }
    }

    /**
     * 根据时间范围获取分析结果
     */
    @GetMapping("/time-range")
    public Result<List<VideoAnalysisResult>> getAnalysisResultsByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            List<VideoAnalysisResult> results = videoAnalysisService.getAnalysisResultsByTimeRange(startTime, endTime);
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("根据时间范围获取分析结果失败，startTime：{}，endTime：{}，错误：{}", 
                     startTime, endTime, e.getMessage(), e);
            return Result.error("根据时间范围获取分析结果失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询分析结果
     */
    @GetMapping("/page")
    public Result<IPage<VideoAnalysisResult>> getAnalysisResultsPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long videoId,
            @RequestParam(required = false) String analysisType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            Page<VideoAnalysisResult> page = new Page<>(current, size);
            IPage<VideoAnalysisResult> result = videoAnalysisService.getAnalysisResultsPage(
                    page, videoId, analysisType, startTime, endTime);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询分析结果失败，错误：{}", e.getMessage(), e);
            return Result.error("分页查询分析结果失败：" + e.getMessage());
        }
    }

    /**
     * 获取最新分析结果
     */
    @GetMapping("/latest")
    public Result<List<VideoAnalysisResult>> getLatestAnalysisResults(
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<VideoAnalysisResult> results = videoAnalysisService.getLatestAnalysisResults(limit);
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("获取最新分析结果失败，limit：{}，错误：{}", limit, e.getMessage(), e);
            return Result.error("获取最新分析结果失败：" + e.getMessage());
        }
    }

    // =================== 数据分析和统计 ===================

    /**
     * 根据分析类型统计数量
     */
    @GetMapping("/statistics/type")
    public Result<Map<String, Long>> getCountByAnalysisType() {
        try {
            Map<String, Long> countMap = videoAnalysisService.getCountByAnalysisType();
            return Result.success(countMap);
            
        } catch (Exception e) {
            log.error("根据分析类型统计数量失败，错误：{}", e.getMessage(), e);
            return Result.error("根据分析类型统计数量失败：" + e.getMessage());
        }
    }

    /**
     * 根据时间范围统计分析数量
     */
    @GetMapping("/statistics/time-range")
    public Result<Long> getCountByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            Long count = videoAnalysisService.getCountByTimeRange(startTime, endTime);
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("根据时间范围统计分析数量失败，startTime：{}，endTime：{}，错误：{}", 
                     startTime, endTime, e.getMessage(), e);
            return Result.error("根据时间范围统计分析数量失败：" + e.getMessage());
        }
    }

    /**
     * 获取分析统计概览
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getAnalysisStatistics() {
        try {
            Map<String, Object> statistics = videoAnalysisService.getAnalysisStatistics();
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取分析统计概览失败，错误：{}", e.getMessage(), e);
            return Result.error("获取分析统计概览失败：" + e.getMessage());
        }
    }

    /**
     * 获取人员流量统计
     */
    @GetMapping("/statistics/person-flow")
    public Result<Map<String, Object>> getPersonFlowStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            Map<String, Object> statistics = videoAnalysisService.getPersonFlowStatistics(startTime, endTime);
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取人员流量统计失败，startTime：{}，endTime：{}，错误：{}", 
                     startTime, endTime, e.getMessage(), e);
            return Result.error("获取人员流量统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取车辆流量统计
     */
    @GetMapping("/statistics/vehicle-flow")
    public Result<Map<String, Object>> getVehicleFlowStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            Map<String, Object> statistics = videoAnalysisService.getVehicleFlowStatistics(startTime, endTime);
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取车辆流量统计失败，startTime：{}，endTime：{}，错误：{}", 
                     startTime, endTime, e.getMessage(), e);
            return Result.error("获取车辆流量统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取异常事件统计
     */
    @GetMapping("/statistics/abnormal-events")
    public Result<Map<String, Object>> getAbnormalEventStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        try {
            Map<String, Object> statistics = videoAnalysisService.getAbnormalEventStatistics(startTime, endTime);
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取异常事件统计失败，startTime：{}，endTime：{}，错误：{}", 
                     startTime, endTime, e.getMessage(), e);
            return Result.error("获取异常事件统计失败：" + e.getMessage());
        }
    }

    /**
     * 生成分析报表
     */
    @PostMapping("/report")
    public Result<Map<String, Object>> generateAnalysisReport(@RequestBody Map<String, Object> params) {
        try {
            String reportType = (String) params.get("reportType");
            LocalDateTime startTime = LocalDateTime.parse((String) params.get("startTime"));
            LocalDateTime endTime = LocalDateTime.parse((String) params.get("endTime"));
            
            Map<String, Object> report = videoAnalysisService.generateAnalysisReport(reportType, startTime, endTime);
            return Result.success("报表生成成功", report);
            
        } catch (Exception e) {
            log.error("生成分析报表失败，错误：{}", e.getMessage(), e);
            return Result.error("生成分析报表失败：" + e.getMessage());
        }
    }

    // =================== 分析结果管理 ===================

    /**
     * 删除分析结果
     */
    @DeleteMapping("/{resultId}")
    public Result<Void> deleteAnalysisResult(@PathVariable Long resultId) {
        try {
            if (videoAnalysisService.deleteAnalysisResult(resultId)) {
                return Result.success();
            } else {
                return Result.error(ResultCode.ANALYSIS_NOT_FOUND);
            }
            
        } catch (Exception e) {
            log.error("删除分析结果失败，resultId：{}，错误：{}", resultId, e.getMessage(), e);
            return Result.error("删除分析结果失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除分析结果
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteAnalysisResults(@RequestBody List<Long> resultIds) {
        try {
            if (videoAnalysisService.deleteAnalysisResults(resultIds)) {
                return Result.success();
            } else {
                return Result.error("批量删除分析结果失败");
            }
            
        } catch (Exception e) {
            log.error("批量删除分析结果失败，resultIds：{}，错误：{}", resultIds, e.getMessage(), e);
            return Result.error("批量删除分析结果失败：" + e.getMessage());
        }
    }

    /**
     * 删除指定视频的所有分析结果
     */
    @DeleteMapping("/video/{videoId}")
    public Result<Void> deleteAnalysisResultsByVideoId(@PathVariable Long videoId) {
        try {
            if (videoAnalysisService.deleteAnalysisResultsByVideoId(videoId)) {
                return Result.success();
            } else {
                return Result.error("删除视频相关分析结果失败");
            }
            
        } catch (Exception e) {
            log.error("删除视频相关分析结果失败，videoId：{}，错误：{}", videoId, e.getMessage(), e);
            return Result.error("删除视频相关分析结果失败：" + e.getMessage());
        }
    }

    // =================== 算法配置 ===================

    /**
     * 获取支持的分析类型
     */
    @GetMapping("/supported-types")
    public Result<List<String>> getSupportedAnalysisTypes() {
        try {
            List<String> types = videoAnalysisService.getSupportedAnalysisTypes();
            return Result.success(types);
            
        } catch (Exception e) {
            log.error("获取支持的分析类型失败，错误：{}", e.getMessage(), e);
            return Result.error("获取支持的分析类型失败：" + e.getMessage());
        }
    }

    /**
     * 检查分析算法是否可用
     */
    @GetMapping("/algorithm/{analysisType}/available")
    public Result<Boolean> isAnalysisAlgorithmAvailable(@PathVariable String analysisType) {
        try {
            boolean available = videoAnalysisService.isAnalysisAlgorithmAvailable(analysisType);
            return Result.success(available);
            
        } catch (Exception e) {
            log.error("检查分析算法是否可用失败，analysisType：{}，错误：{}", analysisType, e.getMessage(), e);
            return Result.error("检查分析算法是否可用失败：" + e.getMessage());
        }
    }
} 