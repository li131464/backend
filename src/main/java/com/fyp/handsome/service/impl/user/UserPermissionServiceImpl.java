package com.fyp.handsome.service.impl.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fyp.handsome.entity.Permission;
import com.fyp.handsome.entity.Role;
import com.fyp.handsome.mapper.PermissionMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户权限服务实现类
 * @author ziye
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPermissionServiceImpl {

    private final PermissionMapper permissionMapper;
    private final UserRoleServiceImpl userRoleService;

    // =================== 权限验证 ===================

    public boolean hasPermission(Long userId, String permissionCode) {
        try {
            List<Permission> permissions = getUserPermissions(userId);
            return permissions.stream()
                    .anyMatch(permission -> permission.getPermissionCode().equals(permissionCode));
        } catch (Exception e) {
            log.error("检查用户权限失败，userId：{}，permissionCode：{}，错误：{}", userId, permissionCode, e.getMessage(), e);
            return false;
        }
    }

    public boolean hasRole(Long userId, String roleCode) {
        try {
            List<Role> roles = userRoleService.getUserRoles(userId);
            return roles.stream()
                    .anyMatch(role -> role.getRoleCode().equals(roleCode));
        } catch (Exception e) {
            log.error("检查用户角色失败，userId：{}，roleCode：{}，错误：{}", userId, roleCode, e.getMessage(), e);
            return false;
        }
    }

    public List<Permission> getUserPermissions(Long userId) {
        try {
            return permissionMapper.selectPermissionsByUserId(userId);
        } catch (Exception e) {
            log.error("查询用户权限失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return List.of();
        }
    }
} 