// This file is auto-generated, don't edit it. Thanks.
package demo;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.core.http.HttpClient;
import com.aliyun.core.http.HttpMethod;
import com.aliyun.core.http.ProxyOptions;
import com.aliyun.httpcomponent.httpclient.ApacheAsyncHttpClientBuilder;
import com.aliyun.sdk.service.quanmiaolightapp20240801.models.*;
import com.aliyun.sdk.service.quanmiaolightapp20240801.*;
import com.google.gson.Gson;
import darabonba.core.RequestConfiguration;
import darabonba.core.client.ClientOverrideConfiguration;
import darabonba.core.utils.CommonUtil;
import darabonba.core.TeaPair;

//import javax.net.ssl.KeyManager;
//import javax.net.ssl.X509TrustManager;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.io.*;

public class SubmitVideoAnalysisTask {
    public static void main(String[] args) throws Exception {

        // HttpClient Configuration
        /*HttpClient httpClient = new ApacheAsyncHttpClientBuilder()
                .connectionTimeout(Duration.ofSeconds(10)) // Set the connection timeout time, the default is 10 seconds
                .responseTimeout(Duration.ofSeconds(10)) // Set the response timeout time, the default is 20 seconds
                .maxConnections(128) // Set the connection pool size
                .maxIdleTimeOut(Duration.ofSeconds(50)) // Set the connection pool timeout, the default is 30 seconds
                // Configure the proxy
                .proxy(new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress("<your-proxy-hostname>", 9001))
                        .setCredentials("<your-proxy-username>", "<your-proxy-password>"))
                // If it is an https connection, you need to configure the certificate, or ignore the certificate(.ignoreSSL(true))
                .x509TrustManagers(new X509TrustManager[]{})
                .keyManagers(new KeyManager[]{})
                .ignoreSSL(false)
                .build();*/

        // Configure Credentials authentication information, including ak, secret, token
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                // Please ensure that the environment variables ALIBABA_CLOUD_ACCESS_KEY_ID and ALIBABA_CLOUD_ACCESS_KEY_SECRET are set.
                .accessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"))
                .accessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"))
                //.securityToken(System.getenv("ALIBABA_CLOUD_SECURITY_TOKEN")) // use STS token
                .build());

        // Configure the Client
        AsyncClient client = AsyncClient.builder()
                .region("cn-beijing") // Region ID
                //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
                .credentialsProvider(provider)
                //.serviceConfiguration(Configuration.create()) // Service-level configuration
                // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                  // Endpoint 请参考 https://api.aliyun.com/product/QuanMiaoLightApp
                                .setEndpointOverride("quanmiaolightapp.cn-beijing.aliyuncs.com")
                        //.setConnectTimeout(Duration.ofSeconds(30))
                )
                .build();

        // Parameter settings for API request
        SubmitVideoAnalysisTaskRequest submitVideoAnalysisTaskRequest = SubmitVideoAnalysisTaskRequest.builder()
                .workspaceId("llm-f3b251bxpv09yaq9")
                .videoUrl("https://ziyeimg.oss-cn-shanghai.aliyuncs.com/ob/%E5%BE%AE%E4%BF%A1%E8%A7%86%E9%A2%912025-06-23_164747_137.mp4")
                .videoModelId("qwen-vl-max-latest")
                .videoModelCustomPromptTemplate("请分析这个监控视频:{videoAsrText} 并严格按照以下JSON格式返回结果：\n\n{\n  \"content_analysis\": {\n    \"overall_summary\": \"填写视频整体描述\",\n    \"scene_info\": {\n      \"location_type\": \"填写场所类型\",\n      \"lighting_condition\": \"填写光线条件\",\n      \"video_quality\": \"填写视频清晰度\",\n      \"weather\": \"填写天气或环境\"\n    },\n    \"key_objects\": [\"物体1\", \"物体2\", \"物体3\"],\n    \"activity_summary\": \"填写活动总结\",\n    \"notable_events\": [\"时间点1 事件描述1\", \"时间点2 事件描述2\"]\n  },\n  \"people_behavior\": {\n    \"people_count\": 填写数字,\n    \"people_details\": [\n      {\n        \"person_id\": 1,\n        \"description\": \"填写人员外观描述\",\n        \"main_activities\": [\"活动1\", \"活动2\"],\n        \"time_in_scene\": \"填写在场时间\",\n        \"behavior_status\": \"正常或异常\"\n      }\n    ],\n    \"abnormal_behaviors\": [\n      {\n        \"person_id\": 填写人员ID,\n        \"behavior\": \"填写异常行为描述\",\n        \"time_period\": \"开始时间-结束时间\",\n        \"severity\": \"轻微/中等/严重\",\n        \"description\": \"填写详细描述\"\n      }\n    ],\n    \"interaction_summary\": \"填写人员互动总结\"\n  },\n  \"event_detection\": {\n    \"total_events\": 填写数字,\n    \"events\": [\n      {\n        \"event_id\": 1,\n        \"event_type\": \"填写事件类型\",\n        \"event_time\": \"HH:MM:SS格式\",\n        \"duration\": \"填写持续时间\",\n        \"location\": \"填写发生位置\",\n        \"severity\": \"正常/轻微/中等/严重\",\n        \"description\": \"填写事件详细描述\",\n        \"involved_people\": 填写涉及人数,\n        \"follow_up_required\": true或false\n      }\n    ],\n    \"security_status\": \"正常/异常/警告\",\n    \"alert_events\": 填写需要警报的事件数量,\n    \"summary\": \"填写事件总结\"\n  },\n  \"timeline_analysis\": {\n    \"video_duration\": \"填写视频时长\",\n    \"analysis_period\": \"开始时间 - 结束时间\",\n    \"timeline\": [\n      {\n        \"time\": \"HH:MM:SS\",\n        \"event\": \"填写事件\",\n        \"people_count\": 填写人数,\n        \"activity\": \"填写活动描述\"\n      }\n    ],\n    \"peak_activity_time\": \"填写活动高峰时段\",\n    \"quiet_periods\": [\"时间段1\", \"时间段2\"],\n    \"attention_points\": [\"时间点1 - 描述1\", \"时间点2 - 描述2\"],\n    \"overall_assessment\": \"填写整体评估\"\n  }\n}\n\n请严格按照以上格式返回，不要添加或删除任何字段，只需要填充具体的内容值。用中文描述，注重监控实用价值。")
                // Request-level configuration rewrite, can set Http request parameters, etc.
                // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
                .build();

        // Asynchronously get the return value of the API request
        CompletableFuture<SubmitVideoAnalysisTaskResponse> response = client.submitVideoAnalysisTask(submitVideoAnalysisTaskRequest);
        // Synchronously get the return value of the API request
        SubmitVideoAnalysisTaskResponse resp = response.get();
        System.out.println(new Gson().toJson(resp));
        // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/

        // Finally, close the client
        client.close();
    }

}