package com.fyp.handsome.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OSS上传服务接口
 * 负责处理文件上传到阿里云OSS的业务逻辑
 *
 * @author ziye
 */
public interface OssUploadService {

    /**
     * 上传视频文件到阿里云OSS
     *
     * @param videoFile 视频文件
     * @return OSS文件访问URL
     * @throws Exception 上传失败时抛出异常
     */
    String uploadVideo(MultipartFile videoFile) throws Exception;

    /**
     * 上传文件到阿里云OSS（通用方法）
     *
     * @param file 文件
     * @param folder 文件夹路径（可选，如 "videos/"）
     * @return OSS文件访问URL
     * @throws Exception 上传失败时抛出异常
     */
    String uploadFile(MultipartFile file, String folder) throws Exception;

    /**
     * 删除OSS中的文件
     *
     * @param fileUrl OSS文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);

    /**
     * 检查文件是否存在
     *
     * @param fileUrl OSS文件URL
     * @return 文件是否存在
     */
    boolean fileExists(String fileUrl);
} 