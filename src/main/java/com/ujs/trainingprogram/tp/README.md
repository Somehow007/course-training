# 培养方案管理系统 - 后端

## 项目简介

本项目是培养方案管理系统的后端服务，面向高校教务处、系主任等角色，提供培养方案的全生命周期管理功能，包括学院管理、专业管理、课程管理、培养方案管理以及版本控制等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.0.7 | 应用框架 |
| Spring Security | 6.x | 安全框架 |
| MyBatis-Plus | 3.5.7 | ORM框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存/Session存储 |
| Redisson | 3.27.2 | Redis客户端 |
| EasyExcel | 4.0.1 | Excel处理 |
| FastJSON2 | 2.0.36 | JSON处理 |
| Hutool | 5.8.27 | 工具库 |
| Knife4j | 4.5.0 | API文档 |

## 项目结构

```
src/main/java/com/ujs/trainingprogram/tp/
├── authentication/          # 认证注解与切面
│   ├── RequireAuthentication.java
│   └── RequireAuthenticationAspect.java
├── common/                  # 公共模块
│   ├── constant/           # 常量定义
│   ├── database/           # 数据库基类
│   ├── enums/              # 枚举类
│   ├── errorcode/          # 错误码
│   ├── exception/          # 异常类
│   ├── filter/             # 过滤器
│   ├── handler/            # 处理器
│   ├── result/             # 返回结果
│   └── web/                # Web相关
├── config/                  # 配置类
│   ├── CorsConfig.java     # 跨域配置
│   ├── DataBaseConfiguration.java
│   ├── MyJacksonConfig.java
│   ├── SessionConfig.java
│   ├── SwaggerConfiguration.java
│   └── WebSecurityConfig.java
├── controller/              # 控制器
│   ├── CollegeController.java
│   ├── CourseController.java
│   ├── CourseExclusivityController.java
│   ├── ExcelController.java
│   ├── MajorController.java
│   ├── SysDictController.java
│   ├── TrainingProgramController.java
│   ├── UserController.java
│   ├── ValidateCodeController.java
│   └── VersionController.java
├── dao/                     # 数据访问层
│   ├── entity/             # 实体类
│   │   ├── CollegeDO.java
│   │   ├── CourseDO.java
│   │   ├── MajorDO.java
│   │   ├── TrainingProgramDO.java
│   │   ├── TrainingProgramDetailDO.java
│   │   ├── UserDO.java
│   │   ├── VersionHistoryDO.java
│   │   └── ...
│   └── mapper/             # Mapper接口
├── dto/                     # 数据传输对象
│   ├── req/                # 请求DTO
│   └── resp/               # 响应DTO
├── excel/                   # Excel相关
│   ├── converter/          # 转换器
│   ├── listener/           # 监听器
│   ├── model/              # 模型
│   └── template/           # 模板
├── security/                # 安全相关
│   ├── CustomUserDetailsService.java
│   └── SecurityUserDetails.java
├── service/                 # 服务层
│   ├── impl/               # 服务实现
│   └── *.java              # 服务接口
├── utils/                   # 工具类
│   ├── ExcelExportUtils.java
│   ├── LoadCacheUtils.java
│   └── SecurityUtils.java
└── TpApplication.java       # 启动类
```

## 功能模块

### 1. 用户认证模块
- 用户登录/登出
- 基于角色的权限控制（教务处、系主任、系教务）
- Session管理（Redis存储）
- 验证码生成

### 2. 学院管理模块
- 学院信息增删改查
- 学院下专业/课程统计

### 3. 专业管理模块
- 专业信息增删改查
- 专业分类管理（工学、理学、文科）

### 4. 课程管理模块
- 课程信息增删改查
- 课程类别/性质/类型管理
- 批量删除

### 5. 培养方案管理模块
- 培养方案创建与管理
- 课程明细管理
- Excel导入导出
- 版本控制

### 6. 版本管理模块
- 版本创建/发布/归档
- 版本对比
- 版本回滚
- 变更日志

### 7. 系统管理模块
- 用户管理
- 系统字典管理

## API文档

启动项目后访问：`http://localhost:8090/doc.html`

## 开发指南

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 本地开发

1. **克隆项目**
```bash
git clone <repository-url>
cd course-training
```

2. **配置数据库**
```sql
CREATE DATABASE course_training CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **执行数据库脚本**
```bash
mysql -u root -p course_training < course_training.sql
```

4. **修改配置文件**

编辑 `src/main/resources/application.yaml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/course_training
    username: your-username
    password: your-password
  data:
    redis:
      host: localhost
      port: 6379
```

5. **启动项目**
```bash
mvn spring-boot:run
```

### 编译打包

```bash
mvn clean package -DskipTests
```

生成的jar包位于 `target/tp-0.0.1-SNAPSHOT.jar`

## 权限控制

系统使用自定义注解 `@RequireAuthentication` 进行权限控制：

```java
@RequireAuthentication(AuthConstant.ACADEMIC_AFFAIRS_STAFF)
@PostMapping("/create")
public Result<Void> createVersion(@RequestBody VersionCreateReqDTO requestParam) {
    // ...
}
```

权限等级说明：
- `ACADEMIC_AFFAIRS_STAFF = "100"`: 教务处（最高权限）
- `DEPARTMENTAL_ACADEMIC_AFFAIRS = "50"`: 系教务
- `DEPARTMENT_CHAIR = "10"`: 系主任

## 统一返回结构

所有接口统一返回 `Result<T>` 结构：

```json
{
  "code": "0",
  "message": null,
  "data": {},
  "requestId": null
}
```

- `code = "0"`: 成功
- `code != "0"`: 失败，具体错误码见错误码说明

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 0 | 成功 |
| AUTHENTICATION_ERROR | 未登录或登录状态失效 |
| ACCESS_DENIED_ERROR | 权限不足 |
| CLIENT_ERROR | 客户端参数错误 |
| SERVICE_ERROR | 服务器内部错误 |

## 数据库设计

### 表结构概览

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| college | 学院表 | id, college_code, college_name |
| major | 专业表 | id, major_code, major_name, college_id, category |
| course | 课程表 | id, course_name, college_id, course_nature, dict_id |
| training_program | 培养方案表 | id, name, major_id, college_id, current_version |
| training_program_detail | 培养方案明细表 | id, training_program_id, course_id, total_credits, term |
| version_history | 版本历史表 | id, training_program_id, version_number, snapshot_data |
| version_change_log | 版本变更记录表 | id, version_id, change_type, old_value, new_value |
| course_exclusivity | 课程分组表 | id, group_code, training_program_id, required_credits |
| course_exclusivity_detail | 课程分组详情表 | id, course_id, exclusivity_id |
| sys_dict | 系统字典表 | id, dict_type, dict_code, dict_name |
| user | 用户表 | id, username, password, college_id, dict_id |
| operation_log | 操作日志表 | id, operator_id, operation_type, target_user_id |

### 表关系图

```
college (学院)
    │
    ├── major (专业) [college_id]
    │       │
    │       └── training_program (培养方案) [major_id, college_id]
    │               │
    │               ├── training_program_detail (培养方案明细) [training_program_id]
    │               │
    │               ├── version_history (版本历史) [training_program_id]
    │               │       │
    │               │       └── version_change_log (版本变更记录) [version_id]
    │               │
    │               └── course_exclusivity (课程分组) [training_program_id]
    │                       │
    │                       └── course_exclusivity_detail (分组详情) [exclusivity_id]
    │
    ├── course (课程) [college_id]
    │
    └── user (用户) [college_id]
```

### 核心表详细说明

#### 1. college（学院表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键（Snowflake） |
| college_code | char(2) | 学院编号 |
| college_name | varchar(40) | 学院名称 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |
| del_flag | tinyint | 删除标识（0:正常 1:删除） |

#### 2. major（专业表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| major_code | char(4) | 专业代码 |
| major_name | varchar(40) | 专业名称 |
| college_id | bigint | 所属学院ID |
| course_num | int | 课程总数 |
| category | tinyint | 专业分类（0:工学 1:理学 2:文科） |

#### 3. course（课程表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| course_code | varchar(20) | 课程编码 |
| course_name | varchar(30) | 课程名 |
| college_id | bigint | 开课学院 |
| course_nature | tinyint | 课程性质（0:必修 1:选修） |
| dict_id | bigint | 课程类别关联ID |

#### 4. training_program（培养方案表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| name | varchar(255) | 培养方案名称 |
| major_id | bigint | 专业ID |
| college_id | bigint | 学院ID |
| year | int | 年份 |
| current_version | int | 当前版本号 |
| version_status | tinyint | 版本状态（0:草稿 1:已发布） |
| last_version_id | bigint | 最新版本ID |

#### 5. training_program_detail（培养方案明细表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| training_program_id | bigint | 培养方案ID |
| course_id | bigint | 课程ID |
| course_nature | tinyint | 课程性质 |
| total_credits | float | 总学分 |
| total_hours | float | 总学时（小时） |
| total_weeks | float | 总学时（周） |
| hours_unit | tinyint | 学时单位（0:小时 1:周） |
| term | tinyint | 建议修读学期 |
| version | int | 版本号 |

#### 6. version_history（版本历史表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| training_program_id | bigint | 培养方案ID |
| version_number | int | 版本号 |
| version_name | varchar(100) | 版本名称 |
| version_status | tinyint | 版本状态（0:草稿 1:已发布 2:已归档 3:已回滚） |
| snapshot_data | longtext | 版本快照数据（JSON） |
| creator_id | bigint | 创建人ID |
| creator_name | varchar(50) | 创建人姓名 |

#### 7. sys_dict（系统字典表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| dict_type | varchar(50) | 字典类型 |
| dict_code | varchar(50) | 字典编码 |
| dict_name | varchar(100) | 字典名称 |
| sort_order | int | 排序号 |

**字典类型说明**：
- `course_type`: 课程类型（通识教育、学科专业基础、专业课程、实验实践环节、进阶研学）
- `user_state`: 用户身份（教务处人员、系教务、系主任）

## 相关文档

- [项目使用指南](../项目使用指南.md)
- [版本管理系统API文档](../版本管理系统API文档.md)
- [后端接口使用指南](../后端接口使用指南.md)

## 更新日志

### 2026-04-13
- 创建后端README文档
- 完善项目结构说明
- 根据实际SQL更新数据库设计
