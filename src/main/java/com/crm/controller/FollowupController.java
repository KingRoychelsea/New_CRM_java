package com.crm.controller;

import com.crm.model.Followup;
import com.crm.service.FollowupService;
import com.crm.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 跟进记录控制器，处理跟进记录相关的HTTP请求
 */
@RestController
@RequestMapping("/api")
public class FollowupController {

    @Autowired
    private FollowupService followupService;

    /**
     * 获取跟进记录列表，支持分页和按客户筛选
     * @param page 页码（默认1）
     * @param limit 每页数量（默认10）
     * @param customerId 客户ID
     * @param session HTTP会话
     * @return 跟进记录列表响应
     */
    @GetMapping("/followups")
    public ResponseEntity<Map<String, Object>> getFollowups(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "customer_id", required = false) String customerIdStr,
            HttpSession session) {

        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        // 处理customer_id参数
        Integer customerId = null;
        if (customerIdStr != null && !customerIdStr.isEmpty()) {
            try {
                customerId = Integer.parseInt(customerIdStr);
            } catch (NumberFormatException e) {
                // 忽略格式错误的customer_id参数
                customerId = null;
            }
        }

        // 获取跟进记录列表
        Page<Followup> followupPage = followupService.getFollowups(page, limit, customerId);

        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("code", 200);
        responseData.put("message", "操作成功");
        responseData.put("data", followupPage.getContent());
        responseData.put("total", followupPage.getTotalElements());
        responseData.put("page", page);
        responseData.put("limit", limit);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    /**
     * 添加跟进记录
     * @param followupData 跟进记录数据
     * @param session HTTP会话
     * @return 添加响应
     */
    @PostMapping("/followups")
    public ResponseEntity<Map<String, Object>> addFollowup(@RequestBody Map<String, Object> followupData, HttpSession session) {
        // 检查登录状态
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        // 构建跟进记录对象
        Followup followup = new Followup();
        followup.setCustomerId((Integer) followupData.get("customer_id"));
        followup.setUserId(userId);
        followup.setFollowMethod((String) followupData.get("follow_method"));
        followup.setContent((String) followupData.get("content"));

        // 解析跟进时间
        if (followupData.containsKey("follow_time")) {
            String followTimeStr = (String) followupData.get("follow_time");
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                LocalDateTime followTime = LocalDateTime.parse(followTimeStr, formatter);
                followup.setFollowTime(followTime);
            } catch (Exception e) {
                return ResponseUtils.badRequest("跟进时间格式错误");
            }
        }

        // 解析下次跟进提醒时间
        if (followupData.containsKey("next_follow_reminder")) {
            String nextFollowReminderStr = (String) followupData.get("next_follow_reminder");
            if (nextFollowReminderStr != null && !nextFollowReminderStr.isEmpty()) {
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
                    LocalDateTime nextFollowReminder = LocalDateTime.parse(nextFollowReminderStr, formatter);
                    followup.setNextFollowReminder(nextFollowReminder);
                } catch (Exception e) {
                    return ResponseUtils.badRequest("下次跟进提醒时间格式错误");
                }
            }
        }

        // 验证必填字段
        if (followup.getCustomerId() == null || followup.getFollowTime() == null || 
                followup.getFollowMethod() == null || followup.getContent() == null) {
            return ResponseUtils.badRequest("客户ID、跟进时间、跟进方式和内容不能为空");
        }

        // 添加跟进记录
        Followup savedFollowup = followupService.addFollowup(followup);

        // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("id", savedFollowup.getId());

        return ResponseUtils.success("添加成功", responseData);
    }

    /**
     * 删除跟进记录
     * @param id 跟进记录ID
     * @param session HTTP会话
     * @return 删除响应
     */
    @DeleteMapping("/followups/{id}")
    public ResponseEntity<Map<String, Object>> deleteFollowup(@PathVariable Integer id, HttpSession session) {
        // 检查登录状态
        if (session.getAttribute("userId") == null) {
            return ResponseUtils.unauthorized("请先登录");
        }

        // 删除跟进记录
        boolean deleted = followupService.deleteFollowup(id);
        if (deleted) {
            return ResponseUtils.success("删除成功");
        } else {
            return ResponseUtils.notFound("跟进记录不存在");
        }
    }

}
