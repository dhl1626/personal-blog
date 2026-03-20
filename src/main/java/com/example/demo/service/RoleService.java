package com.example.demo.service;

import com.example.demo.entity.Permission;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.PermissionRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== 角色管理 ====================

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    @Transactional
    public Role createRole(String name, String description) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new RuntimeException("角色已存在：" + name);
        }
        Role role = new Role(name, description);
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, String name, String description) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        role.setName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    // ==================== 权限管理 ====================

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public Permission getPermissionByName(String name) {
        return permissionRepository.findByName(name).orElse(null);
    }

    @Transactional
    public Permission createPermission(String name, String description) {
        if (permissionRepository.findByName(name).isPresent()) {
            throw new RuntimeException("权限已存在：" + name);
        }
        Permission permission = new Permission(name, description);
        return permissionRepository.save(permission);
    }

    // ==================== 角色权限关联 ====================

    @Transactional
    public void assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("权限不存在"));
        role.addPermission(permission);
        roleRepository.save(role);
    }

    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("权限不存在"));
        role.removePermission(permission);
        roleRepository.save(role);
    }

    // ==================== 用户角色关联 ====================

    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        
        // 检查角色是否已存在，避免重复添加
        boolean hasRole = user.getRoles().stream()
                .anyMatch(r -> r.getId().equals(roleId));
        
        if (!hasRole) {
            user.addRole(role);
            userRepository.save(user);
        }
    }

    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("角色不存在"));
        user.removeRole(role);
        userRepository.save(user);
    }

    // ==================== 初始化默认角色和权限 ====================

    @Transactional
    public void initDefaultRolesAndPermissions() {
        // 创建权限
        Permission articleCreate = createPermissionIfNotExists("article:create", "创建文章");
        Permission articleEdit = createPermissionIfNotExists("article:edit", "编辑文章");
        Permission articleDelete = createPermissionIfNotExists("article:delete", "删除文章");
        Permission articleView = createPermissionIfNotExists("article:view", "查看文章");
        Permission commentCreate = createPermissionIfNotExists("comment:create", "发表评论");
        Permission userManage = createPermissionIfNotExists("user:manage", "用户管理");
        Permission roleManage = createPermissionIfNotExists("role:manage", "角色管理");

        // 创建普通用户角色
        Role roleUser = createRoleIfNotExists("ROLE_USER", "普通用户");
        roleUser.addPermission(articleCreate);
        roleUser.addPermission(articleEdit);
        roleUser.addPermission(articleDelete);
        roleUser.addPermission(articleView);
        roleUser.addPermission(commentCreate);
        roleRepository.save(roleUser);

        // 创建管理员角色
        Role roleAdmin = createRoleIfNotExists("ROLE_ADMIN", "管理员");
        roleAdmin.addPermission(articleCreate);
        roleAdmin.addPermission(articleEdit);
        roleAdmin.addPermission(articleDelete);
        roleAdmin.addPermission(articleView);
        roleAdmin.addPermission(commentCreate);
        roleAdmin.addPermission(userManage);
        roleAdmin.addPermission(roleManage);
        roleRepository.save(roleAdmin);
    }

    private Permission createPermissionIfNotExists(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> createPermission(name, description));
    }

    private Role createRoleIfNotExists(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> createRole(name, description));
    }
}
