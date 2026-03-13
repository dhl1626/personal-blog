package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 显示登录页面 (Spring Security 默认会拦截并跳转到此，但显式定义更清晰)
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // 对应 templates/login.html
    }

    // 显示注册页面
    @GetMapping("/auth/register")
    public String showRegisterForm() {
        return "register"; // 对应 templates/register.html
    }

    // 处理注册提交
    @PostMapping("/auth/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {

        // 1. 简单校验：用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "用户名已存在");
            return "register"; // 返回注册页并显示错误
        }

        // 2. 校验密码一致性
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的密码不一致");
            return "register";
        }

        // 3. 密码加密
        String encodedPassword = passwordEncoder.encode(password);

        // 4. 创建并保存用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        // 默认角色可以在 User 实体构造函数中设置，或在此处设置
        // user.setRole("ROLE_USER"); 
        
        userRepository.save(user);

        // 5. 注册成功，重定向到登录页
        return "redirect:/login?registered=true";
    }
}