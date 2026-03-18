package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制器 - 用于验证项目是否正常运行
 * 访问 /api/health 检查服务状态
 */
@RestController
public class HelloController {
    
    @GetMapping("/api/health")
    public String health() {
        return "Blog service is running!";
    }
}
