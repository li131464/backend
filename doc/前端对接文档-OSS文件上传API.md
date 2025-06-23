# OSS文件上传API接口文档

## 接口概览

本文档描述了阿里云OSS文件上传相关的API接口，包括视频上传、通用文件上传、文件删除和文件存在性检查等功能。

**基础URL**: `http://localhost:8190`

---

## 1. 视频文件上传

### 接口信息
- **URL**: `/api/oss/upload/video`
- **方法**: `POST`
- **Content-Type**: `multipart/form-data`
- **描述**: 专用于视频文件上传，自动存储到`ob/`文件夹，支持视频格式验证

### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| videoFile | File | 是 | 视频文件，支持格式：mp4, avi, mov, wmv, flv, webm, mkv, m4v, 3gp |

### 响应格式
```json
{
  "code": 200,
  "message": "视频上传成功",
  "data": {
    "fileUrl": "https://ziyeimg.oss-cn-shanghai.aliyuncs.com/ob/ob_20241228_161234_abcd1234_video.mp4",
    "originalFilename": "video.mp4",
    "fileSize": "1024000"
  }
}
```

### 错误响应
```json
{
  "code": 500,
  "message": "上传失败: 上传的文件不是支持的视频格式",
  "data": null
}
```

---

## 2. 通用文件上传

### 接口信息
- **URL**: `/api/oss/upload/file`
- **方法**: `POST`
- **Content-Type**: `multipart/form-data`
- **描述**: 通用文件上传接口，可指定存储文件夹

### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| file | File | 是 | 要上传的文件 |
| folder | String | 否 | 文件夹路径，如"videos/"，默认为根目录 |

### 响应格式
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "fileUrl": "https://ziyeimg.oss-cn-shanghai.aliyuncs.com/videos/ob_20241228_161234_abcd1234_file.ext",
    "originalFilename": "file.ext",
    "folder": "videos/",
    "fileSize": "1024000"
  }
}
```

---

## 3. 文件删除

### 接口信息
- **URL**: `/api/oss/delete`
- **方法**: `DELETE`
- **Content-Type**: `application/json`
- **描述**: 根据文件URL删除OSS中的文件

### 请求参数
```json
{
  "fileUrl": "https://ziyeimg.oss-cn-shanghai.aliyuncs.com/ob/ob_20241228_161234_abcd1234_video.mp4"
}
```

### 响应格式
```json
{
  "code": 200,
  "message": "文件删除成功",
  "data": {
    "deleted": true,
    "fileUrl": "https://ziyeimg.oss-cn-shanghai.aliyuncs.com/ob/ob_20241228_161234_abcd1234_video.mp4"
  }
}
```

### 错误响应
```json
{
  "code": 500,
  "message": "文件删除失败",
  "data": {
    "deleted": false,
    "fileUrl": "https://ziyeimg.oss-cn-shanghai.aliyuncs.com/ob/ob_20241228_161234_abcd1234_video.mp4"
  }
}
```

---

## 4. 文件存在性检查

### 接口信息
- **URL**: `/api/oss/exists`
- **方法**: `GET`
- **描述**: 检查指定URL的文件是否存在于OSS中

### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| fileUrl | String | 是 | 完整的OSS文件URL |

### 响应格式
```json
{
  "code": 200,
  "message": "检查完成",
  "data": {
    "exists": true,
    "fileUrl": "https://ziyeimg.oss-cn-shanghai.aliyuncs.com/ob/ob_20241228_161234_abcd1234_video.mp4"
  }
}
```

---

## 响应状态码说明

| 状态码 | 描述 |
|--------|------|
| 200 | 操作成功 |
| 500 | 服务器内部错误 |

## 文件命名规则

上传到OSS的文件会自动重命名，命名格式为：
```
{前缀}_{时间戳}_{UUID}_{原始文件名}.{扩展名}
```

- **前缀**: 环境变量`ALIYUN_OSS_FILE_NAME`的值（当前为"ob"）
- **时间戳**: 格式为`yyyyMMdd_HHmmss`
- **UUID**: 8位随机字符串
- **原始文件名**: 去除扩展名的原文件名

示例：`ob_20241228_161234_abcd1234_video.mp4`

## 注意事项

1. **文件大小限制**: 根据Spring Boot默认配置，单个文件最大1MB，总请求最大10MB
2. **支持的视频格式**: mp4, avi, mov, wmv, flv, webm, mkv, m4v, 3gp
3. **文件URL**: 返回的URL可直接用于前端显示或下载
4. **错误处理**: 所有接口都有统一的错误响应格式
5. **环境配置**: 确保服务器已配置正确的阿里云OSS环境变量 