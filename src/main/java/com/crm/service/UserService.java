package com.crm.service;

import com.crm.model.User;
import com.crm.repository.UserRepository;
import com.crm.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务类，处理用户认证相关的业务逻辑
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户登录验证
     * @param username 用户名
     * @param password 密码
     * @return 登录成功的用户对象，失败则返回null
     */
    public User login(String username, String password) {
        System.out.println("登录请求：username=" + username + ", password=" + password);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            System.out.println("找到用户：id=" + user.getId() + ", username=" + user.getUsername());
            System.out.println("存储的密码：" + user.getPassword());
            System.out.println("输入密码加密后：" + PasswordUtils.encryptPassword(password));
            System.out.println("密码是否匹配：" + PasswordUtils.verifyPassword(password, user.getPassword()));
            if (PasswordUtils.verifyPassword(password, user.getPassword())) {
                return user;
            }
        } else {
            System.out.println("未找到用户：" + username);
        }
        return null;
    }

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 用户对象
     */
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户对象
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    public boolean changePassword(Integer userId, String newPassword) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setPassword(PasswordUtils.encryptPassword(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /**
     * 获取所有用户
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 添加用户
     * @param user 用户对象
     * @return 添加的用户对象
     */
    public User addUser(User user) {
        return userRepository.save(user);
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    public boolean deleteUser(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 创建默认管理员账号（如果不存在）
     */
    public void createDefaultAdmin() {
        User admin = userRepository.findByUsername("admin");
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setPassword(PasswordUtils.encryptPassword("123456"));
            admin.setNickname("系统管理员");
            admin.setRole("admin");
            userRepository.save(admin);
            System.out.println("默认管理员账号创建成功: admin / 123456");
        } else {
            // 重置管理员密码为123456
            admin.setPassword(PasswordUtils.encryptPassword("123456"));
            userRepository.save(admin);
            System.out.println("管理员账号密码已重置: admin / 123456");
        }
    }

}
