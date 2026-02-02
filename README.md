# CRM系统部署说明

## 系统概述
这是一套基础版CRM（客户关系管理）系统，专为中小企业设计，包含客户管理、跟进记录管理和基础系统管理功能。系统采用前后端分离架构，后端使用Java Spring Boot框架，前端使用HTML+CSS+JavaScript，数据存储使用MySQL数据库。

## 技术栈
- **后端**: Java 8+, Spring Boot 2.7+, Spring Data JPA, MySQL Connector
- **前端**: HTML5, CSS3, JavaScript (原生)
- **数据库**: MySQL 5.7+
- **构建工具**: Maven 3.8+

## 环境要求
1. **Java 8+**：确保已安装Java 8或更高版本
2. **Maven 3.8+**：确保已安装Maven 3.8或更高版本
3. **MySQL 5.7+**：确保已安装MySQL 5.7或更高版本，并能正常运行

## 部署步骤

### 步骤1：初始化数据库
1. **启动MySQL服务**
   - Windows：在服务管理器中启动MySQL服务
   - macOS：使用命令 `sudo brew services start mysql` 或通过系统偏好设置启动

2. **创建数据库**
   - 打开MySQL命令行工具或MySQL Workbench
   - 执行以下命令创建数据库：
   
   ```sql
   CREATE DATABASE IF NOT EXISTS crm_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **验证数据库**
   - 执行以下命令检查数据库是否创建成功：
   
   ```sql
   SHOW DATABASES;
   ```
   
   应该能看到 `crm_system` 数据库

### 步骤2：配置后端服务
1. **修改数据库连接配置**
   - 编辑 `src/main/resources/application.properties` 文件，修改MySQL连接配置：
   
   ```properties
   # 数据库配置
   spring.datasource.url=jdbc:mysql://127.0.0.1:3306/crm_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
   spring.datasource.username=user
   spring.datasource.password=612345
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ```
   
   根据实际的MySQL用户名和密码进行修改，例如：
   - 用户名：root
   - 密码：你的MySQL密码
   - 数据库名：crm_system
   - 端口：3306（默认）

### 步骤3：构建项目
1. **构建Maven项目**
   - 打开命令行工具，进入项目目录：
   
   ```bash
   cd d:\trae_project\New_CRM
   ```
   
   - 执行以下命令构建项目：
   
   ```bash
   mvn clean package
   ```
   
   看到 `BUILD SUCCESS` 表示构建成功

### 步骤4：启动后端服务
1. **运行Java应用**
   - 在项目目录下执行以下命令：
   
   ```bash
   java -jar target\crm-system-1.0.0.jar
   ```
   
   看到以下输出表示服务启动成功：
   
   ```
   2026-02-02 13:00:07.085  INFO 26000 --- [           main] com.crm.CrmSystemApplication             : Started CrmSystemApplication in 3.653 seconds (JVM running for 3.967)
   ```

### 步骤5：访问前端页面
1. **打开浏览器**
   - 使用Chrome、Firefox、Edge等现代浏览器
   - 访问以下地址：
   
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
- **问题**：`com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure`
- **解决方案**：检查MySQL服务是否启动，端口是否正确，数据库连接配置是否正确

### 2. 数据库表不存在
- **问题**：`org.hibernate.exception.SQLGrammarException: could not extract ResultSet`
- **解决方案**：Spring Boot JPA会自动创建表结构，确保MySQL服务正常运行，数据库连接配置正确

### 3. 前端无法访问后端API
- **问题**：浏览器控制台显示跨域错误或404错误
- **解决方案**：
  - 确保后端服务正在运行（http://localhost:8080）
  - 检查 `CrmSystemApplication.java` 中的CORS配置是否正确
  - 确认前端请求的API地址是否正确

### 4. 登录失败
- **问题**：输入正确的用户名和密码但登录失败
- **解决方案**：
  - 检查应用启动日志，确认管理员账号是否初始化成功
  - 检查MySQL中 `users` 表是否有正确的管理员账号
  - 确认密码是否使用MD5加密存储

## 项目结构

```
New_CRM/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── crm/
│   │   │           ├── config/           # 配置类
│   │   │           ├── controller/       # 控制器
│   │   │           ├── model/            # 实体类
│   │   │           ├── repository/       # 数据访问层
│   │   │           ├── service/          # 服务层
│   │   │           ├── util/             # 工具类
│   │   │           └── CrmSystemApplication.java  # 应用入口
│   │   └── resources/
│   │       ├── static/                   # 静态资源
│   │       │   ├── css/                  # 样式文件
│   │       │   ├── index.html            # 主页面
│   │       │   └── login.html            # 登录页面
│   │       └── application.properties    # 应用配置
│   └── test/
│       └── java/                         # 测试代码
├── pom.xml                               # Maven配置文件
├── README.md                             # 部署说明文档
└── .gitignore                            # Git忽略文件
```

## 安全说明

1. **密码安全**：系统使用MD5对密码进行加密存储，建议在生产环境中使用更安全的加密方式（如BCrypt）
2. **权限控制**：目前系统仅实现了基本的登录验证，建议在生产环境中添加更细粒度的权限控制
3. **SQL注入防护**：系统使用Spring Data JPA的参数化查询，避免了SQL注入风险
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
