package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity // 启用Security
@RequiredArgsConstructor // Lombok自动生成构造器（若无Lombok，手写构造器）
public class SecurityConfig {

    private final UserRepository userRepository;

    // 【关键1】密码加密器Bean（全局唯一，Security自动使用）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 【关键2】用户加载逻辑（登录时查数据库）
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
    }

    // 【关键3】安全规则配置
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/article/form", "/article/save").authenticated() // 写文章需登录
                .requestMatchers("/", "/articles", "/article/**", "/css/**", "/js/**", "/login", "/auth/register").permitAll() // 允许公开访问
                .anyRequest().authenticated() // 其他请求需登录
            )
            .formLogin(form -> form
                .loginPage("/login") // 自定义登录页
                .defaultSuccessUrl("/articles", true) // 登录成功跳转
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/articles") // 注销后跳转
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // 开发环境暂时关闭CSRF（面试可解释：生产环境会开启）

        return http.build();
    }
}