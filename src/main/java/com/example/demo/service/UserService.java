package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder; // 注意这里通常注入接口 PasswordEncoder
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    // Spring Security内置的BCrypt加密器（自动配置）
    private final PasswordEncoder passwordEncoder; // 使用接口类型更灵活
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 【核心】注册时加密密码
    public User registerUser(String username, String rawPassword, String nickname) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        // 加密原始密码（rawPassword → BCrypt哈希）
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, encodedPassword, nickname);
        return userRepository.save(user);
    }
}