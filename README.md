# CRM系统部署说明

## 系统概述
这是一套基础版CRM（客户关系管理）系统，专为中小企业设计，包含客户管理、跟进记录管理和基础系统管理功能。系统采用前后端分离架构，后端使用Flask框架，前端使用HTML+CSS+JavaScript，数据存储使用MySQL数据库。

## 技术栈
- **后端**: Python 3.7+, Flask, Flask-SQLAlchemy, PyMySQL
- **前端**: HTML5, CSS3, JavaScript (原生)
- **数据库**: MySQL 5.7+

## 环境要求
1. **Python 3.7+**：确保已安装Python 3.7或更高版本
2. **MySQL 5.7+**：确保已安装MySQL 5.7或更高版本，并能正常运行
3. **pip**：Python包管理工具，用于安装依赖

## 部署步骤

### 步骤1：初始化数据库
1. **启动MySQL服务**
   - Windows：在服务管理器中启动MySQL服务
   - macOS：使用命令 `sudo brew services start mysql` 或通过系统偏好设置启动

2. **导入数据库表结构**
   - 打开MySQL命令行工具或MySQL Workbench
   - 执行以下命令导入SQL文件：
   
   ```sql
   -- 方法1：直接执行SQL文件
   source d:\trae_project\New_CRM\crm_database.sql;
   
   -- 方法2：通过MySQL命令行导入
   mysql -u root -p < d:\trae_project\New_CRM\crm_database.sql
   ```
   
   注意：根据实际MySQL用户名和密码进行调整

3. **验证数据库**
   - 执行以下命令检查数据库是否创建成功：
   
   ```sql
   SHOW DATABASES;
   USE crm_system;
   SHOW TABLES;
   ```
   
   应该能看到 `users`、`customers` 和 `followups` 三个表

### 步骤2：配置后端服务
1. **安装Python依赖**
   - 打开命令行工具，进入项目目录：
   
   ```bash
   cd d:\trae_project\New_CRM
   ```
   
   - 执行以下命令安装依赖：
   
   ```bash
   pip install -r requirements.txt
   ```

2. **修改数据库连接配置**
   - 编辑 `app.py` 文件，修改MySQL连接配置：
   
   ```python
   # 默认配置
   app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://root:root@localhost:3306/crm_system?charset=utf8mb4'
   ```
   
   根据实际的MySQL用户名和密码进行修改，例如：
   - 用户名：root
   - 密码：你的MySQL密码
   - 数据库名：crm_system
   - 端口：3306（默认）

### 步骤3：启动后端服务
1. **运行Flask应用**
   - 在项目目录下执行以下命令：
   
   ```bash
   python app.py
   ```
   
   看到以下输出表示服务启动成功：
   
   ```
   * Serving Flask app "app" (lazy loading)
   * Environment: production
     WARNING: This is a development server. Do not use it in a production deployment.
     Use a production WSGI server instead.
   * Debug mode: on
   * Running on http://0.0.0.0:5000/ (Press CTRL+C to quit)
   * Restarting with stat
   * Debugger is active!
   * Debugger PIN: XXX-XXX-XXX
   ```

### 步骤4：访问前端页面
1. **打开浏览器**
   - 使用Chrome、Firefox、Edge等现代浏览器
   - 访问以下地址：
   
   ```
   http://localhost:5000/login.html
   ```
   
   或者直接打开本地文件：
   
   ```
   file:///d:/trae_project/New_CRM/login.html
   ```

2. **登录系统**
   - 使用默认管理员账号登录：
     - 用户名：admin
     - 密码：123456
   - 首次登录后请修改密码

## 系统功能

### 1. 客户管理
- **添加客户**：填写客户基本信息（姓名、手机号、邮箱、公司、职位、来源、备注）
- **客户列表**：分页展示所有客户，支持按姓名、手机号、来源筛选
- **客户详情**：查看、编辑、删除客户信息
- **跟进记录**：查看客户关联的跟进记录

### 2. 跟进记录管理
- **添加跟进记录**：选择客户，填写跟进时间、跟进方式、跟进内容、下次跟进提醒
- **跟进记录列表**：按客户筛选，查看历史跟进轨迹
- **删除跟进记录**：删除不需要的跟进记录

### 3. 个人信息管理
- **修改昵称**：更新用户显示名称
- **修改密码**：修改登录密码
- **查看用户信息**：查看当前登录用户的基本信息

## 常见问题及解决方案

### 1. MySQL连接失败
- **问题**：`sqlalchemy.exc.OperationalError: (pymysql.err.OperationalError) (2003, "Can't connect to MySQL server on 'localhost' ([Errno 111] Connection refused)")`
- **解决方案**：检查MySQL服务是否启动，端口是否正确

### 2. 数据库表不存在
- **问题**：`sqlalchemy.exc.ProgrammingError: (pymysql.err.ProgrammingError) (1146, "Table 'crm_system.users' doesn't exist")`
- **解决方案**：确保已正确导入 `crm_database.sql` 文件，创建了所有必要的表

### 3. 前端无法访问后端API
- **问题**：浏览器控制台显示跨域错误或404错误
- **解决方案**：
  - 确保后端服务正在运行（http://localhost:5000）
  - 检查 `app.py` 中的CORS配置是否正确
  - 确认前端请求的API地址是否正确

### 4. 登录失败
- **问题**：输入正确的用户名和密码但登录失败
- **解决方案**：
  - 检查MySQL中 `users` 表是否有正确的管理员账号
  - 确认密码是否使用MD5加密存储
  - 检查后端服务日志中的错误信息

## 项目结构

```
New_CRM/
├── app.py              # 后端主应用文件
├── crm_database.sql    # 数据库建表SQL文件
├── requirements.txt    # Python依赖文件
├── README.md           # 部署说明文档
├── login.html          # 登录页面
├── index.html          # 主页面
└── css/
    └── style.css       # 样式文件
```

## 安全说明

1. **密码安全**：系统使用MD5对密码进行加密存储，建议在生产环境中使用更安全的加密方式
2. **权限控制**：目前系统仅实现了基本的登录验证，建议在生产环境中添加更细粒度的权限控制
3. **SQL注入防护**：系统使用Flask-SQLAlchemy的参数化查询，避免了SQL注入风险
4. **跨域安全**：系统配置了CORS，允许前端跨域请求，建议在生产环境中限制允许的域名

## 后续优化建议

1. **添加数据导出功能**：支持导出客户列表为Excel文件
2. **添加数据统计功能**：展示客户来源分布、跟进记录统计等
3. **添加用户权限管理**：实现多角色权限控制
4. **优化前端性能**：使用前端框架（如Vue.js）提升用户体验
5. **添加数据备份功能**：定期自动备份数据库

## 联系方式

如有任何问题或建议，欢迎反馈。

---

**注意**：本系统为基础版CRM，适用于中小企业内部使用。在生产环境部署时，建议根据实际需求进行适当的安全加固和功能扩展。
