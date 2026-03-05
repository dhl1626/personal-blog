package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
// 移除类级别的 @RequestMapping("/auth") 
// 原因：这会导致登录页路径变成 /auth/login，与 SecurityConfig 配置的 /login 不一致
// 我们将把 /auth 前缀只加在注册相关方法上
public class AuthController {
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    // 显示注册页
    // 显式添加 /auth/register 路径
    @GetMapping("/auth/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    // 处理注册
    @PostMapping("/auth/register")
    public String registerUser(
            @Valid UserForm userForm, 
            BindingResult result,
            RedirectAttributes redirectAttrs) {
        
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            userService.registerUser(
                userForm.getUsername(), 
                userForm.getPassword(), 
                userForm.getNickname()
            );
            redirectAttrs.addFlashAttribute("successMsg", "注册成功！请登录");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/auth/register";
        }
    }
    
    // 显示登录页（Security自动处理提交，只需提供页面）
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    // 简化表单数据绑定（避免直接用User实体）
    public static class UserForm {
        @NotBlank(message = "用户名不能为空")
        private String username;
        @NotBlank(message = "密码不能为空")
        private String password;
        @NotBlank(message = "昵称不能为空")
        private String nickname;
        // Getter/Setter
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }
}