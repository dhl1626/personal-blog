package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在：" + username));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 公开访问的资源
                .requestMatchers("/", "/articles", "/article/list", "/article/tags", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login", "/auth/register", "/auth/**").permitAll()

                // 需要登录才能访问的资源
                .requestMatchers("/article/form", "/article/save", "/article/edit/**", "/article/delete/**").authenticated()
                .requestMatchers("/article/my-blogs", "/article/comment/**").authenticated()

                // 后台管理页面 - 使用 hasAnyAuthority 直接匹配完整角色名
                .requestMatchers("/admin/dashboard", "/admin/articles").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_EDITOR")

                // 管理员专属资源
                .requestMatchers("/admin/users", "/admin/roles", "/admin/promote", "/role/**", "/permission/**").hasAuthority("ROLE_ADMIN")

                // 其他后台管理页面
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_EDITOR")

                // 后台管理 API - 需要 ADMIN 或 MANAGER 角色
                .requestMatchers("/api/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_EDITOR")

                // 其他所有请求需要登录
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/articles", false)  // false: 根据保存的请求决定
                .permitAll()
                .failureUrl("/login?error=true")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/articles")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
                .authenticationEntryPoint((request, response, authException) -> {
                    // API 请求返回 401，页面请求重定向到登录页
                    if (request.getRequestURI().startsWith("/api/")) {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"unauthorized\", \"message\": \"请先登录\"}");
                    }
                })
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
