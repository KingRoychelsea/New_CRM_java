package com.crm.config;

import com.crm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 应用启动监听器，用于在应用启动时执行初始化操作
 */
@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private UserService userService;

    /**
     * 应用启动时执行
     * @param event 上下文刷新事件
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 创建默认管理员账号（如果不存在）
        userService.createDefaultAdmin();
        System.out.println("应用启动完成，默认管理员账号已初始化");
    }

}
