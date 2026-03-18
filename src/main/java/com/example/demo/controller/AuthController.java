package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.core.Authentication;
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

    @Autowired
    private RoleService roleService;

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
        userRepository.save(user);

        // 5. 分配默认角色（ROLE_USER）
        Role userRole = roleService.getRoleByName("ROLE_USER");
        if (userRole != null) {
            user.addRole(userRole);
            userRepository.save(user);
        }

        // 6. 注册成功，重定向到登录页
        return "redirect:/login?registered=true";
    }

    // ==================== 修改密码 ====================

    // 显示修改密码页面
    @GetMapping("/profile/change-password")
    public String showChangePasswordForm() {
        return "change-password";
    }

    // 处理修改密码提交
    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword,
            Authentication authentication,
            Model model) {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 1. 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            model.addAttribute("error", "原密码错误");
            return "change-password";
        }

        // 2. 验证新密码一致性
        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "两次输入的新密码不一致");
            return "change-password";
        }

        // 3. 验证新密码强度
        if (newPassword.length() < 6) {
            model.addAttribute("error", "密码长度至少为 6 位");
            return "change-password";
        }

        // 4. 更新密码
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        // 5. 修改成功，重定向到首页
        return "redirect:/profile/change-password?success=true";
    }
}