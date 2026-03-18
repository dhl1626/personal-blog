package com.example.demo.config;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 数据初始化配置
 * 用于创建默认管理员账户
 */
@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    /**
     * 创建默认管理员账户
     * 用户名：admin
     * 密码：admin123
     */
    @Bean
    public CommandLineRunner createAdminUser() {
        return args -> {
            // 检查是否已存在管理员账户
            if (userRepository.findByUsername("admin").isEmpty()) {
                // 确保角色已初始化
                roleService.initDefaultRolesAndPermissions();

                // 创建管理员用户
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setNickname("系统管理员");
                userRepository.save(admin);

                // 分配管理员角色
                Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                        .orElseThrow(() -> new RuntimeException("ROLE_ADMIN 角色不存在"));
                admin.addRole(adminRole);
                userRepository.save(admin);

                System.out.println("✅ 默认管理员账户创建成功！");
                System.out.println("   用户名：admin");
                System.out.println("   密码：admin123");
                System.out.println("   ⚠️ 请及时修改密码！");
            }
        };
    }
}
