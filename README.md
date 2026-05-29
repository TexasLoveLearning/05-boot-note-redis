# 备忘录 API (04-boot-note)

Spring Boot 入门练习项目 — 备忘录 CRUD API

## 技术栈

- Java 17 / 21
- Spring Boot 3.3.5
- MyBatisPlus 3.5.7
- MySQL 8
- bcrypt 密码加密

## API 列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/user/register | 用户注册 |
| POST | /api/user/login | 用户登录 |
| POST | /api/notes | 新增备忘录 |
| GET | /api/notes/{id} | 查单个备忘录 |
| GET | /api/notes/user/{userId} | 查某用户的所有备忘录 |
| GET | /api/notes/page?page=1&size=10 | 分页查询 |
| PUT | /api/notes/{id} | 更新备忘录 |
| DELETE | /api/notes/{id} | 删除备忘录（逻辑删除） |

## 项目结构

```
src/main/java/com/fyc/_4bootnote/
├── common/Result.java        ← 统一返回类
├── config/MyBatisPlusConfig.java  ← 分页插件配置
├── controller/
│   ├── UserController.java   ← 注册 & 登录
│   └── NoteController.java   ← 备忘录 CRUD
├── dto/                      ← 请求参数对象
├── entity/                   ← 实体类
└── mapper/                   ← MyBatisPlus Mapper
```

## 开发中遇到的问题

### 1. Spring Boot 4.x + MyBatisPlus 版本兼容
IDEA 创建项目时默认用了 Spring Boot 4.0.6，但 MyBatisPlus 3.5.7 还未适配，导致 Mapper Bean 无法注入（`Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required`）。

**解决：** 降级到 Spring Boot 3.3.5，MyBatisPlus 完美兼容。

### 2. JDK 版本太新导致 Lombok 报错
JDK 25 与当前 Lombok 版本不兼容，编译时抛出：
```
java.lang.ExceptionInInitializerError
com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

**解决：** 切换到 JDK 21（LTS 稳定版）。

### 3. 实体类缺少 MyBatisPlus 注解
`@TableName` 和 `@TableId` 必须加上，否则 MyBatisPlus 不知道表名和主键策略。

### 4. @MapperScan 的必要性
虽然每个 Mapper 接口上都加了 `@Mapper`，但某些版本组合下仍扫描不到。在启动类上加 `@MapperScan` 更稳妥。

### 5. 分页查询需要配置拦截器
MyBatisPlus 的分页功能需要注册 `MybatisPlusInterceptor` + `PaginationInnerInterceptor`，否则 `selectPage` 不会生效。

### 6. DBeaver 连接 MySQL 报 "Public Key Retrieval is not allowed"
MySQL 8+ 的新特性，需要在 DBeaver 连接设置的驱动属性中把 `allowPublicKeyRetrieval` 设为 `true`。
