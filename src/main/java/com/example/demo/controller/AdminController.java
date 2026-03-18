package com.example.demo.controller;

import com.example.demo.entity.Role;
import com.example.demo.entity.Permission;
import com.example.demo.entity.User;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RoleService roleService;

    // ==================== 管理后台首页 ====================
    @GetMapping("")
    public String adminDashboard() {
        return "redirect:/admin/roles";
    }

    // ==================== 角色管理 ====================
    
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public String roleList(Model model) {
        List<Role> roles = roleService.getAllRoles();
        List<Permission> permissions = roleService.getAllPermissions();
        model.addAttribute("roles", roles);
        model.addAttribute("permissions", permissions);
        return "admin/role-list";
    }

    @PostMapping("/roles/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createRole(
            @RequestParam String name,
            @RequestParam String description,
            RedirectAttributes redirectAttrs) {
        try {
            roleService.createRole(name, description);
            redirectAttrs.addFlashAttribute("successMsg", "角色创建成功！");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/update")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateRole(
            @RequestParam Long id,
            @RequestParam String name,
            @RequestParam String description,
            RedirectAttributes redirectAttrs) {
        try {
            roleService.updateRole(id, name, description);
            redirectAttrs.addFlashAttribute("successMsg", "角色更新成功！");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    @GetMapping("/roles/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteRole(
            @PathVariable Long id,
            RedirectAttributes redirectAttrs) {
        try {
            roleService.deleteRole(id);
            redirectAttrs.addFlashAttribute("successMsg", "角色删除成功！");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/assign-permission")
    @PreAuthorize("hasRole('ADMIN')")
    public String assignPermission(
            @RequestParam Long roleId,
            @RequestParam Long permissionId,
            RedirectAttributes redirectAttrs) {
        try {
            roleService.assignPermissionToRole(roleId, permissionId);
            redirectAttrs.addFlashAttribute("successMsg", "权限分配成功！");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/remove-permission")
    @PreAuthorize("hasRole('ADMIN')")
    public String removePermission(
            @RequestParam Long roleId,
            @RequestParam Long permissionId,
            RedirectAttributes redirectAttrs) {
        try {
            roleService.removePermissionFromRole(roleId, permissionId);
            redirectAttrs.addFlashAttribute("successMsg", "权限移除成功！");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/roles";
    }

    // ==================== 用户管理 ====================
    
    @Autowired
    private com.example.demo.repository.UserRepository userRepository;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String userList(Model model) {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("roles", roles);
        return "admin/user-list";
    }

    @PostMapping("/users/assign-role")
    @PreAuthorize("hasRole('ADMIN')")
    public String assignRole(
            @RequestParam Long userId,
            @RequestParam Long roleId,
            RedirectAttributes redirectAttrs) {
        try {
            roleService.assignRoleToUser(userId, roleId);
            redirectAttrs.addFlashAttribute("successMsg", "角色分配成功！");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ==================== 管理员设置 ====================
    
    @GetMapping("/promote")
    @PreAuthorize("hasRole('ADMIN')")
    public String promotePage(Model model, Authentication authentication) {
        String currentUsername = authentication.getName();
        User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
        Long currentUserId = currentUser != null ? currentUser.getId() : null;
        
        // 获取管理员角色
        Role adminRole = roleService.getRoleByName("ROLE_ADMIN");
        
        // 获取所有管理员
        List<User> admins = new ArrayList<>();
        if (adminRole != null) {
            admins = adminRole.getUsers();
        }
        
        // 获取所有非管理员用户
        List<User> allUsers = userRepository.findAll();
        List<User> normalUsers = new ArrayList<>();
        for (User user : allUsers) {
            boolean isAdmin = user.getRoles().stream()
                    .anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
            if (!isAdmin) {
                normalUsers.add(user);
            }
        }
        
        model.addAttribute("admins", admins);
        model.addAttribute("normalUsers", normalUsers);
        model.addAttribute("currentUserId", currentUserId);
        
        return "admin/promote";
    }

    @PostMapping("/promote/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String promoteToAdmin(
            @RequestParam Long userId,
            RedirectAttributes redirectAttrs) {
        try {
            Role adminRole = roleService.getRoleByName("ROLE_ADMIN");
            if (adminRole == null) {
                throw new RuntimeException("管理员角色不存在");
            }
            roleService.assignRoleToUser(userId, adminRole.getId());
            redirectAttrs.addFlashAttribute("successMsg", "用户已提升为管理员！");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/promote";
    }

    @PostMapping("/promote/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public String removeAdmin(
            @RequestParam Long userId,
            RedirectAttributes redirectAttrs) {
        try {
            Role adminRole = roleService.getRoleByName("ROLE_ADMIN");
            if (adminRole == null) {
                throw new RuntimeException("管理员角色不存在");
            }
            roleService.removeRoleFromUser(userId, adminRole.getId());
            redirectAttrs.addFlashAttribute("successMsg", "已移除管理员身份！");
        } catch (RuntimeException e) {
            redirectAttrs.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/promote";
    }
}
