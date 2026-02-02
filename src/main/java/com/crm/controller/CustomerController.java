package com.crm.controller;

import com.crm.model.Customer;
import com.crm.service.CustomerService;
import com.crm.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户控制器，处理客户相关的HTTP请求
 */
@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 获取客户列表，支持分页和筛选
     * @param page 页码（默认1）
     * @param limit 每页数量（默认10）
     * @param name 客户姓名（模糊查询）
     * @param phone 客户手机号（模糊查询）
     * @param source 客户来源
     * @param session HTTP会话
     * @return 客户列表响应
     */
    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomers(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "source", required = false) String source,
            HttpSession session) {

        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        // 获取客户列表
        Page<Customer> customerPage = customerService.getCustomers(page, limit, name, phone, source);

        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("message", "操作成功");
        responseData.put("data", customerPage.getContent());
        responseData.put("total", customerPage.getTotalElements());
        responseData.put("page", page);
        responseData.put("limit", limit);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 添加客户
     * @param customerData 客户数据
     * @param session HTTP会话
     * @return 添加响应
     */
    @PostMapping("/customers")
    public ResponseEntity<Map<String, Object>> addCustomer(@RequestBody Map<String, Object> customerData, HttpSession session) {
        // 检查登录状态
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        // 构建客户对象
        Customer customer = new Customer();
        customer.setName((String) customerData.get("name"));
        customer.setPhone((String) customerData.get("phone"));
        customer.setEmail((String) customerData.get("email"));
        customer.setCompany((String) customerData.get("company"));
        customer.setPosition((String) customerData.get("position"));
        customer.setSource((String) customerData.get("source"));
        customer.setNotes((String) customerData.get("notes"));
        customer.setCreatedBy(userId);

        // 验证必填字段
        if (customer.getName() == null || customer.getPhone() == null) {
            return ResponseUtils.badRequest("姓名和手机号不能为空");
        }

        // 添加客户
        Customer savedCustomer = customerService.addCustomer(customer);

        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", savedCustomer.getId());

        return ResponseUtils.success("添加成功", responseData);
    }

    /**
     * 获取客户详情
     * @param id 客户ID
     * @param session HTTP会话
     * @return 客户详情响应
     */
    @GetMapping("/customers/{id}")
    public ResponseEntity<Map<String, Object>> getCustomer(@PathVariable Integer id, HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        // 获取客户信息
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ResponseUtils.notFound("客户不存在");
        }

        return ResponseUtils.success(customer);
    }

    /**
     * 更新客户信息
     * @param id 客户ID
     * @param customerData 客户数据
     * @param session HTTP会话
     * @return 更新响应
     */
    @PutMapping("/customers/{id}")
    public ResponseEntity<Map<String, Object>> updateCustomer(@PathVariable Integer id, @RequestBody Map<String, Object> customerData, HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        // 获取客户信息
        Customer customer = customerService.getCustomerById(id);
        if (customer == null) {
            return ResponseUtils.notFound("客户不存在");
        }

        // 更新客户字段
        if (customerData.containsKey("name")) {
            customer.setName((String) customerData.get("name"));
        }
        if (customerData.containsKey("phone")) {
            customer.setPhone((String) customerData.get("phone"));
        }
        if (customerData.containsKey("email")) {
            customer.setEmail((String) customerData.get("email"));
        }
        if (customerData.containsKey("company")) {
            customer.setCompany((String) customerData.get("company"));
        }
        if (customerData.containsKey("position")) {
            customer.setPosition((String) customerData.get("position"));
        }
        if (customerData.containsKey("source")) {
            customer.setSource((String) customerData.get("source"));
        }
        if (customerData.containsKey("notes")) {
            customer.setNotes((String) customerData.get("notes"));
        }

        // 验证必填字段
        if (customer.getName() == null || customer.getPhone() == null) {
            return ResponseUtils.badRequest("姓名和手机号不能为空");
        }

        // 更新客户
        customerService.updateCustomer(customer);

        return ResponseUtils.success("更新成功");
    }

    /**
     * 删除客户
     * @param id 客户ID
     * @param session HTTP会话
     * @return 删除响应
     */
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable Integer id, HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        // 删除客户
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            return ResponseUtils.success("删除成功");
        } else {
            return ResponseUtils.notFound("客户不存在");
        }
    }

}
