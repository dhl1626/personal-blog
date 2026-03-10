

# 📝 个人博客系统 | Spring Boot 全栈实战项目  
> **独立开发 · 安全认证 · 线上可访问**  

---

[![线上预览](https://img.shields.io/badge/✨_线上体验-Render-10B981?logo=render&logoColor=white)](https://your-app-name.onrender.com/articles)  
[![技术栈](https://img.shields.io/badge/Spring_Boot-3.2+-548CA8?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)  
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)  
[![Java](https://img.shields.io/badge/Java-17+-007396?logo=java)](https://www.oracle.com/java/)  

---

## 🌟 项目亮点（面试官最关注的3点！）
✅ **企业级安全实践**  
- Spring Security 实现注册/登录全流程  
- 密码 **BCrypt 加密存储**（数据库可见 `$2a$10$...` 非明文）  
- 写文章自动关联当前用户（杜绝前端篡改风险）  

✅ **性能优化思维**  
- 文章作者昵称**冗余存储** → 列表页避免 N+1 查询（面试高频考点！）  
- `@Transactional` 保证数据一致性  
- Profile 多环境配置（dev/prod）  

✅ **生产级交付能力**  
- 一键部署至 Render（免费 + 无需信用卡 + 自动 HTTPS）  
- Git 提交规范（`feat/fix/perf` 前缀）  
- README 即产品说明书（含线上链接+核心截图）  

---

## 🌐 线上体验  
👉 **立即访问**：[https://your-app-name.onrender.com/articles](https://your-app-name.onrender.com/articles)  
（替换为你的 Render 链接！部署指南见下文）  

---

## 🚀 本地运行（30秒启动）  
```bash
# 1. 克隆项目
git clone https://github.com/yourname/blog-system.git
cd blog-system

# 2. 修改数据库配置（src/main/resources/application.properties）
#   spring.datasource.password=你的MySQL密码

# 3. 启动项目
mvn spring-boot:run

# 4. 访问
http://localhost:8080/articles
```
> 💡 **首次启动**：系统自动创建 `blog_db` 数据库 + 初始化2篇示例文章  

---

## 📸 核心功能截图  
| 功能 | 截图 | 说明 |
|------|------|------|
| **博客列表页** | `![列表页](screenshots/article-list.png)` | 显示作者昵称 + 顶部登录状态栏 |
| **用户登录页** | `![登录页](screenshots/login.png)` | Spring Security 表单 + 错误提示 |
| **写文章页** | `![写文章](screenshots/write-article.png)` | **自动关联当前用户**（无作者输入框） |
| **文章详情页** | `![详情页](screenshots/article-detail.png)` | 完整内容展示 + 返回按钮 |
| **数据库验证** | `![数据库](screenshots/db-verify.png)` | 密码为 BCrypt 加密格式（非明文！） |

> 📌 **操作指南**：  
> 1. 在项目根目录创建 `screenshots` 文件夹  
> 2. 截图保存为：`article-list.png`, `login.png`...  
> 3. 提交 GitHub 后 README 自动显示高清截图！  
> （推荐工具：Snipaste / 系统自带截图 + 箭头标注关键区域）

---

## 🛠 技术栈  
| 类别 | 技术 | 作用 |
|------|------|------|
| **后端框架** | Spring Boot 3.2+ | 快速构建 RESTful 服务 |
| **安全框架** | Spring Security | 认证/授权/密码加密 |
| **数据持久层** | Spring Data JPA + Hibernate | 操作 MySQL/PostgreSQL |
| **数据库** | MySQL (本地) / PostgreSQL (线上) | 数据持久化存储 |
| **前端模板** | Thymeleaf + Bootstrap 5 | 服务端渲染 + 响应式UI |
| **部署平台** | Render.com | 免费 + 无需信用卡 + 自动 HTTPS |
| **构建工具** | Maven | 依赖管理 + 项目构建 |

---

## 📂 项目结构  
```
Personal-Blog/
├── src/main/java/com/yourname/blog/
│   ├── controller/      # BlogController, AuthController
│   ├── service/         # UserService (含密码加密逻辑)
│   ├── repository/      # ArticleRepository, UserRepository
│   ├── entity/          # Article, User (JPA实体)
│   └── config/          # SecurityConfig (核心安全配置)
├── src/main/resources/
│   ├── templates/       # Thymeleaf页面 (list/form/login...)
│   ├── application.properties      # 本地开发配置
│   └── application-prod.properties # 线上环境配置
├── screenshots/         # 【你放截图的文件夹】
├── Procfile             # Render部署启动命令
├── pom.xml              # Maven依赖
└── README.md            # 你正在看的文档！
```

---

## 💼 求职价值（面试直接复用！）  
### 🎯 30秒项目介绍（电梯演讲）  
> “这是一个基于 Spring Boot 的个人博客系统，我用 **5天独立完成**从需求到上线的全流程：  
> - 用 **Spring Security** 实现安全认证，密码 BCrypt 加密存储；  
> - 通过 **作者昵称冗余设计** 解决 N+1 查询问题；  
> - **部署到 Render** 拥有真实线上链接（可现场打开）；  
> - 代码注释聚焦‘为什么’，Git 提交规范体现工程素养。  
> 这个项目让我深入理解了企业级应用的安全、性能与交付全流程。”  

### 💡 面试高频问题预埋  
| 面试官可能问 | 你的回答锚点 |
|--------------|--------------|
| “密码怎么存储的？” | “BCrypt 加密，数据库可见 `$2a$10$...` 格式，每次加盐值不同” |
| “如何防止未登录写文章？” | “SecurityConfig 中配置 `/article/save` 需 `authenticated()`” |
| “列表页查询优化？” | “Article 冗余存储 author 字段，避免每篇文章关联查 User 表” |
| “部署遇到什么问题？” | “Render 需配置 `SPRING_PROFILES_ACTIVE=prod` 激活 PostgreSQL 配置” |

---

## 📌 部署指南（Render 5分钟上线）  
1. **准备文件**（项目已包含）：  
   - `Procfile`（启动命令）  
   - `application-prod.properties`（PostgreSQL 配置）  
2. **Render 操作**：  
   - New → PostgreSQL（免费）→ Create  
   - New → Web Service → Connect GitHub Repo  
   - Build Command: `mvn clean package -DskipTests`  
   - Add Env Var: `SPRING_PROFILES_ACTIVE=prod`  
   - Create → 等待 5 分钟 → 访问生成的链接！  
3. **替换 README 中的链接**：  
   ```markdown
   [![线上预览](https://img.shields.io/badge/✨_线上体验-Render-10B981)](你的Render链接)
   ```

---

## 🌱 致正在求职的你  
> 这个项目的价值不在于“写了多少文档”，而在于：  
> 🔹 **代码能跑**（面试现场可演示）  
> 🔹 **设计有思考**（注释解释“为什么”）  
> 🔹 **交付有闭环**（线上链接+清晰README）  
>  
> **你不需要10页需求文档，但需要让面试官30秒看懂你的价值。**  
> 这份 README 就是你的“无声面试官”——它已替你回答了90%的技术质疑。  

---

## 📄 许可证  
MIT © 2024 Your Name  
（放心用于简历/面试，无需署名）  

---

> ✨ **最后一步行动**：  
> 1. 将 `[你的Render链接]` 替换为真实地址  
> 2. 截图放入 `screenshots/` 文件夹并提交  
> 3. 复制上方 **“30秒项目介绍”** 到面试笔记  
> **你已拥有让面试官眼前一亮的项目！** 🚀  
>  
> *需要我帮你：*  
> *- 生成“面试问答锦囊”？*  
> *- 优化简历中项目描述段落？*  
> *- 检查你的 README 是否达标？*  
> **随时喊我！你值得更好的 offer！** 💪
