package com.fyp.handsome.service.impl.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.quanmiaolightapp20240801.AsyncClient;
import com.aliyun.sdk.service.quanmiaolightapp20240801.models.GetVideoAnalysisTaskRequest;
import com.aliyun.sdk.service.quanmiaolightapp20240801.models.GetVideoAnalysisTaskResponse;
import com.aliyun.sdk.service.quanmiaolightapp20240801.models.SubmitVideoAnalysisTaskRequest;
import com.aliyun.sdk.service.quanmiaolightapp20240801.models.SubmitVideoAnalysisTaskResponse;
import com.google.gson.Gson;

import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云视频分析服务实现类
 * 基于阿里云全貌轻应用SDK实现视频分析功能
 * @author ziye
 */
@Slf4j
@Service
public class AliyunVideoAnalysisService {

    // 从配置文件读取阿里云配置信息
    @Value("${aliyun.access-key-id:}")
    private String accessKeyId;
    
    @Value("${aliyun.access-key-secret:}")
    private String accessKeySecret;
    
    @Value("${aliyun.region:cn-beijing}")
    private String region;
    
    @Value("${aliyun.endpoint:quanmiaolightapp.cn-beijing.aliyuncs.com}")
    private String endpoint;
    
    @Value("${aliyun.workspace-id:llm-f3b251bxpv09yaq9}")
    private String workspaceId;
    
    @Value("${aliyun.video-model-id:qwen-vl-max-latest}")
    private String videoModelId;

    // 默认的视频分析提示模板
    private static final String DEFAULT_PROMPT_TEMPLATE = "请分析这个监控视频:{videoAsrText} 并严格按照以下JSON格式返回结果：\n\n" +
            "{\n" +
            "  \"content_analysis\": {\n" +
            "    \"overall_summary\": \"填写视频整体描述\",\n" +
            "    \"scene_info\": {\n" +
            "      \"location_type\": \"填写场所类型\",\n" +
            "      \"lighting_condition\": \"填写光线条件\",\n" +
            "      \"video_quality\": \"填写视频清晰度\",\n" +
            "      \"weather\": \"填写天气或环境\"\n" +
            "    },\n" +
            "    \"key_objects\": [\"物体1\", \"物体2\", \"物体3\"],\n" +
            "    \"activity_summary\": \"填写活动总结\",\n" +
            "    \"notable_events\": [\"时间点1 事件描述1\", \"时间点2 事件描述2\"]\n" +
            "  },\n" +
            "  \"people_behavior\": {\n" +
            "    \"people_count\": 填写数字,\n" +
            "    \"people_details\": [\n" +
            "      {\n" +
            "        \"person_id\": 1,\n" +
            "        \"description\": \"填写人员外观描述\",\n" +
            "        \"main_activities\": [\"活动1\", \"活动2\"],\n" +
            "        \"time_in_scene\": \"填写在场时间\",\n" +
            "        \"behavior_status\": \"正常或异常\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"abnormal_behaviors\": [\n" +
            "      {\n" +
            "        \"person_id\": 填写人员ID,\n" +
            "        \"behavior\": \"填写异常行为描述\",\n" +
            "        \"time_period\": \"开始时间-结束时间\",\n" +
            "        \"severity\": \"轻微/中等/严重\",\n" +
            "        \"description\": \"填写详细描述\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"interaction_summary\": \"填写人员互动总结\"\n" +
            "  },\n" +
            "  \"event_detection\": {\n" +
            "    \"total_events\": 填写数字,\n" +
            "    \"events\": [\n" +
            "      {\n" +
            "        \"event_id\": 1,\n" +
            "        \"event_type\": \"填写事件类型\",\n" +
            "        \"event_time\": \"HH:MM:SS格式\",\n" +
            "        \"duration\": \"填写持续时间\",\n" +
            "        \"location\": \"填写发生位置\",\n" +
            "        \"severity\": \"正常/轻微/中等/严重\",\n" +
            "        \"description\": \"填写事件详细描述\",\n" +
            "        \"involved_people\": 填写涉及人数,\n" +
            "        \"follow_up_required\": true或false\n" +
            "      }\n" +
            "    ],\n" +
            "    \"security_status\": \"正常/异常/警告\",\n" +
            "    \"alert_events\": 填写需要警报的事件数量,\n" +
            "    \"summary\": \"填写事件总结\"\n" +
            "  },\n" +
            "  \"timeline_analysis\": {\n" +
            "    \"video_duration\": \"填写视频时长\",\n" +
            "    \"analysis_period\": \"开始时间 - 结束时间\",\n" +
            "    \"timeline\": [\n" +
            "      {\n" +
            "        \"time\": \"HH:MM:SS\",\n" +
            "        \"event\": \"填写事件\",\n" +
            "        \"people_count\": 填写人数,\n" +
            "        \"activity\": \"填写活动描述\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"peak_activity_time\": \"填写活动高峰时段\",\n" +
            "    \"quiet_periods\": [\"时间段1\", \"时间段2\"],\n" +
            "    \"attention_points\": [\"时间点1 - 描述1\", \"时间点2 - 描述2\"],\n" +
            "    \"overall_assessment\": \"填写整体评估\"\n" +
            "  }\n" +
            "}\n\n" +
            "请严格按照以上格式返回，不要添加或删除任何字段，只需要填充具体的内容值。用中文描述，注重监控实用价值。";

    /**
     * 创建阿里云客户端
     * @return AsyncClient 异步客户端
     */
    private AsyncClient createAliyunClient() {
        try {
            log.info("开始创建阿里云客户端，region：{}，endpoint：{}", region, endpoint);
            
            // HttpClient配置（可选）
            /*HttpClient httpClient = new ApacheAsyncHttpClientBuilder()
                    .connectionTimeout(Duration.ofSeconds(10)) // 设置连接超时时间，默认10秒
                    .responseTimeout(Duration.ofSeconds(10)) // 设置响应超时时间，默认20秒
                    .maxConnections(128) // 设置连接池大小
                    .maxIdleTimeOut(Duration.ofSeconds(50)) // 设置连接池超时时间，默认30秒
                    // 配置代理
                    .proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("<your-proxy-hostname>", 9001))
                            .setCredentials("<your-proxy-username>", "<your-proxy-password>"))
                    // 如果是https连接，需要配置证书，或忽略证书(.ignoreSSL(true))
                    .x509TrustManagers(new X509TrustManager[]{})
                    .keyManagers(new KeyManager[]{})
                    .ignoreSSL(false)
                    .build();*/

            // 配置认证信息，包括ak、secret、token
            StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                    // 优先使用配置文件中的值，如果为空则使用环境变量
                    .accessKeyId(accessKeyId.isEmpty() ? System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID") : accessKeyId)
                    .accessKeySecret(accessKeySecret.isEmpty() ? System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET") : accessKeySecret)
                    //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // 使用STS token
                    .build());

            // 配置客户端
            AsyncClient client = AsyncClient.builder()
                    .region(region) // 区域ID
                    //.httpClient(httpClient) // 使用配置的HttpClient，否则使用默认HttpClient (Apache HttpClient)
                    .credentialsProvider(provider)
                    //.serviceConfiguration(Configuration.create()) // 服务级别配置
                    // 客户端级别配置重写，可以设置Endpoint、Http请求参数等
                    .overrideConfiguration(
                            ClientOverrideConfiguration.create()
                                    // Endpoint 请参考 https://api.aliyun.com/product/QuanMiaoLightApp
                                    .setEndpointOverride(endpoint)
                            //.setConnectTimeout(Duration.ofSeconds(30))
                    )
                    .build();
            
            log.info("阿里云客户端创建成功");
            return client;
            
        } catch (Exception e) {
            log.error("创建阿里云客户端失败，错误：{}", e.getMessage(), e);
            throw new RuntimeException("创建阿里云客户端失败", e);
        }
    }

    /**
     * 提交视频分析任务
     * @param videoUrl 视频URL地址
     * @return SubmitVideoAnalysisTaskResponse 分析任务响应
     */
    public SubmitVideoAnalysisTaskResponse submitVideoAnalysisTask(String videoUrl) {
        return submitVideoAnalysisTask(videoUrl, DEFAULT_PROMPT_TEMPLATE);
    }

    /**
     * 提交视频分析任务（自定义提示模板）
     * @param videoUrl 视频URL地址
     * @param customPromptTemplate 自定义分析提示模板
     * @return SubmitVideoAnalysisTaskResponse 分析任务响应
     */
    public SubmitVideoAnalysisTaskResponse submitVideoAnalysisTask(String videoUrl, String customPromptTemplate) {
        AsyncClient client = null;
        try {
            log.info("开始提交视频分析任务，videoUrl：{}", videoUrl);
            
            // 创建客户端
            client = createAliyunClient();

            // API请求参数设置
            SubmitVideoAnalysisTaskRequest submitVideoAnalysisTaskRequest = SubmitVideoAnalysisTaskRequest.builder()
                    .workspaceId(workspaceId)
                    .videoUrl(videoUrl)
                    .videoModelId(videoModelId)
                    .videoModelCustomPromptTemplate(customPromptTemplate)
                    // 请求级别配置重写，可以设置Http请求参数等
                    // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                    .build();

            // 异步获取API请求的返回值
            CompletableFuture<SubmitVideoAnalysisTaskResponse> responseFuture = client.submitVideoAnalysisTask(submitVideoAnalysisTaskRequest);
            
            // 同步获取API请求的返回值
            SubmitVideoAnalysisTaskResponse response = responseFuture.get();
            
            log.info("视频分析任务提交成功，taskId：{}", response.getBody().getData().getTaskId());
            log.debug("分析任务响应详情：{}", new Gson().toJson(response));
            
            return response;
            
        } catch (Exception e) {
            log.error("提交视频分析任务失败，videoUrl：{}，错误：{}", videoUrl, e.getMessage(), e);
            throw new RuntimeException("提交视频分析任务失败", e);
        } finally {
            // 最后关闭客户端
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    log.warn("关闭阿里云客户端时发生异常：{}", e.getMessage());
                }
            }
        }
    }

    /**
     * 异步提交视频分析任务
     * @param videoUrl 视频URL地址
     * @param callback 异步回调处理
     */
    public void submitVideoAnalysisTaskAsync(String videoUrl, VideoAnalysisCallback callback) {
        submitVideoAnalysisTaskAsync(videoUrl, DEFAULT_PROMPT_TEMPLATE, callback);
    }

    /**
     * 异步提交视频分析任务（自定义提示模板）
     * @param videoUrl 视频URL地址
     * @param customPromptTemplate 自定义分析提示模板
     * @param callback 异步回调处理
     */
    public void submitVideoAnalysisTaskAsync(String videoUrl, String customPromptTemplate, VideoAnalysisCallback callback) {
        AsyncClient client = null;
        try {
            log.info("开始异步提交视频分析任务，videoUrl：{}", videoUrl);
            
            // 创建客户端
            client = createAliyunClient();

            // API请求参数设置
            SubmitVideoAnalysisTaskRequest submitVideoAnalysisTaskRequest = SubmitVideoAnalysisTaskRequest.builder()
                    .workspaceId(workspaceId)
                    .videoUrl(videoUrl)
                    .videoModelId(videoModelId)
                    .videoModelCustomPromptTemplate(customPromptTemplate)
                    .build();

            // 异步获取API请求的返回值
            CompletableFuture<SubmitVideoAnalysisTaskResponse> responseFuture = client.submitVideoAnalysisTask(submitVideoAnalysisTaskRequest);
            
            final AsyncClient finalClient = client;
            
            // 异步处理返回值
            responseFuture.thenAccept(response -> {
                try {
                    log.info("异步视频分析任务提交成功，taskId：{}", response.getBody().getData().getTaskId());
                    callback.onSuccess(response);
                } catch (Exception e) {
                    log.error("处理异步分析成功回调时发生异常：{}", e.getMessage(), e);
                    callback.onError(e);
                } finally {
                    // 关闭客户端
                    try {
                        finalClient.close();
                    } catch (Exception e) {
                        log.warn("关闭阿里云客户端时发生异常：{}", e.getMessage());
                    }
                }
            }).exceptionally(throwable -> {
                try {
                    log.error("异步视频分析任务提交失败：{}", throwable.getMessage(), throwable);
                    callback.onError(throwable);
                } finally {
                    // 关闭客户端
                    try {
                        finalClient.close();
                    } catch (Exception e) {
                        log.warn("关闭阿里云客户端时发生异常：{}", e.getMessage());
                    }
                }
                return null;
            });
            
        } catch (Exception e) {
            log.error("异步提交视频分析任务失败，videoUrl：{}，错误：{}", videoUrl, e.getMessage(), e);
            callback.onError(e);
            // 如果在创建过程中出现异常，需要关闭客户端
            if (client != null) {
                try {
                    client.close();
                } catch (Exception closeException) {
                    log.warn("关闭阿里云客户端时发生异常：{}", closeException.getMessage());
                }
            }
        }
    }

    /**
     * 视频分析回调接口
     */
    public interface VideoAnalysisCallback {
        /**
         * 分析成功回调
         * @param response 分析响应结果
         */
        void onSuccess(SubmitVideoAnalysisTaskResponse response);

        /**
         * 分析失败回调
         * @param throwable 异常信息
         */
        void onError(Throwable throwable);
    }

    /**
     * 获取视频分析任务结果
     * @param taskId 任务ID
     * @return GetVideoAnalysisTaskResponse 任务结果响应
     */
    public GetVideoAnalysisTaskResponse getVideoAnalysisTask(String taskId) {
        AsyncClient client = null;
        try {
            log.info("开始查询视频分析任务结果，taskId：{}", taskId);
            
            // 创建客户端
            client = createAliyunClient();

            // API请求参数设置
            GetVideoAnalysisTaskRequest getVideoAnalysisTaskRequest = GetVideoAnalysisTaskRequest.builder()
                    .taskId(taskId)
                    .workspaceId(workspaceId)
                    // 请求级别配置重写，可以设置Http请求参数等
                    // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                    .build();

            // 异步获取API请求的返回值
            CompletableFuture<GetVideoAnalysisTaskResponse> responseFuture = client.getVideoAnalysisTask(getVideoAnalysisTaskRequest);
            
            // 同步获取API请求的返回值
            GetVideoAnalysisTaskResponse response = responseFuture.get();
            
            log.info("视频分析任务查询成功，taskId：{}，状态：{}", taskId, response.getBody().getData().getTaskStatus());
            log.debug("任务查询响应详情：{}", new Gson().toJson(response));
            
            return response;
            
        } catch (Exception e) {
            log.error("查询视频分析任务失败，taskId：{}，错误：{}", taskId, e.getMessage(), e);
            throw new RuntimeException("查询视频分析任务失败", e);
        } finally {
            // 最后关闭客户端
            if (client != null) {
                try {
                    client.close();
                } catch (Exception e) {
                    log.warn("关闭阿里云客户端时发生异常：{}", e.getMessage());
                }
            }
        }
    }

    /**
     * 异步查询视频分析任务结果
     * @param taskId 任务ID
     * @param callback 异步回调处理
     */
    public void getVideoAnalysisTaskAsync(String taskId, VideoAnalysisResultCallback callback) {
        AsyncClient client = null;
        try {
            log.info("开始异步查询视频分析任务结果，taskId：{}", taskId);
            
            // 创建客户端
            client = createAliyunClient();

            // API请求参数设置
            GetVideoAnalysisTaskRequest getVideoAnalysisTaskRequest = GetVideoAnalysisTaskRequest.builder()
                    .taskId(taskId)
                    .workspaceId(workspaceId)
                    .build();

            // 异步获取API请求的返回值
            CompletableFuture<GetVideoAnalysisTaskResponse> responseFuture = client.getVideoAnalysisTask(getVideoAnalysisTaskRequest);
            
            final AsyncClient finalClient = client;
            
            // 异步处理返回值
            responseFuture.thenAccept(response -> {
                try {
                    log.info("异步查询视频分析任务成功，taskId：{}，状态：{}", taskId, response.getBody().getData().getTaskStatus());
                    callback.onSuccess(response);
                } catch (Exception e) {
                    log.error("处理异步查询成功回调时发生异常：{}", e.getMessage(), e);
                    callback.onError(e);
                } finally {
                    // 关闭客户端
                    try {
                        finalClient.close();
                    } catch (Exception e) {
                        log.warn("关闭阿里云客户端时发生异常：{}", e.getMessage());
                    }
                }
            }).exceptionally(throwable -> {
                try {
                    log.error("异步查询视频分析任务失败：{}", throwable.getMessage(), throwable);
                    callback.onError(throwable);
                } finally {
                    // 关闭客户端
                    try {
                        finalClient.close();
                    } catch (Exception e) {
                        log.warn("关闭阿里云客户端时发生异常：{}", e.getMessage());
                    }
                }
                return null;
            });
            
        } catch (Exception e) {
            log.error("异步查询视频分析任务失败，taskId：{}，错误：{}", taskId, e.getMessage(), e);
            callback.onError(e);
            // 如果在创建过程中出现异常，需要关闭客户端
            if (client != null) {
                try {
                    client.close();
                } catch (Exception closeException) {
                    log.warn("关闭阿里云客户端时发生异常：{}", closeException.getMessage());
                }
            }
        }
    }

    /**
     * 视频分析结果查询回调接口
     */
    public interface VideoAnalysisResultCallback {
        /**
         * 查询成功回调
         * @param response 查询响应结果
         */
        void onSuccess(GetVideoAnalysisTaskResponse response);

        /**
         * 查询失败回调
         * @param throwable 异常信息
         */
        void onError(Throwable throwable);
    }

    /**
     * 获取配置信息（用于调试）
     * @return Map<String, String> 配置信息
     */
    public Map<String, String> getConfigInfo() {
        Map<String, String> config = new HashMap<>();
        config.put("region", region);
        config.put("endpoint", endpoint);
        config.put("workspaceId", workspaceId);
        config.put("videoModelId", videoModelId);
        config.put("accessKeyIdConfigured", !accessKeyId.isEmpty() ? "是" : "否");
        config.put("accessKeySecretConfigured", !accessKeySecret.isEmpty() ? "是" : "否");
        return config;
    }

    /**
     * 提取视频分析结果中的核心JSON内容
     * @param response 阿里云完整响应
     * @return 解析后的JSON对象
     */
    public Object extractAnalysisResult(GetVideoAnalysisTaskResponse response) {
        try {
            log.info("开始提取视频分析结果");
            
            // 获取videoAnalysisResult中的text字段
            var data = response.getBody().getData();
            if (data != null && data.getPayload() != null && data.getPayload().getOutput() != null) {
                var output = data.getPayload().getOutput();
                if (output.getVideoAnalysisResult() != null) {
                    String text = output.getVideoAnalysisResult().getText();
                    if (text != null && !text.isEmpty()) {
                        log.info("获取到分析结果文本，开始提取JSON内容");
                        
                        // 提取JSON部分（去除前面的描述文字）
                        String jsonPart = extractJsonFromText(text);
                        if (jsonPart != null) {
                            // 使用Gson解析JSON字符串
                            Gson gson = new Gson();
                            Object result = gson.fromJson(jsonPart, Object.class);
                            log.info("JSON内容提取并解析成功");
                            return result;
                        }
                    }
                }
            }
            
            log.warn("未能提取到有效的分析结果");
            return null;
            
        } catch (Exception e) {
            log.error("提取分析结果失败：{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从text字段中提取JSON内容
     * @param text 包含JSON的文本
     * @return 纯JSON字符串
     */
    private String extractJsonFromText(String text) {
        try {
            log.debug("开始从文本中提取JSON内容");
            
            // 查找```json和```之间的内容
            int jsonStart = text.indexOf("```json");
            if (jsonStart != -1) {
                jsonStart = text.indexOf("\n", jsonStart) + 1; // 跳过```json行
                int jsonEnd = text.indexOf("```", jsonStart);
                if (jsonEnd != -1) {
                    String jsonContent = text.substring(jsonStart, jsonEnd).trim();
                    log.debug("通过```json标记提取到JSON内容");
                    return jsonContent;
                }
            }
            
            // 如果没有找到```json标记，尝试查找第一个{开始的JSON
            int firstBrace = text.indexOf("{");
            if (firstBrace != -1) {
                // 查找最后一个}
                int lastBrace = text.lastIndexOf("}");
                if (lastBrace != -1 && lastBrace > firstBrace) {
                    String jsonContent = text.substring(firstBrace, lastBrace + 1).trim();
                    log.debug("通过{}括号提取到JSON内容");
                    return jsonContent;
                }
            }
            
            log.warn("未能从文本中提取到有效的JSON内容");
            return null;
            
        } catch (Exception e) {
            log.error("从文本中提取JSON失败：{}", e.getMessage(), e);
            return null;
        }
    }
} 