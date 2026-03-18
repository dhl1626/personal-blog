# 管理员设置指南

本文档详细说明如何设置和管理系统管理员账户。

---

## 📋 目录

1. [方法一：自动创建默认管理员（推荐）](#方法一自动创建默认管理员推荐)
2. [方法二：通过后台管理页面设置](#方法二通过后台管理页面设置)
3. [方法三：通过数据库直接设置](#方法三通过数据库直接设置)
4. [常见问题](#常见问题)

---

## 方法一：自动创建默认管理员（推荐）

### 说明
系统启动时会自动创建默认管理员账户。

### 默认凭据
- **用户名**：`admin`
- **密码**：`admin123`
- **角色**：`ROLE_ADMIN`

### 使用步骤
1. 启动 Spring Boot 应用
2. 查看控制台输出：
   ```
   ✅ 默认管理员账户创建成功！
      用户名：admin
      密码：admin123
      ⚠️ 请及时修改密码！
   ```
3. 使用默认凭据登录
4. **重要**：登录后立即修改密码！

### 安全性建议
- ✅ 首次登录后立即修改密码
- ✅ 修改默认用户名（可选）
- ❌ 不要在生产环境使用默认密码

---

## 方法二：通过后台管理页面设置

### 前提条件
- 已有一个管理员账户（如默认 admin）
- 已登录管理员账户

### 使用步骤

#### 1. 进入后台管理
- 登录后，点击导航栏的 **🔧 后台管理** 链接
- 或直接访问：`http://localhost:8080/admin`

#### 2. 进入管理员设置页面
- 在左侧菜单点击 **👤 管理员设置**
- 或访问：`http://localhost:8080/admin/promote`

#### 3. 提升用户为管理员
1. 在 **提升用户为管理员** 区域
2. 从下拉列表选择要提升的用户
3. 点击 **提升为管理员** 按钮
4. 系统会显示成功提示

#### 4. 移除管理员身份
1. 在 **当前管理员列表** 区域
2. 找到要移除的管理员
3. 点击 **移除管理员** 按钮
4. 确认后完成移除

### 注意事项
- ⚠️ 不能移除当前登录的管理员身份（防止失去管理权限）
- ⚠️ 至少保留一个管理员账户

---

## 方法三：通过数据库直接设置

### 适用场景
- 首次部署系统
- 无法通过页面设置（如所有管理员账户丢失）

### 数据库表结构
权限管理系统涉及以下表：
| 表名 | 说明 |
|------|------|
| `user` | 用户表 |
| `role` | 角色表 |
| `permission` | 权限表 |
| `user_role` | 用户角色关联表 |
| `role_permission` | 角色权限关联表 |

### SQL 操作步骤

#### 1. 查看现有用户
```sql
SELECT * FROM user;
```

#### 2. 查看现有角色
```sql
SELECT * FROM role;
```

#### 3. 如果 ROLE_ADMIN 不存在，创建它
```sql
INSERT INTO role (name, description) 
VALUES ('ROLE_ADMIN', '管理员');
```

#### 4. 获取用户 ID 和角色 ID
```sql
-- 查看用户 ID
SELECT id, username, nickname FROM user;

-- 查看 ROLE_ADMIN 的 ID
SELECT id FROM role WHERE name = 'ROLE_ADMIN';
```

#### 5. 为用户分配管理员角色
```sql
-- 假设用户 id=1，ROLE_ADMIN 的 id=2
INSERT INTO user_role (user_id, role_id) 
VALUES (1, 2);
```

#### 6. 验证是否成功
```sql
SELECT u.username, u.nickname, r.name as role_name
FROM user u
JOIN user_role ur ON u.id = ur.user_id
JOIN role r ON ur.role_id = r.id;
```

### 完整 SQL 脚本示例
```sql
-- 创建管理员角色（如果不存在）
INSERT INTO role (name, description) 
SELECT 'ROLE_ADMIN', '管理员'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ROLE_ADMIN');

-- 为用户 ID=1 分配管理员角色
INSERT INTO user_role (user_id, role_id)
SELECT 1, (SELECT id FROM role WHERE name = 'ROLE_ADMIN')
WHERE NOT EXISTS (
    SELECT 1 FROM user_role 
    WHERE user_id = 1 
    AND role_id = (SELECT id FROM role WHERE name = 'ROLE_ADMIN')
);
```

---

## 常见问题

### Q1: 忘记管理员密码怎么办？
**A:** 可以通过数据库重置密码：
```sql
-- 将 admin 的密码重置为 admin123
UPDATE user 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lqkkO9QS3TzCjH3rS'
WHERE username = 'admin';
```
> 这是 BCrypt 加密后的 "admin123"

### Q2: 所有管理员账户都丢失了怎么办？
**A:** 通过数据库直接恢复：
```sql
-- 1. 确保 ROLE_ADMIN 角色存在
INSERT INTO role (name, description) 
SELECT 'ROLE_ADMIN', '管理员'
WHERE NOT EXISTS (SELECT 1 FROM role WHERE name = 'ROLE_ADMIN');

-- 2. 为第一个用户分配管理员角色
INSERT INTO user_role (user_id, role_id)
SELECT 
    (SELECT id FROM user ORDER BY id LIMIT 1),
    (SELECT id FROM role WHERE name = 'ROLE_ADMIN')
WHERE NOT EXISTS (
    SELECT 1 FROM user_role ur
    JOIN role r ON ur.role_id = r.id
    WHERE r.name = 'ROLE_ADMIN'
);
```

### Q3: 如何删除默认 admin 账户？
**A:** 
1. 先创建另一个管理员账户
2. 用新管理员账户登录
3. 在 **管理员设置** 页面移除原 admin 的管理员身份
4. 或在用户管理中删除该用户

### Q4: 如何修改管理员密码？
**A:** 
- **方法 1**：在个人中心修改（推荐）
- **方法 2**：通过数据库（应急使用）
  ```sql
  UPDATE user 
  SET password = '新的 BCrypt 加密密码'
  WHERE username = 'admin';
  ```

### Q5: 可以创建自定义角色吗？
**A:** 可以！在 **角色管理** 页面：
1. 输入角色名（如 `ROLE_EDITOR`）
2. 输入描述（如 `编辑`）
3. 点击 **创建**
4. 为该角色分配相应权限

---

## 安全最佳实践

1. ✅ **及时修改默认密码** - 首次登录后立即修改
2. ✅ **最小权限原则** - 只授予必要的权限
3. ✅ **定期审计** - 定期检查管理员列表
4. ✅ **密码强度** - 使用强密码（大小写 + 数字 + 特殊字符）
5. ✅ **限制管理员数量** - 仅信任的用户才能成为管理员
6. ✅ **启用 HTTPS** - 生产环境必须使用 HTTPS

---

## 联系支持

如有问题，请查看：
- 应用日志：`logs/application.log`
- 控制台输出
- Spring Boot 启动日志中的初始化信息
