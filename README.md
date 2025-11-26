# 🚗 停车收费管理系统后端

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Redis](https://img.shields.io/badge/Redis-7.0-red.svg)](https://redis.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个基于Spring Boot的企业级停车收费管理系统后端，提供完整的车辆进出管理、自动计费、用户认证授权、数据统计等功能。集成Redis缓存、JWT认证、分布式锁等高级功能，支持高并发场景。

## ✨ 核心特性

### 🔐 认证授权系统
- JWT Token认证，支持多设备登录
- 基于Redis的Token存储和管理
- 权限缓存，提升访问性能
- 自动Token续期和过期清理

### 🚦 缓存与性能优化
- Redis缓存热点数据（用户权限、字典数据等）
- 方法级别缓存注解支持
- 分布式锁保证并发安全
- 智能缓存策略，自动清理过期数据

### 🏗️ 企业级架构
- Spring Boot 3.x + Spring Security
- MyBatis Plus数据访问层
- RESTful API设计规范
- 统一异常处理和响应格式
- 配置中心化管理

### 📊 数据管理
- 车辆进出记录管理
- 自动计费和优惠策略
- 数据统计报表
- 字典数据缓存管理

## 🛠️ 技术栈

### 后端框架
- **Java**: 17
- **Spring Boot**: 3.5.5
- **Spring Security**: 6.x
- **MyBatis Plus**: 3.5.1
- **Redis**: 7.0
- **MySQL**: 8.0

### 开发工具
- **Maven**: 3.9+
- **JUnit**: 5.x
- **Lombok**: 简化Java代码

### 缓存技术
- Redis缓存服务
- JWT Token管理
- 分布式锁实现
- 方法级缓存注解

## 🚀 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+

### 安装步骤

1. **克隆项目**
```bash
git clone https://github.com/your-username/parking-fee-server.git
cd parking-fee-server
```

2. **配置数据库**
```sql
-- 创建数据库
CREATE DATABASE parking_manager CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 导入表结构（如果有SQL文件）
-- source schema.sql;
```

3. **配置Redis**
```bash
# 安装Redis（macOS）
brew install redis
brew services start redis

# 或使用Docker
docker run -d -p 6379:6379 redis:7-alpine
```

4. **修改配置文件**
编辑 `src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/parking_manager?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: your_username
    password: your_password

  data:
    redis:
      host: localhost
      port: 6379
      password: # 如果设置了密码
      database: 0
```

5. **编译运行**
```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run

# 或使用Maven Wrapper
./mvnw spring-boot:run
```

6. **验证服务**
访问 http://localhost:8080/test/hello 验证服务是否正常运行。

## 📖 API文档

### 认证接口

#### 用户登录
```http
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

#### 获取用户信息
```http
GET /auth/info
Authorization: Bearer {token}
```

#### 用户登出
```http
POST /auth/logout
Authorization: Bearer {token}
```

### 响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 响应数据
  }
}
```

## 📁 项目结构

```
parking-fee-server/
├── src/main/java/com/parking/manager/
│   ├── ParkingManagerApplication.java          # 启动类
│   ├── common/                                 # 通用模块
│   │   ├── annotation/                         # 自定义注解
│   │   │   ├── Cacheable.java                  # 缓存注解
│   │   │   └── CacheEvict.java                 # 缓存清除注解
│   │   ├── aspect/                             # AOP切面
│   │   │   └── CacheAspect.java                # 缓存切面实现
│   │   ├── config/                             # 配置类
│   │   │   ├── RedisConfig.java                # Redis配置
│   │   │   └── SecurityConfig.java             # 安全配置
│   │   ├── exception/                          # 异常处理
│   │   │   ├── BusinessException.java          # 业务异常
│   │   │   └── GlobalExceptionHandler.java     # 全局异常处理器
│   │   ├── filter/                             # 过滤器
│   │   │   └── JwtAuthenticationTokenFilter.java # JWT认证过滤器
│   │   ├── handler/                            # 处理器
│   │   │   ├── AuthenticationEntryPointImpl.java # 认证失败处理器
│   │   │   └── LogoutSuccessHandlerImpl.java   # 登出成功处理器
│   │   ├── result/                             # 统一响应
│   │   │   ├── Result.java                     # 响应对象
│   │   │   └── PageResult.java                 # 分页响应
│   │   ├── service/                            # 通用服务
│   │   │   ├── DictCacheService.java           # 字典缓存服务
│   │   │   ├── DistributedLockService.java     # 分布式锁服务
│   │   │   ├── TokenCacheService.java          # Token缓存服务
│   │   │   ├── UserDetailsServiceImpl.java     # 用户详情服务
│   │   │   └── UserPermissionCacheService.java # 用户权限缓存
│   │   └── utils/                              # 工具类
│   │       ├── JwtUtils.java                   # JWT工具类
│   │       └── RedisService.java               # Redis服务工具
│   ├── system/                                 # 系统管理模块
│   │   └── controller/                         # 系统控制器
│   │       ├── AuthController.java             # 认证控制器
│   │       └── TestController.java             # 测试控制器
│   ├── parking/                                # 停车场管理模块
│   ├── vehicle/                                # 车辆管理模块
│   ├── charge/                                 # 收费管理模块
│   ├── inout/                                  # 进出管理模块
│   ├── statistics/                             # 统计报表模块
│   ├── monitor/                                # 系统监控模块
│   └── ai/                                     # AI集成模块
│       └── service/
│           └── AiService.java                  # AI服务接口
├── src/main/resources/
│   ├── application.yml                         # 主配置文件
│   └── application.properties                  # 备用配置文件
├── src/test/                                   # 测试代码
├── .gitignore                                  # Git忽略文件
├── mvnw                                        # Maven Wrapper (Unix)
├── mvnw.cmd                                    # Maven Wrapper (Windows)
├── pom.xml                                     # Maven配置
└── README.md                                   # 项目说明
```

## 🔧 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/parking_manager?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: your_password
```

### Redis配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: # 可选
      database: 0
      timeout: 10s
      lettuce:
        pool:
          max-active: 200
          max-wait: -1ms
          max-idle: 10
          min-idle: 0
```

### JWT配置
```yaml
jwt:
  header: Authorization
  secret: parking-manager-secret-key-2024
  expiration: 7200  # 2小时
  tokenHead: Bearer
```

## 🧪 测试

```bash
# 运行单元测试
mvn test

# 运行特定测试类
mvn test -Dtest=AuthControllerTest

# 生成测试覆盖率报告
mvn test jacoco:report
```

## 📊 监控与运维

### 健康检查
访问 `http://localhost:8080/actuator/health` 查看应用健康状态

### 缓存监控
- Redis连接状态
- 缓存命中率统计
- 内存使用情况

### 日志配置
项目使用SLF4J + Logback进行日志管理，可通过 `application.yml` 配置日志级别：
```yaml
logging:
  level:
    com.parking.manager: DEBUG
    org.springframework.security: DEBUG
```

## 🤝 贡献指南

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 开发规范
- 遵循阿里巴巴Java开发规范
- 提交前运行测试确保通过
- 更新相关文档
- 使用有意义的提交信息

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👥 开发者

- **项目维护者**: [您的名字]
- **邮箱**: your-email@example.com

## 🙏 致谢

感谢以下开源项目和贡献者：
- [Spring Boot](https://spring.io/projects/spring-boot/)
- [MyBatis Plus](https://baomidou.com/)
- [Redis](https://redis.io/)
- [JWT](https://jwt.io/)

---

⭐ 如果这个项目对你有帮助，请给它一个星标！
