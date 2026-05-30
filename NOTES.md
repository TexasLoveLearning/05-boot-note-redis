# 📝 备忘录项目 — 开发笔记

## 项目简介

一个带缓存的备忘录 REST API，前端直连后端。

**技术栈：** Spring Boot 3 + MySQL + MyBatis-Plus + Redis + 原生 HTML/CSS/JS

**GitHub：** https://github.com/TexasLoveLearning/05-boot-note-redis

---

## 🐛 遇到的问题 & 解决方案

### 1. 前端更新不生效

**问题：** 编辑笔记后页面没有更新。

**原因：** 前端 `api()` 函数写死了请求方法：

```javascript
// ❌ 错误代码
method: body ? 'POST' : 'GET'
```

后端更新接口是 `@PutMapping`，但前端发了 `POST`，导致请求被忽略。

**解决：** `api()` 函数增加 `method` 参数，更新时传 `'PUT'`，删除传 `'DELETE'`。

```javascript
async function api(url, body, method) {
    method: method || (body ? 'POST' : 'GET'),
}
```

---

### 2. 前端缺少查询按钮

**问题：** 后端写了带缓存的 `GET /api/notes/{id}`，但前端没有入口去调用它。

**解决：** 为每条笔记增加「👁️ 查看」按钮，点击弹出详情弹窗，调用缓存接口。

---

### 3. RedisTemplate 类型转换错误

```java
// ❌ 编译报错：不可转换的类型
Note note = (Note) redisTemplate.opsForValue().get(key);
```

**原因：** 默认注入的 `RedisTemplate` 是 `<String, String>` 类型，`get()` 返回 `String`，不能强转成 `Note`。

**解决：** 新建 `RedisConfig`，配置 `<String, Object>` 类型 + Jackson JSON 序列化：

```java
@Bean
public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    // 用 Jackson 序列化，对象存成 JSON
    Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
    // ... 配置 ObjectMapper
    template.setValueSerializer(serializer);
    template.setKeySerializer(new StringRedisSerializer());
    return template;
}
```

---

### 4. 对象无法存进 Redis

**问题：** 调用 `redisTemplate.opsForValue().set(key, note)` 时抛异常。

**原因：** JDK 默认序列化要求对象实现 `Serializable`，而 `Note` 和 `User` 实体类没有实现。

**解决：**
```java
public class Note implements Serializable {
    private static final long serialVersionUID = 1L;
    // ...
}
```

同时配合 RedisConfig 改用 JSON 序列化，存进去的是可读的 JSON 字符串。

---

### 5. Docker 拉取 Redis 镜像失败

**问题：** `docker run redis` 超时失败。

**原因：** 国内网络连不上 Docker Hub（registry-1.docker.io 被墙）。

**解决：** 不依赖 Docker，直接在 WSL2 里**编译安装 Redis**：

```bash
curl -L -o redis.tar.gz https://download.redis.io/releases/redis-7.2.5.tar.gz
tar -xzf redis.tar.gz
cd redis-7.2.5 && make -j$(nproc)
cp src/redis-server ~/bin/
cp src/redis-cli ~/bin/
redis-server --daemonize yes
```

或者配置 Docker Desktop 的国内镜像源（`daemon.json` 加 `registry-mirrors`）。

---

## 📚 重要知识点

### Cache-Aside 模式（旁路缓存）

最常用的缓存策略，核心流程：

```
查询：
1. 查 Redis → 有则返回（缓存命中 ✅）
2. 没有 → 查 MySQL → 存进 Redis（设置 TTL）→ 返回

更新/删除：
1. 操作数据库
2. 删除 Redis 中的 key（让下次查询重新加载）
```

**为什么删缓存而不是更新缓存？**
- 删缓存简单可靠，不会出现并发写导致的数据不一致
- 下次查询自动"懒加载"重新填

### RedisTemplate vs StringRedisTemplate

| | RedisTemplate | StringRedisTemplate |
|--|-------------|-------------------|
| 默认序列化 | JDK 序列化（二进制，不可读） | String 序列化 |
| value 类型 | Object | String |
| 适用场景 | 存对象 | 存字符串/数值 |

**实战建议：** 自己配 `RedisTemplate<String, Object>` + Jackson 序列化，对象存成 JSON，可读性好，还能跨语言。

### 缓存常见问题（面试高频）

**缓存穿透：** 查一个不存在的数据（如 id = -1），每次都穿透到数据库。
- 解决：缓存空值（`null` 也存 Redis，TTL 短一些）

**缓存雪崩：** 大量 key 同时过期，请求全部打到数据库。
- 解决：TTL 加随机偏移量（基础 TTL + `random.nextInt(600)`）

**缓存击穿：** 一个热点 key 过期瞬间，大量请求同时打到数据库。
- 解决：互斥锁（只让一个请求去查数据库，其他等待）

### @TableLogic（MyBatis-Plus 逻辑删除）

```java
@TableLogic
private Integer deleted;
// deleted = 0 未删除，deleted = 1 已删除
```

MyBatis-Plus 会**自动在查询条件里加 `WHERE deleted=0`**，删除时执行 `UPDATE SET deleted=1` 而不是 `DELETE FROM`。

### 分页查询

```java
Page<Note> pageObj = new Page<>(page, size);
noteMapper.selectPage(pageObj, null);
// pageObj.getRecords() → 当前页数据
// pageObj.getTotal() → 总数
```

---

## 🔗 项目链接

- **GitHub:** https://github.com/TexasLoveLearning/05-boot-note-redis
- **本地启动:** IDEA 运行 `Application.java`，访问 http://localhost:8080
- **Redis:** 本地 6379 端口，无需密码
- **MySQL:** `note_db` 数据库，表 `user` + `note`
