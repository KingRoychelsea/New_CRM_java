# CRM系统部署指南

## 系统概述
这是一套基础版CRM（客户关系管理）系统，专为中小企业设计，包含客户管理、跟进记录管理和基础系统管理功能。

## 数据库连接信息
根据用户提供的信息，数据库连接配置已更新为：
- **主机**：127.0.0.1
- **端口**：3306
- **用户名**：user
- **密码**：612345
- **数据库名**：crm_system

## 部署步骤

### 步骤1：初始化数据库
1. **启动MySQL服务**
   - Windows：在服务管理器中启动MySQL服务
   - macOS：使用命令 `sudo brew services start mysql` 或通过系统偏好设置启动

2. **导入数据库表结构**
   - 打开MySQL命令行工具或MySQL Workbench
   - 使用以下命令登录MySQL（输入密码时输入 `612345`）：
   
   ```bash
   mysql -u user -p
   ```
   
   - 执行以下命令导入SQL文件：
   
   ```sql
   -- 方法1：直接执行SQL文件
   source d:\trae_project\New_CRM\crm_database.sql;
   ```
   
   或者通过命令行直接导入：
   
   ```bash
   mysql -u user -p crm_system < d:\trae_project\New_CRM\crm_database.sql
   ```

3. **验证数据库**
   - 执行以下命令检查数据库是否创建成功：
   
   ```sql
   SHOW DATABASES;
   USE crm_system;
   SHOW TABLES;
   ```
   
   应该能看到 `users`、`customers` 和 `followups` 三个表

### 步骤2：构建和运行Java后端服务
1. **打开命令行工具**，进入项目目录：
   
   ```bash
   cd d:\trae_project\New_CRM
   ```
   
2. **执行Maven构建命令**（确保已安装Maven）：
   
   ```bash
   mvn clean package
   ```
   
   看到以下输出表示构建成功：
   
   ```
   [INFO] BUILD SUCCESS
   [INFO] ------------------------------------------------------------------------
   [INFO] Total time:  10.543 s
   [INFO] Finished at: 2024-07-01T12:00:00+08:00
   [INFO] ------------------------------------------------------------------------
   ```

3. **启动Java后端服务**：
   
   ```bash
   java -jar target/crm-system-0.0.1-SNAPSHOT.jar
   ```
   
   看到以下输出表示服务启动成功：
   
   ```
   .   ____          _            __ _ _
   /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
   ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
   \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
   '  |____| .__|_| |_|_| |_\__, | / / / /
   =========|_|==============|___/=/_/_/_/
   :: Spring Boot ::        (v2.7.0)

   2024-07-01 12:00:00.000  INFO 12345 --- [           main] com.crm.CrmSystemApplication            : Starting CrmSystemApplication using Java 11.0.15 on DESKTOP-XXXX with PID 12345 (D:\trae_project\New_CRM\target\crm-system-0.0.1-SNAPSHOT.jar started by user in D:\trae_project\New_CRM)
   2024-07-01 12:00:00.001  INFO 12345 --- [           main] com.crm.CrmSystemApplication            : No active profile set, falling back to default profiles: default
   2024-07-01 12:00:01.234  INFO 12345 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
   2024-07-01 12:00:01.245  INFO 12345 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
   2024-07-01 12:00:01.246  INFO 12345 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet engine: [Apache Tomcat/9.0.63]
   2024-07-01 12:00:01.356  INFO 12345 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
   2024-07-01 12:00:01.367  INFO 12345 --- [           main] com.crm.CrmSystemApplication            : Started CrmSystemApplication in 1.5 seconds (JVM running for 1.8)
   ```

### 步骤3：访问前端页面
1. **打开浏览器**
   - 使用Chrome、Firefox、Edge等现代浏览器
   - 直接打开本地文件：
   
   ```
   file:///d:/trae_project/New_CRM/login.html
   ```
   
   或者通过后端服务访问：
   
   ```
   http://localhost:8080/login.html
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
- **问题**：`sqlalchemy.exc.OperationalError: (pymysql.err.OperationalError) (2003, "Can't connect to MySQL server on '127.0.0.1' ([Errno 111] Connection refused)")`
- **解决方案**：检查MySQL服务是否启动，端口是否正确

### 2. 数据库权限不足
- **问题**：`Access denied for user 'user'@'localhost' to database 'crm_system'`
- **解决方案**：确保MySQL用户 `user` 有创建和操作数据库的权限

### 3. 前端无法访问后端API
- **问题**：浏览器控制台显示跨域错误或404错误
- **解决方案**：
  - 确保后端服务正在运行（http://localhost:8080）
  - 检查浏览器控制台的错误信息
  - 确认前端请求的API地址是否正确（应为 http://localhost:8080/api）

### 4. 登录失败
- **问题**：输入正确的用户名和密码但登录失败
- **解决方案**：
  - 检查MySQL中 `users` 表是否有正确的管理员账号
  - 确认密码是否使用MD5加密存储
  - 检查后端服务日志中的错误信息
  - 确保Spring Boot应用启动时没有报错

## 技术支持

如果在部署过程中遇到任何问题，请按照以下步骤排查：

1. **检查MySQL服务是否正常运行**
   - 确保MySQL服务已启动
   - 验证数据库连接参数是否正确

2. **检查Java环境**
   - 确保已安装JDK 8或更高版本
   - 确保Maven已正确安装并配置环境变量

3. **查看后端服务日志中的错误信息**
   - Spring Boot启动时的日志会显示详细的错误信息
   - 常见错误包括：数据库连接失败、端口被占用、依赖缺失等

4. **检查浏览器控制台的网络请求和错误信息**
   - 查看API请求是否成功
   - 检查是否有跨域错误
   - 确认前端请求的API地址是否正确

5. **常见Java相关问题**
   - **端口被占用**：使用 `netstat -ano | findstr :8080` 查看占用端口的进程，然后结束该进程
   - **Maven构建失败**：检查网络连接，确保依赖可以正常下载
   - **数据库连接失败**：检查MySQL服务状态和连接参数

系统已配置为使用您提供的数据库连接信息，按照上述步骤操作即可顺利部署和运行。
