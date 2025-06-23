# 视频信息管理MVP

## 📋 项目简介

视频信息管理MVP是从完整的视频监控信息管理系统中提取出的最小化可行产品，专注于视频信息的基础CRUD操作。本项目采用最小化实现策略，确保核心功能完整可用，为后续功能扩展奠定基础。

## 🎯 核心特性

- ✅ **视频信息管理**：支持视频信息的增删改查操作
- ✅ **多条件查询**：支持按名称、地点、来源、时间等条件查询
- ✅ **分页支持**：完整的分页查询功能
- ✅ **参数校验**：完善的输入参数校验机制
- ✅ **逻辑删除**：安全的逻辑删除机制
- ✅ **异常处理**：统一的异常处理和错误响应
- ✅ **RESTful API**：标准的REST接口设计

## 🛠 技术栈

- **后端框架**: Spring Boot 3.4.5
- **Java版本**: Java 21
- **数据库**: MySQL
- **ORM框架**: MyBatis Plus 3.5.5
- **构建工具**: Maven
- **代码简化**: Lombok

## 📁 项目结构

```
handsome/
├── src/main/java/com/fyp/handsome/
│   ├── controller/          # 控制器层
│   ├── service/            # 业务逻辑层
│   │   └── impl/video/     # 视频相关服务实现
│   ├── mapper/             # 数据访问层
│   ├── entity/             # 实体类
│   ├── dto/                # 数据传输对象
│   │   └── video/          # 视频相关DTO
│   ├── exception/          # 异常处理
│   ├── config/             # 配置类
│   └── util/               # 工具类
├── src/main/resources/
│   ├── application.yml     # 应用配置
│   └── mapper/             # MyBatis映射文件
└── doc/video-management-mvp/   # MVP项目文档
    ├── 01-项目概述与需求.md
    ├── 02-数据库设计.md
    ├── 03-后端接口设计.md
    ├── 04-开发任务拆分.md
    ├── 05-API测试用例.http
    └── README.md
```

## 📚 文档导航

1. **[项目概述与需求](./01-项目概述与需求.md)** - 项目背景、目标和功能需求
2. **[数据库设计](./02-数据库设计.md)** - 数据库表结构和设计说明
3. **[后端接口设计](./03-后端接口设计.md)** - 详细的API接口文档
4. **[开发任务拆分](./04-开发任务拆分.md)** - 完整的开发任务和计划
5. **[API测试用例](./05-API测试用例.http)** - 完整的接口测试用例

## 🚀 快速开始

### 前置条件

- Java 21+
- MySQL 8.0+
- Maven 3.6+

### 环境配置

1. **配置数据库**
   ```sql
   -- 创建数据库
   CREATE DATABASE handsome CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **执行数据库初始化脚本**
   ```bash
   # 执行数据库设计文档中的表创建脚本
   mysql -u username -p handsome < doc/database_design.sql
   ```

3. **修改应用配置**
   ```yaml
   # src/main/resources/application.yml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/handsome
       username: your_username
       password: your_password
   ```

### 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/handsome-*.jar
```

### 测试接口

项目启动后，可以使用提供的测试用例文件进行接口测试：

1. 使用VSCode REST Client插件打开 `doc/video-management-mvp/05-API测试用例.http`
2. 逐个执行测试用例验证功能

## 📋 API接口概览

### 基础路径
```
http://localhost:8080/api/v1/videos
```

### 主要接口

| 方法 | 路径 | 功能 |
|------|------|------|
| POST | `/api/v1/videos` | 创建视频信息 |
| GET | `/api/v1/videos` | 分页查询视频信息 |
| GET | `/api/v1/videos/{id}` | 根据ID查询视频信息 |
| PUT | `/api/v1/videos/{id}` | 更新视频信息 |
| DELETE | `/api/v1/videos/{id}` | 删除视频信息 |

## 🧪 测试说明

### 测试覆盖范围

- ✅ 正常业务流程测试
- ✅ 参数校验测试
- ✅ 异常场景测试
- ✅ 边界值测试
- ✅ 特殊字符测试
- ✅ 逻辑删除验证

### 测试数据

项目提供了完整的测试数据和测试用例，包括：

- 正常的视频信息创建测试
- 各种查询条件的组合测试
- 参数校验失败的场景测试
- 边界值和特殊字符的测试

## 📈 开发进度

### 已完成功能 ✅

- [x] 项目需求分析和设计
- [x] 数据库表结构设计
- [x] API接口设计规范
- [x] 开发任务拆分计划
- [x] 测试用例设计

### 开发计划 📅

| 阶段 | 任务 | 预计时间 | 状态 |
|------|------|----------|------|
| 阶段一 | 基础框架搭建 | 0.5天 | ⏳ 待开始 |
| 阶段二 | 业务逻辑层开发 | 1天 | ⏳ 待开始 |
| 阶段三 | 控制器层开发 | 0.5天 | ⏳ 待开始 |
| 阶段四 | 配置和工具类 | 0.5天 | ⏳ 待开始 |
| 阶段五 | 测试和优化 | 1天 | ⏳ 待开始 |

**预计总开发时间：3.5天**

## 🔧 开发规范

### 代码规范

- 遵循阿里巴巴Java开发规范
- 使用Lombok简化代码
- 为每一行关键代码添加中文注释
- 合理的异常处理机制

### 命名规范

- 类名：大驼峰命名法（PascalCase）
- 方法名和变量名：小驼峰命名法（camelCase）
- 常量：全大写下划线分隔（UPPER_SNAKE_CASE）
- 数据库表和字段：下划线分隔（snake_case）

### 包结构规范

```
com.fyp.handsome
├── controller     # 控制器层
├── service        # 服务接口
│   └── impl       # 服务实现
├── mapper         # 数据访问层
├── entity         # 实体类
├── dto            # 数据传输对象
├── exception      # 异常类
├── config         # 配置类
└── util           # 工具类
```

## 🛡️ 质量保证

### 代码质量标准

- [ ] 代码结构清晰，分层合理
- [ ] 命名规范，符合Java编码标准
- [ ] 注释完整，特别是业务逻辑部分
- [ ] 无明显的代码重复
- [ ] 异常处理机制完善

### 测试标准

- [ ] API测试用例全部通过
- [ ] 边界值测试通过
- [ ] 异常场景测试通过
- [ ] 性能基准测试通过

## 🚀 后续扩展

### 短期扩展（1-2周）

- 批量操作功能
- 数据导入导出
- 高级搜索功能
- 操作日志记录

### 中期扩展（1个月）

- 文件上传功能
- 缓存机制
- 用户权限集成
- 性能监控

### 长期扩展（3个月）

- 视频分析功能集成
- 可视化展示功能
- 微服务拆分
- 分布式部署

## 📞 联系信息

- **开发者**: ziye
- **项目地址**: /Users/fengyuping/Desktop/nb/handsome
- **文档位置**: doc/video-management-mvp/

## 📄 许可证

本项目仅用于学习和开发目的。

---

**注意**: 这是一个MVP项目，专注于核心功能实现。所有的设计决策都以最小化实现为原则，确保快速交付和功能验证。 