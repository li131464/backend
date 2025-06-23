package com.fyp.handsome.service.impl.oss;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.fyp.handsome.config.OssConfig;
import com.fyp.handsome.service.OssUploadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * OSS上传服务实现类
 * 负责处理文件上传到阿里云OSS的具体业务逻辑
 *
 * @author ziye
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OssUploadServiceImpl implements OssUploadService {

    private final OSS ossClient; // OSS客户端
    private final OssConfig ossConfig; // OSS配置

    /**
     * 上传视频文件到阿里云OSS
     * 自动将视频文件存储到videos/文件夹下
     *
     * @param videoFile 视频文件
     * @return OSS文件访问URL
     * @throws Exception 上传失败时抛出异常
     */
    @Override
    public String uploadVideo(MultipartFile videoFile) throws Exception {
        log.info("开始上传视频文件: {}", videoFile.getOriginalFilename());
        
        // 验证文件是否为视频格式
        if (!isVideoFile(videoFile)) {
            throw new IllegalArgumentException("上传的文件不是支持的视频格式");
        }
        
        // 上传到videos文件夹
        return uploadFile(videoFile, "ob/");
    }

    /**
     * 上传文件到阿里云OSS（通用方法）
     *
     * @param file 文件
     * @param folder 文件夹路径（可选，如 "videos/"）
     * @return OSS文件访问URL
     * @throws Exception 上传失败时抛出异常
     */
    @Override
    public String uploadFile(MultipartFile file, String folder) throws Exception {
        // 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }

        // 生成唯一的文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFileName = generateUniqueFileName(originalFilename, fileExtension);
        
        // 构建完整的对象键（包含文件夹路径）
        String objectKey = (folder != null && !folder.isEmpty()) ? folder + uniqueFileName : uniqueFileName;
        
        log.info("上传文件到OSS，原文件名: {}, 对象键: {}", originalFilename, objectKey);

        try (InputStream inputStream = file.getInputStream()) {
            // 创建上传对象的元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize()); // 设置文件大小
            metadata.setContentType(file.getContentType()); // 设置文件类型
            metadata.setContentDisposition("inline"); // 设置文件可以在浏览器中直接显示

            // 创建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucketName(), 
                objectKey, 
                inputStream, 
                metadata
            );

            // 执行上传
            ossClient.putObject(putObjectRequest);
            
            // 生成文件访问URL
            String fileUrl = ossConfig.getFileUrl(objectKey);
            log.info("文件上传成功，访问URL: {}", fileUrl);
            
            return fileUrl;
            
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new Exception("文件上传到OSS失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除OSS中的文件
     *
     * @param fileUrl OSS文件URL
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            // 从URL中提取对象键
            String objectKey = extractObjectKeyFromUrl(fileUrl);
            if (objectKey == null) {
                log.warn("无法从URL中提取对象键: {}", fileUrl);
                return false;
            }
            
            // 执行删除操作
            ossClient.deleteObject(ossConfig.getBucketName(), objectKey);
            log.info("文件删除成功，对象键: {}", objectKey);
            return true;
            
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param fileUrl OSS文件URL
     * @return 文件是否存在
     */
    @Override
    public boolean fileExists(String fileUrl) {
        try {
            // 从URL中提取对象键
            String objectKey = extractObjectKeyFromUrl(fileUrl);
            if (objectKey == null) {
                return false;
            }
            
            // 检查文件是否存在
            return ossClient.doesObjectExist(ossConfig.getBucketName(), objectKey);
            
        } catch (Exception e) {
            log.error("检查文件是否存在时发生错误: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 验证文件是否为视频格式
     *
     * @param file 文件
     * @return 是否为视频格式
     */
    private boolean isVideoFile(MultipartFile file) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        
        // 检查MIME类型
        if (contentType != null && contentType.startsWith("video/")) {
            return true;
        }
        
        // 检查文件扩展名
        if (originalFilename != null) {
            String extension = getFileExtension(originalFilename).toLowerCase();
            return extension.matches("\\.(mp4|avi|mov|wmv|flv|webm|mkv|m4v|3gp)$");
        }
        
        return false;
    }

    /**
     * 获取文件扩展名
     *
     * @param filename 文件名
     * @return 文件扩展名（包含点号）
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return filename.substring(lastDotIndex);
    }

    /**
     * 生成唯一的文件名
     *
     * @param originalFilename 原始文件名
     * @param fileExtension 文件扩展名
     * @return 唯一的文件名
     */
    private String generateUniqueFileName(String originalFilename, String fileExtension) {
        // 生成时间戳（年月日时分秒）
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        
        // 生成UUID（取前8位）
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        
        // 如果设置了默认文件名前缀，使用它
        String prefix = ossConfig.getDefaultFileName() != null ? ossConfig.getDefaultFileName() + "_" : "";
        
        // 组合生成唯一文件名：前缀_时间戳_UUID_原始文件名（不含扩展名）.扩展名
        String baseFilename = originalFilename != null ? 
            originalFilename.substring(0, originalFilename.lastIndexOf('.') != -1 ? 
                originalFilename.lastIndexOf('.') : originalFilename.length()) : "file";
        
        return prefix + timestamp + "_" + uuid + "_" + baseFilename + fileExtension;
    }

    /**
     * 从OSS文件URL中提取对象键
     *
     * @param fileUrl OSS文件URL
     * @return 对象键，如果提取失败返回null
     */
    private String extractObjectKeyFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }
        
        try {
            // 构建预期的OSS URL前缀
            String expectedPrefix = ossConfig.getOssUrl() + "/";
            
            if (fileUrl.startsWith(expectedPrefix)) {
                return fileUrl.substring(expectedPrefix.length());
            } else {
                log.warn("文件URL格式不正确: {}", fileUrl);
                return null;
            }
        } catch (Exception e) {
            log.error("从URL提取对象键时发生错误: {}", e.getMessage(), e);
            return null;
        }
    }
} 