# 培养方案管理系统 - 前端

## 项目简介

本项目是培养方案管理系统的前端应用，面向高校教务处、系主任等角色，提供培养方案的全生命周期管理功能，包括学院管理、专业管理、课程管理、培养方案管理以及版本控制等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.30 | 渐进式 JavaScript 框架 |
| TypeScript | 5.9.3 | JavaScript 的超集，提供类型支持 |
| Vite | 8.0.1 | 下一代前端构建工具 |
| Element Plus | 2.13.6 | Vue 3 组件库 |
| Pinia | 3.0.4 | Vue 3 状态管理库 |
| Vue Router | 5.0.4 | Vue.js 官方路由 |
| Axios | 1.14.0 | HTTP 客户端 |
| Sass | 1.98.0 | CSS 预处理器 |

## 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口封装
│   │   ├── college.ts     # 学院管理接口
│   │   ├── course.ts      # 课程管理接口
│   │   ├── major.ts       # 专业管理接口
│   │   ├── training-program.ts  # 培养方案接口
│   │   ├── user.ts        # 用户管理接口
│   │   ├── version.ts     # 版本管理接口
│   │   └── sys-dict.ts    # 系统字典接口
│   ├── assets/            # 资源文件
│   │   └── styles/        # 全局样式
│   ├── components/        # 公共组件
│   │   ├── business/      # 业务组件
│   │   ├── common/        # 通用组件
│   │   └── layout/        # 布局组件
│   ├── composables/       # 组合式函数
│   ├── directives/        # 自定义指令
│   ├── router/            # 路由配置
│   ├── stores/            # Pinia 状态管理
│   ├── types/             # TypeScript 类型定义
│   ├── utils/             # 工具函数
│   ├── views/             # 页面组件
│   ├── App.vue            # 根组件
│   └── main.ts            # 入口文件
├── .env.development       # 开发环境变量
├── .env.production        # 生产环境变量
├── index.html             # HTML 模板
├── package.json           # 项目依赖配置
├── tsconfig.json          # TypeScript 配置
└── vite.config.ts         # Vite 配置
```

## 功能模块

### 1. 用户认证
- 用户登录/登出
- 基于角色的权限控制
- 会话管理

### 2. 学院管理
- 学院列表展示
- 学院信息增删改查

### 3. 专业管理
- 专业列表展示
- 专业信息增删改查
- 按学院筛选专业

### 4. 课程管理
- 课程列表展示
- 课程信息增删改查
- 按学院、课程类别筛选

### 5. 培养方案管理
- 培养方案列表
- 培养方案详情
- 课程明细管理
- Excel 导入导出

### 6. 版本管理
- 版本列表展示
- 创建/发布/归档版本
- 版本对比
- 版本回滚

### 7. 系统管理
- 用户管理
- 系统字典管理

## 开发指南

### 环境要求

- Node.js >= 16.0.0（推荐 18.0+）
- npm >= 8.0.0 或 yarn >= 1.22.0

### 安装依赖

```bash
npm install
# 或
yarn install
```

### 开发模式

```bash
npm run dev
# 或
yarn dev
```

访问 http://localhost:5173

### 生产构建

```bash
npm run build
# 或
yarn build
```

### 预览构建结果

```bash
npm run preview
# 或
yarn preview
```

## 环境配置

### 开发环境 (.env.development)

```env
VITE_API_BASE_URL=http://localhost:8090
```

### 生产环境 (.env.production)

```env
VITE_API_BASE_URL=http://your-production-server:8090
```

## 权限控制

系统使用自定义指令 `v-permission` 进行权限控制：

```vue
<el-button v-permission="'100'">仅教务处可见</el-button>
```

权限等级说明：
- `'100'`: 教务处（最高权限）
- `'50'`: 系教务
- `'10'`: 系主任

## API 调用示例

```typescript
import { trainingProgramApi } from '@/api'

// 分页查询培养方案
const { records, total } = await trainingProgramApi.page({
  current: 1,
  size: 10
})

// 获取培养方案详情
const detail = await trainingProgramApi.getDetail(id)
```

## 代码规范

- 使用 TypeScript 编写代码，确保类型安全
- 组件使用 `<script setup>` 语法
- 样式使用 Scoped CSS 或 SCSS
- 遵循 Vue 3 组合式 API 最佳实践

## 相关文档

- [版本管理系统使用手册](../版本管理系统使用手册.md)
- [版本管理系统API文档](../版本管理系统API文档.md)
- [版本管理系统前端组件文档](../版本管理系统前端组件文档.md)
- [后端接口使用指南](../后端接口使用指南.md)

## 更新日志

### 2026-04-13
- 更新项目文档
- 完善版本管理功能
