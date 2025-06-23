package com.fyp.handsome.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import lombok.Data;

/**
 * 阿里云OSS配置类
 * 负责初始化OSS客户端和相关配置参数
 *
 * @author ziye
 */
@Configuration
@Data
public class OssConfig {

    // 从环境变量中读取OSS配置信息
    @Value("${ALIYUN_OSS_ACCESS_KEY_ID}")
    private String accessKeyId;

    @Value("${ALIYUN_OSS_ACCESS_KEY_SECRET}")
    private String accessKeySecret;

    @Value("${ALIYUN_OSS_BUCKET_NAME}")
    private String bucketName;

    @Value("${ALIYUN_OSS_ENDPOINT}")
    private String endpoint;

    @Value("${ALIYUN_OSS_FILE_NAME:#{null}}")
    private String defaultFileName;

    /**
     * 创建OSS客户端Bean
     * 
     * @return OSS客户端实例
     */
    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    /**
     * 获取完整的OSS访问域名
     * 
     * @return OSS访问域名
     */
    public String getOssUrl() {
        return "https://" + bucketName + "." + endpoint;
    }

    /**
     * 获取文件的完整访问URL
     * 
     * @param objectKey OSS对象键
     * @return 完整的文件访问URL
     */
    public String getFileUrl(String objectKey) {
        return getOssUrl() + "/" + objectKey;
    }
} 