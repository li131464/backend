# 视频监控信息管理系统 - 用户权限管理API前端对接文档

## 一、概述

### 1.1 文档说明
本文档详细描述视频监控信息管理系统用户权限管理模块的API接口，供前端开发团队进行接口对接。系统采用RESTful API设计风格，支持用户认证、用户管理、角色管理、权限分配等功能。

### 1.2 基本信息
- **API基础URL**: `http://localhost:8190`
- **API前缀**: `/api/user`
- **数据格式**: JSON
- **字符编码**: UTF-8
- **HTTP方法**: GET、POST、PUT、DELETE

### 1.3 认证机制
系统采用Token认证机制：
- 用户登录成功后获得`accessToken`和`refreshToken`
- `accessToken`用于API访问认证，有效期2小时
- `refreshToken`用于刷新Token，有效期7天
- 需要在请求头中携带：`Authorization: Bearer {accessToken}`（部分接口需要）

## 二、通用数据结构

### 2.1 统一响应格式
所有API接口均返回统一的响应格式：

```json
{
    "code": 200,          // 响应状态码
    "success": true,      // 是否成功
    "message": "操作成功", // 响应消息
    "data": {},          // 响应数据（可为空）
    "timestamp": "2024-01-01T12:00:00"  // 响应时间戳
}
```

### 2.2 状态码说明
| 状态码 | 说明 | 描述 |
|--------|------|------|
| 200 | SUCCESS | 操作成功 |
| 400 | BAD_REQUEST | 请求参数错误 |
| 401 | UNAUTHORIZED | 未授权访问 |
| 403 | FORBIDDEN | 访问被禁止 |
| 404 | NOT_FOUND | 资源不存在 |
| 409 | CONFLICT | 资源冲突 |
| 500 | INTERNAL_ERROR | 服务器内部错误 |

### 2.3 分页数据结构
分页查询接口返回的数据结构：

```json
{
    "code": 200,
    "success": true,
    "data": {
        "records": [],        // 当前页数据列表
        "total": 100,         // 总记录数
        "size": 10,          // 每页大小
        "current": 1,        // 当前页码
        "pages": 10,         // 总页数
        "searchCount": true,  // 是否进行count查询
        "optimizeCountSql": true,
        "maxLimit": null,
        "countId": null,
        "orders": []
    }
}
```

### 2.4 实体数据结构

#### 2.4.1 用户信息（User）
```json
{
    "id": 1,                           // 用户ID
    "username": "admin",               // 用户名
    "password": null,                  // 密码（响应中不返回）
    "realName": "管理员",              // 真实姓名
    "email": "admin@example.com",      // 邮箱
    "phone": "13800138000",           // 手机号
    "status": 1,                      // 状态（1-正常，0-禁用）
    "lastLoginTime": "2024-01-01T12:00:00",  // 最后登录时间
    "createTime": "2024-01-01T10:00:00",     // 创建时间
    "updateTime": "2024-01-01T12:00:00"      // 更新时间
}
```

#### 2.4.2 角色信息（Role）
```json
{
    "id": 1,                          // 角色ID
    "roleName": "超级管理员",          // 角色名称
    "roleCode": "SUPER_ADMIN",        // 角色编码
    "roleDescription": "系统超级管理员", // 角色描述
    "status": 1,                      // 状态（1-正常，0-删除）
    "createTime": "2024-01-01T10:00:00",  // 创建时间
    "updateTime": "2024-01-01T10:00:00"   // 更新时间
}
```

#### 2.4.3 权限信息（Permission）
```json
{
    "id": 1,                          // 权限ID
    "permissionName": "视频信息查询",   // 权限名称
    "permissionCode": "video:query",  // 权限编码
    "permissionDescription": "查询视频信息的权限", // 权限描述
    "permissionType": 3,              // 权限类型（1-菜单，2-按钮，3-接口）
    "status": 1,                      // 状态（1-正常，0-删除）
    "createTime": "2024-01-01T10:00:00"   // 创建时间
}
```

## 三、用户认证接口

### 3.1 用户登录

#### 接口信息
- **接口地址**: `POST /api/user/login`
- **接口描述**: 用户登录认证，获取访问令牌
- **是否需要认证**: 否

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

#### 请求示例
```json
{
    "username": "admin",
    "password": "admin123"
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "登录成功",
    "data": {
        "success": true,
        "message": "登录成功",
        "user": {
            "id": 1,
            "username": "admin",
            "realName": "管理员",
            "email": "admin@example.com",
            "phone": "13800138000",
            "status": 1,
            "lastLoginTime": "2024-01-01T12:00:00"
        },
        "accessToken": "token_1_1735648800000",
        "refreshToken": "refresh_1_1735648800000",
        "tokenType": "Bearer",
        "expiresIn": 7200
    }
}
```

#### 错误响应
```json
{
    "code": 401,
    "success": false,
    "message": "用户名或密码错误"
}
```

### 3.2 用户登出

#### 接口信息
- **接口地址**: `POST /api/user/logout`
- **接口描述**: 用户登出，清除会话信息
- **是否需要认证**: 否

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID（作为查询参数） |

#### 请求示例
```
POST /api/user/logout?userId=1
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 3.3 刷新Token

#### 接口信息
- **接口地址**: `POST /api/user/refresh-token`
- **接口描述**: 使用刷新令牌获取新的访问令牌
- **是否需要认证**: 否

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| refreshToken | String | 是 | 刷新令牌 |

#### 请求示例
```json
{
    "refreshToken": "refresh_1_1735648800000"
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "Token刷新成功",
    "data": {
        "success": true,
        "message": "Token刷新成功",
        "accessToken": "token_1_1735651200000",
        "refreshToken": "refresh_1_1735651200000",
        "tokenType": "Bearer",
        "expiresIn": 7200
    }
}
```

#### 错误响应
```json
{
    "code": 401,
    "success": false,
    "message": "刷新令牌无效或已过期"
}
```

## 四、用户管理接口

### 4.1 用户注册

#### 接口信息
- **接口地址**: `POST /api/user/register`
- **接口描述**: 注册新用户账户
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名（4-20字符，字母数字下划线） |
| password | String | 是 | 密码（6-20字符） |
| realName | String | 是 | 真实姓名 |
| email | String | 否 | 邮箱地址 |
| phone | String | 否 | 手机号码 |
| status | Integer | 否 | 状态（1-正常，0-禁用，默认1） |

#### 请求示例
```json
{
    "username": "testuser",
    "password": "test123",
    "realName": "测试用户",
    "email": "test@example.com",
    "phone": "13800138000"
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

#### 错误响应
```json
{
    "code": 409,
    "success": false,
    "message": "用户名已存在"
}
```

### 4.2 获取用户信息

#### 接口信息
- **接口地址**: `GET /api/user/info`
- **接口描述**: 获取当前用户信息
- **是否需要认证**: 是

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID（查询参数） |

#### 请求示例
```
GET /api/user/info?userId=1
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "admin",
        "realName": "管理员",
        "email": "admin@example.com",
        "phone": "13800138000",
        "status": 1,
        "lastLoginTime": "2024-01-01T12:00:00",
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T12:00:00"
    }
}
```

### 4.3 根据ID获取用户信息

#### 接口信息
- **接口地址**: `GET /api/user/{id}`
- **接口描述**: 根据用户ID获取用户信息
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID（路径参数） |

#### 请求示例
```
GET /api/user/1
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "admin",
        "realName": "管理员",
        "email": "admin@example.com",
        "phone": "13800138000",
        "status": 1,
        "lastLoginTime": "2024-01-01T12:00:00",
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T12:00:00"
    }
}
```

### 4.4 更新用户信息

#### 接口信息
- **接口地址**: `PUT /api/user/info`
- **接口描述**: 更新当前用户信息
- **是否需要认证**: 是

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID |
| realName | String | 否 | 真实姓名 |
| email | String | 否 | 邮箱地址 |
| phone | String | 否 | 手机号码 |

#### 请求示例
```json
{
    "id": 1,
    "realName": "管理员（更新）",
    "email": "admin_updated@example.com",
    "phone": "13800138001"
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 4.5 更新用户信息（管理员使用）

#### 接口信息
- **接口地址**: `PUT /api/user/{id}`
- **接口描述**: 管理员更新用户信息
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID（路径参数） |
| realName | String | 否 | 真实姓名 |
| email | String | 否 | 邮箱地址 |
| phone | String | 否 | 手机号码 |
| status | Integer | 否 | 状态 |

#### 请求示例
```json
{
    "realName": "管理员（管理员更新）",
    "email": "admin_admin_updated@example.com",
    "phone": "13800138002"
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 4.6 修改密码

#### 接口信息
- **接口地址**: `PUT /api/user/password`
- **接口描述**: 用户修改自己的密码
- **是否需要认证**: 是

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | String | 是 | 用户ID |
| oldPassword | String | 是 | 旧密码 |
| newPassword | String | 是 | 新密码（6-20字符） |

#### 请求示例
```json
{
    "userId": "1",
    "oldPassword": "oldpass123",
    "newPassword": "newpass123"
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

#### 错误响应
```json
{
    "code": 400,
    "success": false,
    "message": "原密码错误"
}
```

### 4.7 用户列表查询

#### 接口信息
- **接口地址**: `GET /api/user/list`
- **接口描述**: 分页查询用户列表，支持条件筛选
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |
| username | String | 否 | 用户名（模糊查询） |
| realName | String | 否 | 真实姓名（模糊查询） |
| email | String | 否 | 邮箱（模糊查询） |
| status | Integer | 否 | 状态筛选 |

#### 请求示例
```
GET /api/user/list?current=1&size=10&username=admin&status=1
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": {
        "records": [
            {
                "id": 1,
                "username": "admin",
                "realName": "管理员",
                "email": "admin@example.com",
                "phone": "13800138000",
                "status": 1,
                "lastLoginTime": "2024-01-01T12:00:00",
                "createTime": "2024-01-01T10:00:00",
                "updateTime": "2024-01-01T12:00:00"
            }
        ],
        "total": 1,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

### 4.8 删除用户

#### 接口信息
- **接口地址**: `DELETE /api/user/{id}`
- **接口描述**: 删除指定用户
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID（路径参数） |

#### 请求示例
```
DELETE /api/user/2
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 4.9 批量删除用户

#### 接口信息
- **接口地址**: `DELETE /api/user/batch`
- **接口描述**: 批量删除用户
- **是否需要认证**: 是（管理员权限）

#### 请求参数
请求体为用户ID数组

#### 请求示例
```json
[3, 4, 5]
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 4.10 更新用户状态

#### 接口信息
- **接口地址**: `PUT /api/user/{id}/status`
- **接口描述**: 启用或禁用用户
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID（路径参数） |
| status | Integer | 是 | 状态（查询参数，1-启用，0-禁用） |

#### 请求示例
```
PUT /api/user/1/status?status=1
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 4.11 重置用户密码

#### 接口信息
- **接口地址**: `PUT /api/user/{id}/reset-password`
- **接口描述**: 管理员重置用户密码
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID（路径参数） |
| newPassword | String | 是 | 新密码 |

#### 请求示例
```json
{
    "newPassword": "resetpass123"
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 4.12 修改用户密码（管理员使用）

#### 接口信息
- **接口地址**: `PUT /api/user/{id}/change-password`
- **接口描述**: 管理员修改用户密码
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 用户ID（路径参数） |
| oldPassword | String | 是 | 旧密码 |
| newPassword | String | 是 | 新密码 |

#### 请求示例
```json
{
    "oldPassword": "oldpass123",
    "newPassword": "newpass123"
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

## 五、用户查询接口

### 5.1 根据用户名获取用户

#### 接口信息
- **接口地址**: `GET /api/user/username/{username}`
- **接口描述**: 根据用户名查询用户信息
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名（路径参数） |

#### 请求示例
```
GET /api/user/username/testuser
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": {
        "id": 2,
        "username": "testuser",
        "realName": "测试用户",
        "email": "test@example.com",
        "phone": "13800138000",
        "status": 1,
        "lastLoginTime": "2024-01-01T12:00:00",
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T12:00:00"
    }
}
```

### 5.2 根据邮箱获取用户

#### 接口信息
- **接口地址**: `GET /api/user/email/{email}`
- **接口描述**: 根据邮箱查询用户信息
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| email | String | 是 | 邮箱地址（路径参数） |

#### 请求示例
```
GET /api/user/email/admin@example.com
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": {
        "id": 1,
        "username": "admin",
        "realName": "管理员",
        "email": "admin@example.com",
        "phone": "13800138000",
        "status": 1,
        "lastLoginTime": "2024-01-01T12:00:00",
        "createTime": "2024-01-01T10:00:00",
        "updateTime": "2024-01-01T12:00:00"
    }
}
```

### 5.3 分页查询用户信息

#### 接口信息
- **接口地址**: `GET /api/user/page`
- **接口描述**: 分页查询用户信息（兼容性接口）
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |
| username | String | 否 | 用户名（模糊查询） |
| realName | String | 否 | 真实姓名（模糊查询） |
| email | String | 否 | 邮箱（模糊查询） |
| status | Integer | 否 | 状态筛选 |

#### 请求示例
```
GET /api/user/page?current=1&size=10&username=admin&realName=admin&status=1
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": {
        "records": [
            {
                "id": 1,
                "username": "admin",
                "realName": "管理员",
                "email": "admin@example.com",
                "phone": "13800138000",
                "status": 1,
                "lastLoginTime": "2024-01-01T12:00:00",
                "createTime": "2024-01-01T10:00:00",
                "updateTime": "2024-01-01T12:00:00"
            }
        ],
        "total": 1,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

## 六、角色管理接口

### 6.1 新增角色

#### 接口信息
- **接口地址**: `POST /api/user/role`
- **接口描述**: 创建新角色
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roleName | String | 是 | 角色名称 |
| roleCode | String | 是 | 角色编码（唯一） |
| roleDescription | String | 否 | 角色描述 |
| status | Integer | 否 | 状态（1-正常，0-删除，默认1） |

#### 请求示例
```json
{
    "roleName": "测试角色",
    "roleCode": "TEST_ROLE",
    "roleDescription": "用于测试的角色",
    "status": 1
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

#### 错误响应
```json
{
    "code": 409,
    "success": false,
    "message": "角色已存在"
}
```

### 6.2 更新角色

#### 接口信息
- **接口地址**: `PUT /api/user/role/{id}`
- **接口描述**: 更新角色信息
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 角色ID（路径参数） |
| roleName | String | 是 | 角色名称 |
| roleCode | String | 是 | 角色编码 |
| roleDescription | String | 否 | 角色描述 |
| status | Integer | 否 | 状态 |

#### 请求示例
```json
{
    "roleName": "超级管理员（更新）",
    "roleCode": "SUPER_ADMIN_UPDATED",
    "roleDescription": "更新后的超级管理员角色",
    "status": 1
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 6.3 删除角色

#### 接口信息
- **接口地址**: `DELETE /api/user/role/{id}`
- **接口描述**: 删除指定角色
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 角色ID（路径参数） |

#### 请求示例
```
DELETE /api/user/role/5
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 6.4 获取所有角色

#### 接口信息
- **接口地址**: `GET /api/user/role/all`
- **接口描述**: 获取所有有效角色列表
- **是否需要认证**: 是

#### 请求参数
无

#### 请求示例
```
GET /api/user/role/all
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": [
        {
            "id": 1,
            "roleName": "超级管理员",
            "roleCode": "SUPER_ADMIN",
            "roleDescription": "系统超级管理员，拥有所有权限",
            "status": 1,
            "createTime": "2024-01-01T10:00:00",
            "updateTime": "2024-01-01T10:00:00"
        },
        {
            "id": 2,
            "roleName": "监控管理员",
            "roleCode": "MONITOR_ADMIN",
            "roleDescription": "负责视频监控设备管理",
            "status": 1,
            "createTime": "2024-01-01T10:00:00",
            "updateTime": "2024-01-01T10:00:00"
        }
    ]
}
```

### 6.5 分页查询角色

#### 接口信息
- **接口地址**: `GET /api/user/role/page`
- **接口描述**: 分页查询角色列表，支持条件筛选
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |
| roleName | String | 否 | 角色名称（模糊查询） |
| roleCode | String | 否 | 角色编码（模糊查询） |
| status | Integer | 否 | 状态筛选 |

#### 请求示例
```
GET /api/user/role/page?current=1&size=10&roleName=管理员&status=1
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": {
        "records": [
            {
                "id": 1,
                "roleName": "超级管理员",
                "roleCode": "SUPER_ADMIN",
                "roleDescription": "系统超级管理员，拥有所有权限",
                "status": 1,
                "createTime": "2024-01-01T10:00:00",
                "updateTime": "2024-01-01T10:00:00"
            }
        ],
        "total": 1,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

## 七、权限管理接口

### 7.1 新增权限

#### 接口信息
- **接口地址**: `POST /api/user/permission`
- **接口描述**: 创建新权限
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| permissionName | String | 是 | 权限名称 |
| permissionCode | String | 是 | 权限编码（唯一） |
| permissionDescription | String | 否 | 权限描述 |
| permissionType | Integer | 是 | 权限类型（1-菜单，2-按钮，3-接口） |
| status | Integer | 否 | 状态（1-正常，0-删除，默认1） |

#### 请求示例
```json
{
    "permissionName": "测试权限",
    "permissionCode": "test:permission",
    "permissionDescription": "用于测试的权限",
    "permissionType": 3,
    "status": 1
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 7.2 更新权限

#### 接口信息
- **接口地址**: `PUT /api/user/permission/{id}`
- **接口描述**: 更新权限信息
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 权限ID（路径参数） |
| permissionName | String | 是 | 权限名称 |
| permissionCode | String | 是 | 权限编码 |
| permissionDescription | String | 否 | 权限描述 |
| permissionType | Integer | 是 | 权限类型 |
| status | Integer | 否 | 状态 |

#### 请求示例
```json
{
    "permissionName": "视频信息查询（更新）",
    "permissionCode": "video:query:updated",
    "permissionDescription": "更新后的视频查询权限",
    "permissionType": 3,
    "status": 1
}
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 7.3 删除权限

#### 接口信息
- **接口地址**: `DELETE /api/user/permission/{id}`
- **接口描述**: 删除指定权限
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 权限ID（路径参数） |

#### 请求示例
```
DELETE /api/user/permission/17
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 7.4 获取所有权限

#### 接口信息
- **接口地址**: `GET /api/user/permission/all`
- **接口描述**: 获取所有有效权限列表
- **是否需要认证**: 是

#### 请求参数
无

#### 请求示例
```
GET /api/user/permission/all
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": [
        {
            "id": 1,
            "permissionName": "视频信息查询",
            "permissionCode": "video:query",
            "permissionDescription": "查询视频信息的权限",
            "permissionType": 3,
            "status": 1,
            "createTime": "2024-01-01T10:00:00"
        },
        {
            "id": 2,
            "permissionName": "视频信息新增",
            "permissionCode": "video:add",
            "permissionDescription": "新增视频信息的权限",
            "permissionType": 3,
            "status": 1,
            "createTime": "2024-01-01T10:00:00"
        }
    ]
}
```

### 7.5 分页查询权限

#### 接口信息
- **接口地址**: `GET /api/user/permission/page`
- **接口描述**: 分页查询权限列表，支持条件筛选
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| current | Integer | 否 | 当前页码（默认1） |
| size | Integer | 否 | 每页大小（默认10） |
| permissionName | String | 否 | 权限名称（模糊查询） |
| permissionCode | String | 否 | 权限编码（模糊查询） |
| permissionType | Integer | 否 | 权限类型筛选 |
| status | Integer | 否 | 状态筛选 |

#### 请求示例
```
GET /api/user/permission/page?current=1&size=10&permissionName=视频&permissionType=3&status=1
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": {
        "records": [
            {
                "id": 1,
                "permissionName": "视频信息查询",
                "permissionCode": "video:query",
                "permissionDescription": "查询视频信息的权限",
                "permissionType": 3,
                "status": 1,
                "createTime": "2024-01-01T10:00:00"
            }
        ],
        "total": 1,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

## 八、用户角色关系管理接口

### 8.1 为用户分配角色

#### 接口信息
- **接口地址**: `POST /api/user/{userId}/roles`
- **接口描述**: 为指定用户分配角色
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID（路径参数） |
| roleIds | Array | 是 | 角色ID列表（请求体） |

#### 请求示例
```json
[1, 2]
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 8.2 获取用户的角色

#### 接口信息
- **接口地址**: `GET /api/user/{userId}/roles`
- **接口描述**: 获取指定用户的角色列表
- **是否需要认证**: 是

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID（路径参数） |

#### 请求示例
```
GET /api/user/1/roles
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": [
        {
            "id": 1,
            "roleName": "超级管理员",
            "roleCode": "SUPER_ADMIN",
            "roleDescription": "系统超级管理员，拥有所有权限",
            "status": 1,
            "createTime": "2024-01-01T10:00:00",
            "updateTime": "2024-01-01T10:00:00"
        }
    ]
}
```

### 8.3 为角色分配权限

#### 接口信息
- **接口地址**: `POST /api/user/role/{roleId}/permissions`
- **接口描述**: 为指定角色分配权限
- **是否需要认证**: 是（管理员权限）

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roleId | Long | 是 | 角色ID（路径参数） |
| permissionIds | Array | 是 | 权限ID列表（请求体） |

#### 请求示例
```json
[1, 2, 3, 6, 7]
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功"
}
```

### 8.4 获取角色的权限

#### 接口信息
- **接口地址**: `GET /api/user/role/{roleId}/permissions`
- **接口描述**: 获取指定角色的权限列表
- **是否需要认证**: 是

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roleId | Long | 是 | 角色ID（路径参数） |

#### 请求示例
```
GET /api/user/role/1/permissions
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": [
        {
            "id": 1,
            "permissionName": "视频信息查询",
            "permissionCode": "video:query",
            "permissionDescription": "查询视频信息的权限",
            "permissionType": 3,
            "status": 1,
            "createTime": "2024-01-01T10:00:00"
        },
        {
            "id": 2,
            "permissionName": "视频信息新增",
            "permissionCode": "video:add",
            "permissionDescription": "新增视频信息的权限",
            "permissionType": 3,
            "status": 1,
            "createTime": "2024-01-01T10:00:00"
        }
    ]
}
```

### 8.5 获取用户的权限

#### 接口信息
- **接口地址**: `GET /api/user/{userId}/permissions`
- **接口描述**: 获取指定用户的所有权限（通过角色继承得到）
- **是否需要认证**: 是

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID（路径参数） |

#### 请求示例
```
GET /api/user/1/permissions
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": [
        {
            "id": 1,
            "permissionName": "视频信息查询",
            "permissionCode": "video:query",
            "permissionDescription": "查询视频信息的权限",
            "permissionType": 3,
            "status": 1,
            "createTime": "2024-01-01T10:00:00"
        }
    ]
}
```

## 九、权限验证接口

### 9.1 检查用户权限

#### 接口信息
- **接口地址**: `GET /api/user/{userId}/has-permission/{permissionCode}`
- **接口描述**: 检查指定用户是否拥有指定权限
- **是否需要认证**: 是

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID（路径参数） |
| permissionCode | String | 是 | 权限编码（路径参数） |

#### 请求示例
```
GET /api/user/2/has-permission/video:query
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": true
}
```

### 9.2 检查用户角色

#### 接口信息
- **接口地址**: `GET /api/user/{userId}/has-role/{roleCode}`
- **接口描述**: 检查指定用户是否拥有指定角色
- **是否需要认证**: 是

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| userId | Long | 是 | 用户ID（路径参数） |
| roleCode | String | 是 | 角色编码（路径参数） |

#### 请求示例
```
GET /api/user/2/has-role/SUPER_ADMIN
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": false
}
```

## 十、统计信息接口

### 10.1 获取用户统计概览

#### 接口信息
- **接口地址**: `GET /api/user/statistics`
- **接口描述**: 获取用户相关的统计信息
- **是否需要认证**: 是（管理员权限）

#### 请求参数
无

#### 请求示例
```
GET /api/user/statistics
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": {
        "totalUsers": 100,        // 总用户数
        "activeUsers": 95,        // 活跃用户数
        "disabledUsers": 5,       // 禁用用户数
        "totalRoles": 4,          // 总角色数
        "totalPermissions": 16,   // 总权限数
        "todayLogins": 25,        // 今日登录数
        "recentRegistrations": 3   // 近7天注册数
    }
}
```

### 10.2 获取用户总数

#### 接口信息
- **接口地址**: `GET /api/user/count`
- **接口描述**: 获取系统用户总数
- **是否需要认证**: 是（管理员权限）

#### 请求参数
无

#### 请求示例
```
GET /api/user/count
```

#### 响应示例
```json
{
    "code": 200,
    "success": true,
    "message": "操作成功",
    "data": 100
}
```

## 十一、错误处理

### 11.1 常见错误码

| 错误码 | 错误信息 | 说明 | 解决方案 |
|--------|----------|------|----------|
| 400 | 请求参数错误 | 请求参数格式不正确或缺失必填参数 | 检查请求参数格式和完整性 |
| 401 | 未授权访问 | Token无效或已过期 | 重新登录获取新Token |
| 403 | 访问被禁止 | 用户权限不足 | 联系管理员分配相应权限 |
| 404 | 资源不存在 | 请求的用户、角色或权限不存在 | 检查资源ID是否正确 |
| 409 | 资源冲突 | 用户名、角色编码、权限编码已存在 | 使用不同的标识符 |
| 500 | 服务器内部错误 | 系统内部异常 | 联系系统管理员 |

### 11.2 错误响应格式

```json
{
    "code": 400,
    "success": false,
    "message": "用户名已存在",
    "data": null,
    "timestamp": "2024-01-01T12:00:00"
}
```

## 十四、附录

### 14.1 预定义角色列表
| 角色ID | 角色名称 | 角色编码 | 描述 |
|--------|----------|----------|------|
| 1 | 超级管理员 | SUPER_ADMIN | 系统最高权限，可管理所有功能 |
| 2 | 监控管理员 | MONITOR_ADMIN | 负责视频监控设备管理 |
| 3 | 安全管理员 | SECURITY_ADMIN | 负责系统安全管理 |
| 4 | 数据分析员 | DATA_ANALYST | 负责数据分析和报表 |

### 14.2 预定义权限列表
| 权限编码 | 权限名称 | 权限描述 | 权限类型 |
|----------|----------|----------|----------|
| video:query | 视频信息查询 | 查询视频信息的权限 | 接口权限 |
| video:add | 视频信息新增 | 新增视频信息的权限 | 接口权限 |
| video:edit | 视频信息修改 | 修改视频信息的权限 | 接口权限 |
| video:delete | 视频信息删除 | 删除视频信息的权限 | 接口权限 |
| analysis:query | 视频分析查询 | 查询分析结果的权限 | 接口权限 |
| analysis:execute | 视频分析执行 | 执行视频分析的权限 | 接口权限 |
| visualization:view | 可视化查看 | 查看可视化图表的权限 | 接口权限 |
| user:manage | 用户管理 | 用户增删改查权限 | 接口权限 |

### 14.3 数据字典

#### 用户状态
- 1: 正常
- 0: 禁用

#### 角色状态
- 1: 正常
- 0: 删除

#### 权限类型
- 1: 菜单权限
- 2: 按钮权限
- 3: 接口权限

#### 权限状态
- 1: 正常
- 0: 删除

---

**文档版本**: v1.0  
**创建时间**: 2024-01-01  
**最后更新**: 2024-01-01  
**维护团队**: 后端开发组  

如有疑问，请联系开发团队。 