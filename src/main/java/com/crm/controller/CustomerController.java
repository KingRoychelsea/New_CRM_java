package com.crm.controller;

import com.crm.model.Customer;
import com.crm.service.CustomerService;
import com.crm.service.FollowupService;
import com.crm.util.ResponseUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户控制器，处理客户相关的HTTP请求
 */
@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private FollowupService followupService;

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

    /**
     * 导出客户列表为Excel文件
     * @param name 客户姓名（可选）
     * @param phone 客户手机号（可选）
     * @param source 客户来源（可选）
     * @param session HTTP会话
     * @return Excel文件响应
     */
    @GetMapping("/customers/export")
    public ResponseEntity<byte[]> exportCustomersToExcel(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "phone", required = false) String phone,
            @RequestParam(value = "source", required = false) String source,
            HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            // 获取客户列表
            Page<Customer> customerPage = customerService.getCustomers(1, Integer.MAX_VALUE, name, phone, source);
            List<Customer> customers = customerPage.getContent();

            // 创建Excel工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("客户列表");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("姓名");
            headerRow.createCell(2).setCellValue("手机号");
            headerRow.createCell(3).setCellValue("邮箱");
            headerRow.createCell(4).setCellValue("公司");
            headerRow.createCell(5).setCellValue("职位");
            headerRow.createCell(6).setCellValue("来源");
            headerRow.createCell(7).setCellValue("备注");
            headerRow.createCell(8).setCellValue("创建时间");
            headerRow.createCell(9).setCellValue("创建人");

            // 填充数据
            int rowNum = 1;
            for (Customer customer : customers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(customer.getId());
                row.createCell(1).setCellValue(customer.getName());
                row.createCell(2).setCellValue(customer.getPhone());
                row.createCell(3).setCellValue(customer.getEmail() != null ? customer.getEmail() : "");
                row.createCell(4).setCellValue(customer.getCompany() != null ? customer.getCompany() : "");
                row.createCell(5).setCellValue(customer.getPosition() != null ? customer.getPosition() : "");
                row.createCell(6).setCellValue(customer.getSource() != null ? customer.getSource() : "");
                row.createCell(7).setCellValue(customer.getNotes() != null ? customer.getNotes() : "");
                row.createCell(8).setCellValue(customer.getCreatedAt() != null ? customer.getCreatedAt().toString() : "");
                row.createCell(9).setCellValue(customer.getCreatedBy() != null ? customer.getCreatedBy().toString() : "");
            }

            // 调整列宽
            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            byte[] bytes = outputStream.toByteArray();
            outputStream.close();

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "customers_" + System.currentTimeMillis() + ".xlsx");
            headers.setContentLength(bytes.length);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取客户来源分布统计
     * @param session HTTP会话
     * @return 客户来源分布统计数据
     */
    @GetMapping("/statistics/customer/source")
    public ResponseEntity<Map<String, Object>> getCustomerSourceStatistics(HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Map<String, Long> statistics = customerService.getCustomerSourceStatistics();
            return ResponseUtils.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.serverError("获取客户来源分布统计失败");
        }
    }

    /**
     * 获取客户数量统计（按时间段）
     * @param days 天数
     * @param session HTTP会话
     * @return 客户数量统计数据
     */
    @GetMapping("/statistics/customer/count")
    public ResponseEntity<Map<String, Object>> getCustomerCountStatistics(
            @RequestParam(value = "days", defaultValue = "7") int days,
            HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Map<String, Long> statistics = customerService.getCustomerCountStatistics(days);
            return ResponseUtils.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.serverError("获取客户数量统计失败");
        }
    }

    /**
     * 获取跟进方式分布统计
     * @param session HTTP会话
     * @return 跟进方式分布统计数据
     */
    @GetMapping("/statistics/followup/method")
    public ResponseEntity<Map<String, Object>> getFollowupMethodStatistics(HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Map<String, Long> statistics = followupService.getFollowupMethodStatistics();
            return ResponseUtils.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.serverError("获取跟进方式分布统计失败");
        }
    }

    /**
     * 获取跟进记录数量统计（按时间段）
     * @param days 天数
     * @param session HTTP会话
     * @return 跟进记录数量统计数据
     */
    @GetMapping("/statistics/followup/count")
    public ResponseEntity<Map<String, Object>> getFollowupCountStatistics(
            @RequestParam(value = "days", defaultValue = "7") int days,
            HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            Map<String, Long> statistics = followupService.getFollowupCountStatistics(days);
            return ResponseUtils.success(statistics);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.serverError("获取跟进记录数量统计失败");
        }
    }

}
