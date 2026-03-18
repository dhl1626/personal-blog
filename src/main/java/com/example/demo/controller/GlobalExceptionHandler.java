package com.example.demo.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 全局异常处理器
 * 拦截 Controller 中抛出的运行时异常，避免显示 500 白屏，而是重定向回列表页并提示错误。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public RedirectView handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes) {
        // 打印错误日志到控制台，方便调试
        System.err.println("【系统异常】: " + ex.getMessage());
        ex.printStackTrace();

        // 将错误信息添加到 Flash Attribute (重定向后一次性使用)
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        
        // 重定向回文章列表页，并标记为错误状态
        // 你的 article-list.html 需要能处理 errorMessage 或 error 参数
        return new RedirectView("/article/list?error=forbidden");
    }
}