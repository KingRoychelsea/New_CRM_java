package com.crm.controller;

import com.crm.util.TestDataGenerator;
import com.crm.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 测试数据控制器，用于生成测试数据
 */
@RestController
@RequestMapping("/api")
public class TestDataController {

    @Autowired
    private TestDataGenerator testDataGenerator;

    /**
     * 生成测试数据
     * @param session HTTP会话
     * @return 生成结果响应
     */
    @GetMapping("/test/generate-data")
    public ResponseEntity<Map<String, Object>> generateTestData(HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        try {
            testDataGenerator.generateTestData();
            return ResponseUtils.success("测试数据生成成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseUtils.serverError("生成测试数据失败");
        }
    }
}
