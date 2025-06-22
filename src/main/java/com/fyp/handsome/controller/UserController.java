package com.fyp.handsome.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyp.handsome.dto.Result;
import com.fyp.handsome.dto.ResultCode;
import com.fyp.handsome.entity.Permission;
import com.fyp.handsome.entity.Role;
import com.fyp.handsome.entity.User;
import com.fyp.handsome.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户权限管理控制器
 * @author ziye
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // =================== 用户认证 ===================

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        try {
            String username = params.get("username");
            String password = params.get("password");
            
            log.info("用户尝试登录，username：{}", username);
            
            Map<String, Object> result = userService.login(username, password);
            
            if ((Boolean) result.get("success")) {
                return Result.success("登录成功", result);
            } else {
                return Result.error((String) result.get("message"));
            }
            
        } catch (Exception e) {
            log.error("用户登录异常，错误：{}", e.getMessage(), e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestParam Long userId) {
        try {
            if (userService.logout(userId)) {
                return Result.success();
            } else {
                return Result.error("登出失败");
            }
            
        } catch (Exception e) {
            log.error("用户登出失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return Result.error("登出失败：" + e.getMessage());
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh-token")
    public Result<Map<String, Object>> refreshToken(@RequestBody Map<String, String> params) {
        try {
            String refreshToken = params.get("refreshToken");
            
            Map<String, Object> result = userService.refreshToken(refreshToken);
            
            if ((Boolean) result.get("success")) {
                return Result.success("Token刷新成功", result);
            } else {
                return Result.error((String) result.get("message"));
            }
            
        } catch (Exception e) {
            log.error("Token刷新失败，错误：{}", e.getMessage(), e);
            return Result.error("Token刷新失败：" + e.getMessage());
        }
    }

    // =================== 用户管理 ===================

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        try {
            log.info("用户注册，username：{}", user.getUsername());
            
            if (userService.register(user)) {
                return Result.success();
            } else {
                return Result.error(ResultCode.USER_ALREADY_EXISTS);
            }
            
        } catch (Exception e) {
            log.error("用户注册失败，username：{}，错误：{}", user.getUsername(), e.getMessage(), e);
            return Result.error("用户注册失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam Long userId) {
        try {
            User user = userService.getById(userId);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.error(ResultCode.USER_NOT_FOUND);
            }
            
        } catch (Exception e) {
            log.error("获取用户信息失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取用户信息
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.error(ResultCode.USER_NOT_FOUND);
            }
            
        } catch (Exception e) {
            log.error("获取用户信息失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody User user) {
        try {
            if (userService.updateUser(user)) {
                return Result.success();
            } else {
                return Result.error("用户信息更新失败");
            }
            
        } catch (Exception e) {
            log.error("更新用户信息失败，userId：{}，错误：{}", user.getId(), e.getMessage(), e);
            return Result.error("更新用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户信息（管理员使用）
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            user.setId(id);
            
            if (userService.updateUser(user)) {
                return Result.success();
            } else {
                return Result.error("用户信息更新失败");
            }
            
        } catch (Exception e) {
            log.error("更新用户信息失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("更新用户信息失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> params) {
        try {
            Long userId = Long.valueOf(params.get("userId"));
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            
            if (userService.changePassword(userId, oldPassword, newPassword)) {
                return Result.success();
            } else {
                return Result.error(ResultCode.PASSWORD_ERROR);
            }
            
        } catch (Exception e) {
            log.error("修改密码失败，错误：{}", e.getMessage(), e);
            return Result.error("修改密码失败：" + e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            if (userService.deleteUser(id)) {
                return Result.success();
            } else {
                return Result.error("用户删除失败");
            }
            
        } catch (Exception e) {
            log.error("删除用户失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("删除用户失败：" + e.getMessage());
        }
    }

    /**
     * 批量删除用户
     */
    @DeleteMapping("/batch")
    public Result<Void> deleteUsers(@RequestBody List<Long> userIds) {
        try {
            if (userService.deleteUsers(userIds)) {
                return Result.success();
            } else {
                return Result.error("批量删除用户失败");
            }
            
        } catch (Exception e) {
            log.error("批量删除用户失败，userIds：{}，错误：{}", userIds, e.getMessage(), e);
            return Result.error("批量删除用户失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            if (userService.updateUserStatus(id, status)) {
                return Result.success();
            } else {
                return Result.error("用户状态更新失败");
            }
            
        } catch (Exception e) {
            log.error("更新用户状态失败，id：{}，status：{}，错误：{}", id, status, e.getMessage(), e);
            return Result.error("更新用户状态失败：" + e.getMessage());
        }
    }

    /**
     * 重置用户密码
     */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> params) {
        try {
            String newPassword = params.get("newPassword");
            
            if (userService.resetPassword(id, newPassword)) {
                return Result.success();
            } else {
                return Result.error("密码重置失败");
            }
            
        } catch (Exception e) {
            log.error("重置密码失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("重置密码失败：" + e.getMessage());
        }
    }

    /**
     * 修改用户密码（管理员使用）
     */
    @PutMapping("/{id}/change-password")
    public Result<Void> changePasswordById(@PathVariable Long id, @RequestBody Map<String, String> params) {
        try {
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            
            if (userService.changePassword(id, oldPassword, newPassword)) {
                return Result.success();
            } else {
                return Result.error(ResultCode.PASSWORD_ERROR);
            }
            
        } catch (Exception e) {
            log.error("修改密码失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("修改密码失败：" + e.getMessage());
        }
    }

    // =================== 用户查询 ===================

    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.error(ResultCode.USER_NOT_FOUND);
            }
            
        } catch (Exception e) {
            log.error("根据用户名获取用户失败，username：{}，错误：{}", username, e.getMessage(), e);
            return Result.error("根据用户名获取用户失败：" + e.getMessage());
        }
    }

    /**
     * 根据邮箱获取用户
     */
    @GetMapping("/email/{email}")
    public Result<User> getUserByEmail(@PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.error(ResultCode.USER_NOT_FOUND);
            }
            
        } catch (Exception e) {
            log.error("根据邮箱获取用户失败，email：{}，错误：{}", email, e.getMessage(), e);
            return Result.error("根据邮箱获取用户失败：" + e.getMessage());
        }
    }

    /**
     * 用户列表查询（支持分页和条件查询）
     */
    @GetMapping("/list")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status) {
        try {
            Page<User> page = new Page<>(current, size);
            IPage<User> result = userService.getUsersPage(page, username, realName, email, status);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("用户列表查询失败，错误：{}", e.getMessage(), e);
            return Result.error("用户列表查询失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询用户信息（保留原有接口）
     */
    @GetMapping("/page")
    public Result<IPage<User>> getUsersPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer status) {
        try {
            Page<User> page = new Page<>(current, size);
            IPage<User> result = userService.getUsersPage(page, username, realName, email, status);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询用户信息失败，错误：{}", e.getMessage(), e);
            return Result.error("分页查询用户信息失败：" + e.getMessage());
        }
    }

    // =================== 角色管理 ===================

    /**
     * 新增角色
     */
    @PostMapping("/role")
    public Result<Void> addRole(@RequestBody Role role) {
        try {
            if (userService.addRole(role)) {
                return Result.success();
            } else {
                return Result.error(ResultCode.ROLE_ALREADY_EXISTS);
            }
            
        } catch (Exception e) {
            log.error("新增角色失败，roleName：{}，错误：{}", role.getRoleName(), e.getMessage(), e);
            return Result.error("新增角色失败：" + e.getMessage());
        }
    }

    /**
     * 更新角色
     */
    @PutMapping("/role/{id}")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Role role) {
        try {
            role.setId(id);
            
            if (userService.updateRole(role)) {
                return Result.success();
            } else {
                return Result.error("角色更新失败");
            }
            
        } catch (Exception e) {
            log.error("更新角色失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("更新角色失败：" + e.getMessage());
        }
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/role/{id}")
    public Result<Void> deleteRole(@PathVariable Long id) {
        try {
            if (userService.deleteRole(id)) {
                return Result.success();
            } else {
                return Result.error(ResultCode.ROLE_NOT_FOUND);
            }
            
        } catch (Exception e) {
            log.error("删除角色失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("删除角色失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有角色
     */
    @GetMapping("/role/all")
    public Result<List<Role>> getAllRoles() {
        try {
            List<Role> roles = userService.getAllRoles();
            return Result.success(roles);
            
        } catch (Exception e) {
            log.error("获取所有角色失败，错误：{}", e.getMessage(), e);
            return Result.error("获取所有角色失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询角色
     */
    @GetMapping("/role/page")
    public Result<IPage<Role>> getRolesPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) Integer status) {
        try {
            Page<Role> page = new Page<>(current, size);
            IPage<Role> result = userService.getRolesPage(page, roleName, roleCode, status);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询角色失败，错误：{}", e.getMessage(), e);
            return Result.error("分页查询角色失败：" + e.getMessage());
        }
    }

    // =================== 权限管理 ===================

    /**
     * 新增权限
     */
    @PostMapping("/permission")
    public Result<Void> addPermission(@RequestBody Permission permission) {
        try {
            if (userService.addPermission(permission)) {
                return Result.success();
            } else {
                return Result.error("权限添加失败");
            }
            
        } catch (Exception e) {
            log.error("新增权限失败，permissionName：{}，错误：{}", permission.getPermissionName(), e.getMessage(), e);
            return Result.error("新增权限失败：" + e.getMessage());
        }
    }

    /**
     * 更新权限
     */
    @PutMapping("/permission/{id}")
    public Result<Void> updatePermission(@PathVariable Long id, @RequestBody Permission permission) {
        try {
            permission.setId(id);
            
            if (userService.updatePermission(permission)) {
                return Result.success();
            } else {
                return Result.error("权限更新失败");
            }
            
        } catch (Exception e) {
            log.error("更新权限失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("更新权限失败：" + e.getMessage());
        }
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/permission/{id}")
    public Result<Void> deletePermission(@PathVariable Long id) {
        try {
            if (userService.deletePermission(id)) {
                return Result.success();
            } else {
                return Result.error(ResultCode.PERMISSION_NOT_FOUND);
            }
            
        } catch (Exception e) {
            log.error("删除权限失败，id：{}，错误：{}", id, e.getMessage(), e);
            return Result.error("删除权限失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有权限
     */
    @GetMapping("/permission/all")
    public Result<List<Permission>> getAllPermissions() {
        try {
            List<Permission> permissions = userService.getAllPermissions();
            return Result.success(permissions);
            
        } catch (Exception e) {
            log.error("获取所有权限失败，错误：{}", e.getMessage(), e);
            return Result.error("获取所有权限失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询权限
     */
    @GetMapping("/permission/page")
    public Result<IPage<Permission>> getPermissionsPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String permissionName,
            @RequestParam(required = false) String permissionCode,
            @RequestParam(required = false) Integer permissionType,
            @RequestParam(required = false) Integer status) {
        try {
            Page<Permission> page = new Page<>(current, size);
            IPage<Permission> result = userService.getPermissionsPage(
                    page, permissionName, permissionCode, permissionType, status);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("分页查询权限失败，错误：{}", e.getMessage(), e);
            return Result.error("分页查询权限失败：" + e.getMessage());
        }
    }

    // =================== 用户角色关系管理 ===================

    /**
     * 为用户分配角色
     */
    @PostMapping("/{userId}/roles")
    public Result<Void> assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        try {
            if (userService.assignRolesToUser(userId, roleIds)) {
                return Result.success();
            } else {
                return Result.error("用户角色分配失败");
            }
            
        } catch (Exception e) {
            log.error("为用户分配角色失败，userId：{}，roleIds：{}，错误：{}", userId, roleIds, e.getMessage(), e);
            return Result.error("用户角色分配失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的角色
     */
    @GetMapping("/{userId}/roles")
    public Result<List<Role>> getUserRoles(@PathVariable Long userId) {
        try {
            List<Role> roles = userService.getUserRoles(userId);
            return Result.success(roles);
            
        } catch (Exception e) {
            log.error("获取用户角色失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return Result.error("获取用户角色失败：" + e.getMessage());
        }
    }

    // =================== 角色权限关系管理 ===================

    /**
     * 为角色分配权限
     */
    @PostMapping("/role/{roleId}/permissions")
    public Result<Void> assignPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        try {
            if (userService.assignPermissionsToRole(roleId, permissionIds)) {
                return Result.success();
            } else {
                return Result.error("角色权限分配失败");
            }
            
        } catch (Exception e) {
            log.error("为角色分配权限失败，roleId：{}，permissionIds：{}，错误：{}", 
                     roleId, permissionIds, e.getMessage(), e);
            return Result.error("角色权限分配失败：" + e.getMessage());
        }
    }

    /**
     * 获取角色的权限
     */
    @GetMapping("/role/{roleId}/permissions")
    public Result<List<Permission>> getRolePermissions(@PathVariable Long roleId) {
        try {
            List<Permission> permissions = userService.getRolePermissions(roleId);
            return Result.success(permissions);
            
        } catch (Exception e) {
            log.error("获取角色权限失败，roleId：{}，错误：{}", roleId, e.getMessage(), e);
            return Result.error("获取角色权限失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的权限
     */
    @GetMapping("/{userId}/permissions")
    public Result<List<Permission>> getUserPermissions(@PathVariable Long userId) {
        try {
            List<Permission> permissions = userService.getUserPermissions(userId);
            return Result.success(permissions);
            
        } catch (Exception e) {
            log.error("获取用户权限失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return Result.error("获取用户权限失败：" + e.getMessage());
        }
    }

    // =================== 权限验证 ===================

    /**
     * 检查用户是否有指定权限
     */
    @GetMapping("/{userId}/has-permission/{permissionCode}")
    public Result<Boolean> hasPermission(@PathVariable Long userId, @PathVariable String permissionCode) {
        try {
            boolean hasPermission = userService.hasPermission(userId, permissionCode);
            return Result.success(hasPermission);
            
        } catch (Exception e) {
            log.error("检查用户权限失败，userId：{}，permissionCode：{}，错误：{}", 
                     userId, permissionCode, e.getMessage(), e);
            return Result.error("检查用户权限失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户是否有指定角色
     */
    @GetMapping("/{userId}/has-role/{roleCode}")
    public Result<Boolean> hasRole(@PathVariable Long userId, @PathVariable String roleCode) {
        try {
            boolean hasRole = userService.hasRole(userId, roleCode);
            return Result.success(hasRole);
            
        } catch (Exception e) {
            log.error("检查用户角色失败，userId：{}，roleCode：{}，错误：{}", 
                     userId, roleCode, e.getMessage(), e);
            return Result.error("检查用户角色失败：" + e.getMessage());
        }
    }

    // =================== 统计信息 ===================

    /**
     * 获取用户统计概览
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getUserStatistics() {
        try {
            Map<String, Object> statistics = userService.getUserStatistics();
            return Result.success(statistics);
            
        } catch (Exception e) {
            log.error("获取用户统计概览失败，错误：{}", e.getMessage(), e);
            return Result.error("获取用户统计概览失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户总数
     */
    @GetMapping("/count")
    public Result<Long> getUserTotalCount() {
        try {
            Long count = userService.getUserTotalCount();
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("获取用户总数失败，错误：{}", e.getMessage(), e);
            return Result.error("获取用户总数失败：" + e.getMessage());
        }
    }
} 