package com.fyp.handsome.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fyp.handsome.dto.Result;
import com.fyp.handsome.service.OssUploadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OSS文件上传控制器
 * 负责处理文件上传到阿里云OSS的HTTP请求
 *
 * @author ziye
 */
@RestController
@RequestMapping("/api/oss")
@RequiredArgsConstructor
@Slf4j
public class OssUploadController {

    private final OssUploadService ossUploadService; // OSS上传服务

    /**
     * 上传视频文件到OSS
     * 专门用于视频文件上传，会自动验证文件格式并存储到ob/文件夹
     *
     * @param videoFile 视频文件（支持.mp4等格式）
     * @return 上传结果，包含文件访问URL
     */
    @PostMapping("/upload/video")
    public Result<Map<String, String>> uploadVideo(@RequestParam("videoFile") MultipartFile videoFile) {
        try {
            log.info("接收到视频上传请求，文件名: {}, 文件大小: {} bytes", 
                    videoFile.getOriginalFilename(), videoFile.getSize());
            
            // 调用服务层上传视频
            String fileUrl = ossUploadService.uploadVideo(videoFile);
            
            // 构建返回结果
            Map<String, String> resultData = new HashMap<>();
            resultData.put("fileUrl", fileUrl);
            resultData.put("originalFilename", videoFile.getOriginalFilename());
            resultData.put("fileSize", String.valueOf(videoFile.getSize()));
            
            log.info("视频上传成功，URL: {}", fileUrl);
            return Result.success("视频上传成功", resultData);
            
        } catch (IllegalArgumentException e) {
            log.warn("视频上传参数错误: {}", e.getMessage());
            return Result.error("上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("视频上传失败: {}", e.getMessage(), e);
            return Result.error("视频上传失败，请稍后重试");
        }
    }

    /**
     * 通用文件上传到OSS
     * 可以指定文件夹路径，支持各种文件类型
     *
     * @param file 要上传的文件
     * @param folder 文件夹路径（可选，默认为根目录）
     * @return 上传结果，包含文件访问URL
     */
    @PostMapping("/upload/file")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", required = false, defaultValue = "") String folder) {
        try {
            log.info("接收到文件上传请求，文件名: {}, 文件夹: {}, 文件大小: {} bytes", 
                    file.getOriginalFilename(), folder, file.getSize());
            
            // 调用服务层上传文件
            String fileUrl = ossUploadService.uploadFile(file, folder);
            
            // 构建返回结果
            Map<String, String> resultData = new HashMap<>();
            resultData.put("fileUrl", fileUrl);
            resultData.put("originalFilename", file.getOriginalFilename());
            resultData.put("folder", folder);
            resultData.put("fileSize", String.valueOf(file.getSize()));
            
            log.info("文件上传成功，URL: {}", fileUrl);
            return Result.success("文件上传成功", resultData);
            
        } catch (IllegalArgumentException e) {
            log.warn("文件上传参数错误: {}", e.getMessage());
            return Result.error("上传失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return Result.error("文件上传失败，请稍后重试");
        }
    }

    /**
     * 删除OSS中的文件
     * 根据文件URL删除对应的文件
     *
     * @param request 删除请求，包含文件URL
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Map<String, Object>> deleteFile(@RequestBody FileDeleteRequest request) {
        try {
            log.info("接收到文件删除请求，URL: {}", request.getFileUrl());
            
            // 调用服务层删除文件
            boolean success = ossUploadService.deleteFile(request.getFileUrl());
            
            // 构建返回结果
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("deleted", success);
            resultData.put("fileUrl", request.getFileUrl());
            
            if (success) {
                log.info("文件删除成功，URL: {}", request.getFileUrl());
                return Result.success("文件删除成功", resultData);
            } else {
                log.warn("文件删除失败，URL: {}", request.getFileUrl());
                return Result.error("文件删除失败");
            }
            
        } catch (Exception e) {
            log.error("文件删除失败: {}", e.getMessage(), e);
            return Result.error("文件删除失败，请稍后重试");
        }
    }

    /**
     * 检查文件是否存在
     * 根据文件URL检查文件是否存在于OSS中
     *
     * @param fileUrl 文件URL
     * @return 检查结果
     */
    @GetMapping("/exists")
    public Result<Map<String, Object>> fileExists(@RequestParam("fileUrl") String fileUrl) {
        try {
            log.info("接收到文件存在性检查请求，URL: {}", fileUrl);
            
            // 调用服务层检查文件是否存在
            boolean exists = ossUploadService.fileExists(fileUrl);
            
            // 构建返回结果
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("exists", exists);
            resultData.put("fileUrl", fileUrl);
            
            log.info("文件存在性检查完成，URL: {}, 存在: {}", fileUrl, exists);
            return Result.success("检查完成", resultData);
            
        } catch (Exception e) {
            log.error("文件存在性检查失败: {}", e.getMessage(), e);
            return Result.error("检查失败，请稍后重试");
        }
    }

    /**
     * 文件删除请求DTO
     * 用于接收删除文件的请求参数
     */
    public static class FileDeleteRequest {
        private String fileUrl; // 要删除的文件URL

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }
} 