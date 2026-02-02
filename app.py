# Flask应用主文件
# 配置MySQL连接、应用初始化、路由注册

from flask import Flask, request, jsonify, session, send_from_directory
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
import pymysql
import hashlib
import os

# 配置MySQL连接
# 注意：根据本地MySQL配置修改用户名和密码
app = Flask(__name__)
app.config['SECRET_KEY'] = 'crm_secret_key_2026'
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql+pymysql://user:612345@127.0.0.1:3306/crm_system?charset=utf8mb4'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

# 初始化CORS，允许跨域请求
CORS(app)

# 初始化SQLAlchemy
# 修复pymysql连接问题
pymysql.install_as_MySQLdb()
db = SQLAlchemy(app)

# 数据库模型定义
# 1. 用户模型
class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.String(50), unique=True, nullable=False)
    password = db.Column(db.String(100), nullable=False)
    nickname = db.Column(db.String(50), nullable=False)
    role = db.Column(db.String(20), default='user')
    created_at = db.Column(db.DateTime, default=db.func.current_timestamp())
    updated_at = db.Column(db.DateTime, default=db.func.current_timestamp(), onupdate=db.func.current_timestamp())

# 2. 客户模型
class Customer(db.Model):
    __tablename__ = 'customers'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    name = db.Column(db.String(50), nullable=False)
    phone = db.Column(db.String(20), nullable=False)
    email = db.Column(db.String(100))
    company = db.Column(db.String(100))
    position = db.Column(db.String(50))
    source = db.Column(db.String(50))
    notes = db.Column(db.Text)
    created_by = db.Column(db.Integer, db.ForeignKey('users.id', ondelete='SET NULL'))
    created_at = db.Column(db.DateTime, default=db.func.current_timestamp())
    updated_at = db.Column(db.DateTime, default=db.func.current_timestamp(), onupdate=db.func.current_timestamp())

# 3. 跟进记录模型
class Followup(db.Model):
    __tablename__ = 'followups'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    customer_id = db.Column(db.Integer, db.ForeignKey('customers.id', ondelete='CASCADE'), nullable=False)
    user_id = db.Column(db.Integer, db.ForeignKey('users.id', ondelete='CASCADE'), nullable=False)
    follow_time = db.Column(db.DateTime, nullable=False)
    follow_method = db.Column(db.String(20), nullable=False)
    content = db.Column(db.Text, nullable=False)
    next_follow_reminder = db.Column(db.DateTime)
    created_at = db.Column(db.DateTime, default=db.func.current_timestamp())
    updated_at = db.Column(db.DateTime, default=db.func.current_timestamp(), onupdate=db.func.current_timestamp())

# 工具函数
# 密码加密函数
def encrypt_password(password):
    return hashlib.md5(password.encode('utf-8')).hexdigest()

# 登录验证装饰器
def login_required(func):
    def wrapper(*args, **kwargs):
        if 'user_id' not in session:
            return jsonify({'code': 401, 'message': '请先登录'}), 401
        return func(*args, **kwargs)
    wrapper.__name__ = func.__name__
    return wrapper

# 路由定义
# 1. 用户登录
@app.route('/api/login', methods=['POST'])
def login():
    try:
        data = request.get_json()
        username = data.get('username')
        password = data.get('password')
        
        # 参数校验
        if not username or not password:
            return jsonify({'code': 400, 'message': '用户名和密码不能为空'}), 400
        
        # 查询用户
        user = User.query.filter_by(username=username).first()
        if not user:
            return jsonify({'code': 401, 'message': '用户名或密码错误'}), 401
        
        # 验证密码
        if user.password != encrypt_password(password):
            return jsonify({'code': 401, 'message': '用户名或密码错误'}), 401
        
        # 存储用户信息到session
        session['user_id'] = user.id
        session['username'] = user.username
        session['nickname'] = user.nickname
        session['role'] = user.role
        
        return jsonify({'code': 200, 'message': '登录成功', 'data': {
            'id': user.id,
            'username': user.username,
            'nickname': user.nickname,
            'role': user.role
        }})
    except Exception as e:
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 2. 用户退出
@app.route('/api/logout', methods=['POST'])
def logout():
    try:
        session.clear()
        return jsonify({'code': 200, 'message': '退出成功'})
    except Exception as e:
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 3. 获取当前用户信息
@app.route('/api/user/info', methods=['GET'])
@login_required
def get_user_info():
    try:
        user_id = session.get('user_id')
        user = User.query.get(user_id)
        if not user:
            return jsonify({'code': 404, 'message': '用户不存在'}), 404
        
        return jsonify({'code': 200, 'data': {
            'id': user.id,
            'username': user.username,
            'nickname': user.nickname,
            'role': user.role
        }})
    except Exception as e:
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 4. 修改个人信息
@app.route('/api/user/update', methods=['POST'])
@login_required
def update_user():
    try:
        user_id = session.get('user_id')
        data = request.get_json()
        
        user = User.query.get(user_id)
        if not user:
            return jsonify({'code': 404, 'message': '用户不存在'}), 404
        
        # 更新昵称
        if 'nickname' in data:
            user.nickname = data['nickname']
        
        # 更新密码
        if 'password' in data and data['password']:
            user.password = encrypt_password(data['password'])
        
        db.session.commit()
        return jsonify({'code': 200, 'message': '更新成功'})
    except Exception as e:
        db.session.rollback()
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 5. 客户管理 - 获取客户列表
@app.route('/api/customers', methods=['GET'])
@login_required
def get_customers():
    try:
        # 获取查询参数
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 10, type=int)
        name = request.args.get('name')
        phone = request.args.get('phone')
        source = request.args.get('source')
        
        # 构建查询
        query = Customer.query
        
        # 筛选条件
        if name:
            query = query.filter(Customer.name.like(f'%{name}%'))
        if phone:
            query = query.filter(Customer.phone.like(f'%{phone}%'))
        if source:
            query = query.filter(Customer.source == source)
        
        # 排序和分页
        query = query.order_by(Customer.created_at.desc())
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        
        # 构建返回数据
        customers = []
        for customer in pagination.items:
            customers.append({
                'id': customer.id,
                'name': customer.name,
                'phone': customer.phone,
                'email': customer.email,
                'company': customer.company,
                'position': customer.position,
                'source': customer.source,
                'notes': customer.notes,
                'created_by': customer.created_by,
                'created_at': customer.created_at.strftime('%Y-%m-%d %H:%M:%S'),
                'updated_at': customer.updated_at.strftime('%Y-%m-%d %H:%M:%S')
            })
        
        return jsonify({
            'code': 200,
            'data': customers,
            'total': pagination.total,
            'page': page,
            'limit': limit
        })
    except Exception as e:
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 6. 客户管理 - 添加客户
@app.route('/api/customers', methods=['POST'])
@login_required
def add_customer():
    try:
        data = request.get_json()
        user_id = session.get('user_id')
        
        # 参数校验
        if not data.get('name') or not data.get('phone'):
            return jsonify({'code': 400, 'message': '姓名和手机号不能为空'}), 400
        
        # 创建客户
        customer = Customer(
            name=data.get('name'),
            phone=data.get('phone'),
            email=data.get('email'),
            company=data.get('company'),
            position=data.get('position'),
            source=data.get('source'),
            notes=data.get('notes'),
            created_by=user_id
        )
        
        db.session.add(customer)
        db.session.commit()
        
        return jsonify({'code': 200, 'message': '添加成功', 'data': {'id': customer.id}})
    except Exception as e:
        db.session.rollback()
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 7. 客户管理 - 获取客户详情
@app.route('/api/customers/<int:id>', methods=['GET'])
@login_required
def get_customer(id):
    try:
        customer = Customer.query.get(id)
        if not customer:
            return jsonify({'code': 404, 'message': '客户不存在'}), 404
        
        return jsonify({'code': 200, 'data': {
            'id': customer.id,
            'name': customer.name,
            'phone': customer.phone,
            'email': customer.email,
            'company': customer.company,
            'position': customer.position,
            'source': customer.source,
            'notes': customer.notes,
            'created_by': customer.created_by,
            'created_at': customer.created_at.strftime('%Y-%m-%d %H:%M:%S'),
            'updated_at': customer.updated_at.strftime('%Y-%m-%d %H:%M:%S')
        }})
    except Exception as e:
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 8. 客户管理 - 更新客户
@app.route('/api/customers/<int:id>', methods=['PUT'])
@login_required
def update_customer(id):
    try:
        customer = Customer.query.get(id)
        if not customer:
            return jsonify({'code': 404, 'message': '客户不存在'}), 404
        
        data = request.get_json()
        
        # 更新字段
        if 'name' in data:
            customer.name = data['name']
        if 'phone' in data:
            customer.phone = data['phone']
        if 'email' in data:
            customer.email = data['email']
        if 'company' in data:
            customer.company = data['company']
        if 'position' in data:
            customer.position = data['position']
        if 'source' in data:
            customer.source = data['source']
        if 'notes' in data:
            customer.notes = data['notes']
        
        db.session.commit()
        return jsonify({'code': 200, 'message': '更新成功'})
    except Exception as e:
        db.session.rollback()
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 9. 客户管理 - 删除客户
@app.route('/api/customers/<int:id>', methods=['DELETE'])
@login_required
def delete_customer(id):
    try:
        customer = Customer.query.get(id)
        if not customer:
            return jsonify({'code': 404, 'message': '客户不存在'}), 404
        
        db.session.delete(customer)
        db.session.commit()
        return jsonify({'code': 200, 'message': '删除成功'})
    except Exception as e:
        db.session.rollback()
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 10. 跟进记录 - 获取跟进记录列表
@app.route('/api/followups', methods=['GET'])
@login_required
def get_followups():
    try:
        # 获取查询参数
        page = request.args.get('page', 1, type=int)
        limit = request.args.get('limit', 10, type=int)
        customer_id = request.args.get('customer_id', type=int)
        
        # 构建查询
        query = Followup.query
        
        # 筛选条件
        if customer_id:
            query = query.filter(Followup.customer_id == customer_id)
        
        # 排序和分页
        query = query.order_by(Followup.follow_time.desc())
        pagination = query.paginate(page=page, per_page=limit, error_out=False)
        
        # 构建返回数据
        followups = []
        for followup in pagination.items:
            followups.append({
                'id': followup.id,
                'customer_id': followup.customer_id,
                'user_id': followup.user_id,
                'follow_time': followup.follow_time.strftime('%Y-%m-%d %H:%M:%S'),
                'follow_method': followup.follow_method,
                'content': followup.content,
                'next_follow_reminder': followup.next_follow_reminder.strftime('%Y-%m-%d %H:%M:%S') if followup.next_follow_reminder else None,
                'created_at': followup.created_at.strftime('%Y-%m-%d %H:%M:%S')
            })
        
        return jsonify({
            'code': 200,
            'data': followups,
            'total': pagination.total,
            'page': page,
            'limit': limit
        })
    except Exception as e:
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 11. 跟进记录 - 添加跟进记录
@app.route('/api/followups', methods=['POST'])
@login_required
def add_followup():
    try:
        data = request.get_json()
        user_id = session.get('user_id')
        
        # 参数校验
        if not data.get('customer_id') or not data.get('follow_time') or not data.get('follow_method') or not data.get('content'):
            return jsonify({'code': 400, 'message': '客户ID、跟进时间、跟进方式和内容不能为空'}), 400
        
        # 创建跟进记录
        followup = Followup(
            customer_id=data.get('customer_id'),
            user_id=user_id,
            follow_time=data.get('follow_time'),
            follow_method=data.get('follow_method'),
            content=data.get('content'),
            next_follow_reminder=data.get('next_follow_reminder')
        )
        
        db.session.add(followup)
        db.session.commit()
        
        return jsonify({'code': 200, 'message': '添加成功', 'data': {'id': followup.id}})
    except Exception as e:
        db.session.rollback()
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 12. 跟进记录 - 删除跟进记录
@app.route('/api/followups/<int:id>', methods=['DELETE'])
@login_required
def delete_followup(id):
    try:
        followup = Followup.query.get(id)
        if not followup:
            return jsonify({'code': 404, 'message': '跟进记录不存在'}), 404
        
        db.session.delete(followup)
        db.session.commit()
        return jsonify({'code': 200, 'message': '删除成功'})
    except Exception as e:
        db.session.rollback()
        return jsonify({'code': 500, 'message': '服务器错误: ' + str(e)}), 500

# 初始化数据库表结构
# 注意：在生产环境中，应该使用数据库迁移工具
with app.app_context():
    # 先创建数据库（如果不存在）
    from sqlalchemy import create_engine
    import pymysql
    
    # 创建数据库连接（不指定数据库名）
    engine = create_engine('mysql+pymysql://user:612345@127.0.0.1:3306', pool_pre_ping=True)
    
    # 创建数据库
    with engine.connect() as conn:
        conn.execute('CREATE DATABASE IF NOT EXISTS crm_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci')
        conn.execute('USE crm_system')
    
    # 创建表结构
    db.create_all()
    
    # 创建默认管理员账号（如果不存在）
    from sqlalchemy.exc import IntegrityError
    try:
        admin_user = User(
            username='admin',
            password=encrypt_password('123456'),  # 使用MD5加密密码
            nickname='系统管理员',
            role='admin'
        )
        db.session.add(admin_user)
        db.session.commit()
        print('默认管理员账号创建成功')
    except IntegrityError:
        db.session.rollback()
        print('管理员账号已存在')

# 静态文件服务路由
@app.route('/<path:path>')
def static_file(path):
    if path.endswith('.html'):
        return send_from_directory(os.getcwd(), path)
    elif path.startswith('css/'):
        return send_from_directory(os.getcwd(), path)
    return send_from_directory(os.getcwd(), path)

# 默认路由
@app.route('/')
def index():
    return send_from_directory(os.getcwd(), 'login.html')

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=5000)
