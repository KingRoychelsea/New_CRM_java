package com.crm.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应工具类，用于统一API响应格式
 */
public class ResponseUtils {

    /**
     * 成功响应
     * @param data 响应数据
     * @return 成功响应实体
     */
    public static ResponseEntity<Map<String, Object>> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "操作成功");
        if (data != null) {
            response.put("data", data);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 成功响应（无数据）
     * @return 成功响应实体
     */
    public static ResponseEntity<Map<String, Object>> success() {
        return success(null);
    }

    /**
     * 成功响应（自定义消息）
     * @param message 响应消息
     * @param data 响应数据
     * @return 成功响应实体
     */
    public static ResponseEntity<Map<String, Object>> success(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        if (data != null) {
            response.put("data", data);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 错误响应
     * @param code 错误码
     * @param message 错误消息
     * @return 错误响应实体
     */
    public static ResponseEntity<Map<String, Object>> error(int code, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", code);
        response.put("message", message);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        switch (code) {
            case 401:
                status = HttpStatus.UNAUTHORIZED;
                break;
            case 403:
                status = HttpStatus.FORBIDDEN;
                break;
            case 404:
                status = HttpStatus.NOT_FOUND;
                break;
            case 500:
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
            default:
                status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, status);
    }

    /**
     * 400错误响应（请求错误）
     * @param message 错误消息
     * @return 400错误响应实体
     */
    public static ResponseEntity<Map<String, Object>> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 401错误响应（认证失败）
     * @param message 错误消息
     * @return 401错误响应实体
     */
    public static ResponseEntity<Map<String, Object>> unauthorized(String message) {
        return error(401, message);
    }

    /**
     * 404错误响应（资源不存在）
     * @param message 错误消息
     * @return 404错误响应实体
     */
    public static ResponseEntity<Map<String, Object>> notFound(String message) {
        return error(404, message);
    }

    /**
     * 403错误响应（权限不足）
     * @param message 错误消息
     * @return 403错误响应实体
     */
    public static ResponseEntity<Map<String, Object>> forbidden(String message) {
        return error(403, message);
    }

    /**
     * 500错误响应（服务器错误）
     * @param message 错误消息
     * @return 500错误响应实体
     */
    public static ResponseEntity<Map<String, Object>> serverError(String message) {
        return error(500, message);
    }

}
