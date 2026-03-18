package com.example.demo;

import com.example.demo.service.RoleService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	/**
	 * 应用启动时初始化默认角色和权限
	 */
	@Bean
	public CommandLineRunner initRolesAndPermissions(RoleService roleService) {
		return args -> {
			try {
				roleService.initDefaultRolesAndPermissions();
				System.out.println("✅ 默认角色和权限初始化完成");
			} catch (Exception e) {
				System.err.println("❌ 初始化角色权限失败：" + e.getMessage());
			}
		};
	}

}
