# bigdataPqP 项目代码审查报告

> 审查时间：2026-05-30
> 项目：bigdataPqP（大数据应用刷题平台后端）
> 技术栈：Spring Boot 3.5.14 + MyBatis + Spring Security + JWT + MySQL

---

## 一、🔴 安全问题（优先级最高）

### 1.1 JWT 密钥硬编码在源码中

**文件：** `src/main/java/com/itfan/common/constant/Constants.java`

```java
public static final String USER_LOGIN_KEY = "Y1UPd1tWr/uQbfdd2l10hBmPQyVji1BawX1LHdYBCLM=";
```

**问题：** 密钥直接写在代码里，一旦代码泄露（比如推到 GitHub），任何人都能伪造 JWT Token。

**建议：** 移到 `application.yaml` 中，通过 `@Value` 注入，且不要提交到版本控制：

```yaml
jwt:
  secret: ${JWT_SECRET:Y1UPd1tWr/uQbfdd2l10hBmPQyVji1BawX1LHdYBCLM=}
  expiration: 43200000
```

---

### 1.2 数据库密码硬编码

**文件：** `src/main/resources/application.yaml`

```yaml
username: root
password: root
```

**问题：** root 用户 + 简单密码 + 硬编码，三重风险。生产环境应使用环境变量或配置中心。

**建议：**

```yaml
spring:
  datasource:
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
```

---

### 1.3 HS256 密钥长度不足

**文件：** `JwtUtils.java`

当前密钥 `Y1UPd1tWr/uQbfdd2l10hBmPQyVji1BawX1LHdYBCLM=` 解码后只有 32 字节（256 bit），刚好踩线。建议使用更长的密钥（至少 512 bit / 64 字节）以提高安全性。

---

### 1.4 Token 有效期过长

**文件：** `JwtUtils.java`

```java
public static final long EXPIRATION_TIME = 12 * 60 * 60 * 1000; // 12小时
```

**建议：** 缩短到 2-4 小时，配合 Refresh Token 机制，或前端定期刷新。

---

## 二、🟡 命名规范问题

### 2.1 接口名首字母小写（违反 Java 规范）

| 文件 | 当前 | 应改为 |
|------|------|--------|
| `service/loginService.java` | `loginService` | `LoginService` |
| `service/indexService.java` | `indexService` | `IndexService` |

Java 接口名必须大写开头。当前写法虽然能跑，但 IDE 和代码检查工具会报警告，且与实现类命名冲突（`LoginService` 既是接口又是实现类，靠包名区分，容易混淆）。

---

### 2.2 方法命名不规范

| 文件 | 当前 | 建议 |
|------|------|------|
| `loginService.slogin()` | `slogin`（什么意思？） | `register()` |
| `LoginController.log_in()` | `log_in`（下划线） | `login()` |
| `UserMapper.selectUserNameinfo()` | `驼峰不一致` | `selectUserByUsername()` |
| `UserMapper.selectUserNameHeadImgEmail()` | 太长 | `selectUserBasicInfo()` |
| `UserMapper.insertUserNamePassword()` | 太长 | `insertUser()` |
| `IndexController.OfflineDataProcessing()` | 大写开头+下划线风格 | `getIndex()` 或 `getUserInfo()` |

---

### 2.3 类名不规范

| 文件 | 当前 | 应改为 |
|------|------|--------|
| `pojo/indexInfo.java` | `indexInfo`（小写开头） | `IndexInfo` |

---

### 2.4 字段命名不规范

**文件：** `pojo/User.java`

```java
private String img_head;   // 应为 imgHead（驼峰）
private String Email;      // 应为 email（小写开头）
```

**影响：** Lombok 生成的 getter 会是 `getEmail()` 和 `getImg_head()`，前端传参时 `Email` 大写开头会导致 JSON 序列化/反序列化不匹配（Jackson 默认按字段名映射）。

---

## 三、🟠 代码质量问题

### 3.1 日志拼接方式错误

**文件：** `LoginController.java` 第 45 行

```java
log.info("注册: {}"+user);  // ❌ 字符串拼接，{} 不会被替换
```

**应改为：**

```java
log.info("注册: {}", user);  // ✅ 参数化日志
```

同样的问题在 `IndexController.java` 和 `IndexService.java` 中也存在：

```java
log.info("Index get接口信息"+user);        // ❌
log.info("传到服务这里的用户信息:"+user);    // ❌
log.info("查询后的用户信息:"+searchUserInfo); // ❌
```

---

### 3.2 Controller 层直接接收 Entity

**文件：** `LoginController.java`

```java
public Result log_in(@RequestBody User user)
```

**问题：** 直接用数据库实体接收请求，会暴露内部字段（如 `id`、`create_time`），且无法做参数校验。

**建议：** 创建 DTO：

```java
@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
}
```

---

### 3.3 缺少全局异常处理

项目中没有 `@ControllerAdvice`，如果数据库挂了或出现空指针，前端会收到 500 错误和一堆堆栈信息。

**建议添加：**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常", e);
        return Result.Error("系统繁忙，请稍后重试");
    }
}
```

---

### 3.4 缺少参数校验

Controller 中没有使用 `@Valid` 注解，即使加了校验注解也不会生效。

---

### 3.5 变量名拼写错误

**文件：** `IndexController.java`

```java
private indexService indaxService;  // "indax" 应为 "index"
```

---

### 3.6 方法名与功能不匹配

**文件：** `IndexController.java`

```java
public Result OfflineDataProcessing(User user)  // 这是首页接口，不是"离线数据处理"
```

---

### 3.7 Mapper XML 文件为空

**文件：** `src/main/resources/com/itfan/mapper/UserMapper.xml`

内容为空的 XML 文件，因为 SQL 全用注解写的。这个文件可以删除，避免困惑。

---

### 3.8 UserMapper.xml 路径可能不对

MyBatis 的 XML mapper 文件放在 `src/main/resources/com/itfan/bigdatapqp/` 目录下，但 Mapper 接口在 `com.itfan.mapper` 包，namespace 可能对不上。虽然当前用注解所以没报错，但如果后续要用 XML 写 SQL，路径需要调整。

---

## 四、🟠 逻辑问题

### 4.1 倒计时逻辑有 Bug

**文件：** `IndexService.java`

```java
int year = Year.now().plusYears(1).getValue();
LocalDate endTime = LocalDate.of(year, 4, 16);
Period between = Period.between(today, endTime);
```

**问题：** 永远用的是"明年4月16日"。如果今天是 2026年5月30日，算出来的是 2027年4月16日的倒计时。但如果比赛日期是固定的（比如2026年某月某日），这个逻辑就错了。

**另外：** 如果当前日期超过4月16日，`Period.between()` 会返回负数月份和天数，前端显示会异常。

**建议：** 将截止日期配置化，并处理已过期的情况：

```yaml
competition:
  deadline: 2026-09-15
```

---

### 4.2 登录失败原因不区分

`LoginService.login()` 用户不存在和密码错误都返回 `null`，前端只显示"用户名或密码错误"。虽然安全上可以（防止枚举用户），但建议在日志中区分，方便排查问题。

---

### 4.3 注册接口无密码长度校验

`LoginService.slogin()` 只检查了密码不为 null，没有长度校验（前端有 6 位限制，但后端没有）。

---

## 五、📁 项目结构问题

### 5.1 common/ing_content 目录

```
common/ing_content/Undefined/789f50f32432dd997863cdfa14f03eb7.jpg
```

这个目录名 `Undefined` 看起来是上传逻辑出错时产生的，不应该提交到代码库。建议在 `.gitignore` 中排除 `common/` 目录，或删除这个文件。

---

### 5.2 .idea 目录

虽然 `.gitignore` 已经排除了 `.idea`，但目录还在项目中（可能是先提交后加的 gitignore）。里面包含数据库连接信息（`dataSources.local.xml`），如果推到远程仓库会泄露凭据。

---

## 六、📋 改进优先级建议

| 优先级 | 问题 | 影响 |
|--------|------|------|
| P0 | JWT 密钥硬编码 | 安全漏洞，Token 可被伪造 |
| P0 | 数据库密码硬编码 | 安全漏洞 |
| P0 | .idea 目录含数据库凭据 | 泄露风险 |
| P1 | 接口名小写开头 | 代码规范，团队协作障碍 |
| P1 | 字段命名 Email/img_head | 序列化问题 |
| P1 | 倒计时逻辑 Bug | 功能错误 |
| P1 | 缺少全局异常处理 | 用户体验差 |
| P2 | 方法命名不规范 | 可维护性 |
| P2 | 日志拼接方式 | 排查问题困难 |
| P2 | 缺少 DTO 和参数校验 | 安全+健壮性 |
| P3 | 空的 XML 文件 | 整洁度 |
| P3 | common/ing_content 目录 | 整洁度 |

---

## 七、✅ 做得好的地方

1. **Spring Security + JWT 架构选型正确** — 无状态认证，适合前后端分离
2. **BCrypt 密码加密** — 注册时正确使用了 `passwordEncoder.encode()`
3. **Vite 代理配置合理** — 前端开发体验好
4. **Result 统一响应封装** — 格式规范
5. **密码比对使用 `passwordEncoder.matches()`** — 没有明文比对
