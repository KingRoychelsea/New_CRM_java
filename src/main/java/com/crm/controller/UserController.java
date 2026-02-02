package com.crm.controller;

import com.crm.model.User;
import com.crm.service.UserService;
import com.crm.util.ResponseUtils;
import com.crm.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器，处理用户相关的HTTP请求
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param loginData 登录数据（包含username和password）
     * @param session HTTP会话
     * @return 登录响应
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData, HttpSession session) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (username == null || password == null) {
            return ResponseUtils.badRequest("用户名和密码不能为空");
        }

        User user = userService.login(username, password);
        if (user != null) {
            // 登录成功，将用户信息存储到会话中
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            
            // 构建响应数据
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 200);
            responseData.put("message", "登录成功");
            
            // 构建用户信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("role", user.getRole());
            
            responseData.put("data", userInfo);
            
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            // 构建错误响应
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 401);
            responseData.put("message", "用户名或密码错误");
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * 用户退出
     * @param session HTTP会话
     * @return 退出响应
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("message", "退出成功");
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 获取当前用户信息
     * @param session HTTP会话
     * @return 用户信息响应
     */
    @GetMapping("/user/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 401);
            responseData.put("message", "请先登录");
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserById(userId);
        if (user != null) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 200);
            responseData.put("message", "操作成功");
            
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("nickname", user.getNickname());
            userInfo.put("role", user.getRole());
            
            responseData.put("data", userInfo);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 404);
            responseData.put("message", "用户不存在");
            return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 修改个人信息
     * @param updateData 更新数据（包含nickname和password）
     * @param session HTTP会话
     * @return 更新响应
     */
    @PostMapping("/user/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, String> updateData, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 401);
            responseData.put("message", "请先登录");
            return new ResponseEntity<>(responseData, HttpStatus.UNAUTHORIZED);
        }

        User user = userService.getUserById(userId);
        if (user != null) {
            // 更新昵称
            if (updateData.containsKey("nickname")) {
                user.setNickname(updateData.get("nickname"));
            }

            // 更新密码
            if (updateData.containsKey("password") && updateData.get("password") != null && !updateData.get("password").isEmpty()) {
                user.setPassword(PasswordUtils.encryptPassword(updateData.get("password")));
            }

            userService.updateUser(user);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 200);
            responseData.put("message", "更新成功");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("code", 404);
            responseData.put("message", "用户不存在");
            return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * 获取用户列表（仅管理员）
     * @param session HTTP会话
     * @return 用户列表响应
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        User currentUser = userService.getUserById(userId);
        if (!"admin".equals(currentUser.getRole())) {
            return ResponseUtils.forbidden("权限不足");
        }

        List<User> users = userService.getAllUsers();
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("message", "操作成功");
        responseData.put("data", users);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 添加用户（仅管理员）
     * @param userData 用户数据
     * @param session HTTP会话
     * @return 添加响应
     */
    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, String> userData, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        User currentUser = userService.getUserById(userId);
        if (!"admin".equals(currentUser.getRole())) {
            return ResponseUtils.forbidden("权限不足");
        }

        String username = userData.get("username");
        String password = userData.get("password");
        String nickname = userData.get("nickname");
        String role = userData.get("role");

        if (username == null || password == null || nickname == null || role == null) {
            return ResponseUtils.badRequest("用户名、密码、昵称和角色不能为空");
        }

        if (userService.getUserByUsername(username) != null) {
            return ResponseUtils.badRequest("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(PasswordUtils.encryptPassword(password));
        user.setNickname(nickname);
        user.setRole(role);

        userService.addUser(user);
        return ResponseUtils.success("添加成功");
    }

    /**
     * 更新用户信息（仅管理员）
     * @param id 用户ID
     * @param userData 用户数据
     * @param session HTTP会话
     * @return 更新响应
     */
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Integer id, @RequestBody Map<String, String> userData, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        User currentUser = userService.getUserById(userId);
        if (!"admin".equals(currentUser.getRole())) {
            return ResponseUtils.forbidden("权限不足");
        }

        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseUtils.notFound("用户不存在");
        }

        // 更新昵称
        if (userData.containsKey("nickname")) {
            user.setNickname(userData.get("nickname"));
        }

        // 更新角色
        if (userData.containsKey("role")) {
            user.setRole(userData.get("role"));
        }

        // 更新密码
        if (userData.containsKey("password") && userData.get("password") != null && !userData.get("password").isEmpty()) {
            user.setPassword(PasswordUtils.encryptPassword(userData.get("password")));
        }

        userService.updateUser(user);
        return ResponseUtils.success("更新成功");
    }

    /**
     * 删除用户（仅管理员）
     * @param id 用户ID
     * @param session HTTP会话
     * @return 删除响应
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Integer id, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        User currentUser = userService.getUserById(userId);
        if (!"admin".equals(currentUser.getRole())) {
            return ResponseUtils.forbidden("权限不足");
        }

        // 不能删除自己
        if (id.equals(userId)) {
            return ResponseUtils.badRequest("不能删除自己");
        }

        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            return ResponseUtils.success("删除成功");
        } else {
            return ResponseUtils.notFound("用户不存在");
        }
    }

}
