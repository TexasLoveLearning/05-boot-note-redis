# 📝 备忘录 API

基于 **Spring Boot 3** 的备忘录 RESTful API 项目，支持用户注册登录、备忘录 CRUD、Redis 缓存、参数校验和全局异常处理。

## 技术栈

| 技术 | 说明 |
|------|------|
| **Spring Boot 3.3.5** | 框架 |
| **MySQL 8** | 数据库 |
| **MyBatis-Plus 3.5.7** | ORM |
| **Redis 7** | 缓存 |
| **Spring Validation** | 参数校验 |
| **Lombok** | 代码简化 |
| **BCrypt** | 密码加密 |

## 功能

- ✅ 用户注册（密码 BCrypt 加密）
- ✅ 用户登录（返回 userId）
- ✅ 备忘录 CRUD（增删改查）
- ✅ 按用户查询备忘录
- ✅ 分页查询（MyBatis-Plus 分页插件）
- ✅ 逻辑删除（deleted 字段）
- ✅ Redis 缓存（Cache-Aside 模式）
- ✅ 全局异常处理（统一返回格式）
- ✅ 参数校验（@Valid）

## 快速启动

### 前置要求

- JDK 17+
- MySQL 8+
- Redis 7+

### 1. 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS boot_note;
```

### 2. 导入表结构

项目启动时会自动建表（MyBatis-Plus 自动 DDL），或手动执行：

```sql
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    create_time DATETIME DEFAULT NOW()
);

CREATE TABLE note (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    create_time DATETIME DEFAULT NOW(),
    update_time DATETIME DEFAULT NOW(),
    deleted INT DEFAULT 0
);
```

### 3. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/boot_note
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 4. 启动

```bash
mvn spring-boot:run
```

访问 `http://localhost:8080`

## API 文档

### 用户

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/register` | 注册 |
| POST | `/api/user/login` | 登录 |

### 备忘录

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/notes` | 新增 |
| GET | `/api/notes/{id}` | 查单个 |
| PUT | `/api/notes/{id}` | 修改 |
| DELETE | `/api/notes/{id}` | 删除 |
| GET | `/api/notes/user/{userId}` | 按用户查 |
| GET | `/api/notes/page?page=1&size=10` | 分页查 |

### 请求示例

见 `src/test.http`（IDEA HTTP Client 可直接运行）

## 项目结构

```
src/main/java/com/fyc/_4bootnote/
├── common/
│   ├── GlobalExceptionHandler.java   # 全局异常处理
│   ├── Result.java                   # 统一返回结果
│   └── ServiceException.java         # 自定义业务异常
├── config/
│   ├── MyBatisPlusConfig.java        # MyBatis-Plus 配置（分页）
│   └── RedisConfig.java              # Redis JSON 序列化配置
├── controller/
│   ├── NoteController.java           # 备忘录 API
│   └── UserController.java           # 用户 API
├── dto/
│   ├── UserLoginDTO.java
│   └── UserRegisterDTO.java
├── entity/
│   ├── Note.java
│   └── User.java
├── mapper/
│   ├── NoteMapper.java
│   └── UserMapper.java
└── Application.java
```

## 学习记录

详见 `NOTES.md`，包含开发过程遇到的问题和关键知识点总结。
