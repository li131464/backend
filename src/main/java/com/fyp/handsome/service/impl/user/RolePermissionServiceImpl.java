package com.fyp.handsome.service.impl.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyp.handsome.entity.Permission;
import com.fyp.handsome.mapper.PermissionMapper;
import com.fyp.handsome.mapper.RoleMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 角色权限服务实现类
 * @author ziye
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl {

    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;

    // =================== 角色权限关系管理 ===================

    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        try {
            log.info("为角色分配权限，roleId：{}，permissionIds：{}", roleId, permissionIds);
            
            // 先删除角色的所有权限
            roleMapper.deleteRolePermissions(roleId);
            
            // 批量插入新的权限关联
            if (permissionIds != null && !permissionIds.isEmpty()) {
                for (Long permissionId : permissionIds) {
                    roleMapper.insertRolePermission(roleId, permissionId);
                }
            }
            
            log.info("角色权限分配成功，roleId：{}，permissionIds：{}", roleId, permissionIds);
            return true;
        } catch (Exception e) {
            log.error("角色权限分配失败，roleId：{}，permissionIds：{}，错误：{}", roleId, permissionIds, e.getMessage(), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        try {
            log.info("移除角色权限，roleId：{}，permissionIds：{}", roleId, permissionIds);
            
            if (permissionIds != null && !permissionIds.isEmpty()) {
                for (Long permissionId : permissionIds) {
                    roleMapper.deleteRolePermissionByPermissionId(roleId, permissionId);
                }
            }
            
            log.info("移除角色权限成功，roleId：{}，permissionIds：{}", roleId, permissionIds);
            return true;
        } catch (Exception e) {
            log.error("移除角色权限失败，roleId：{}，permissionIds：{}，错误：{}", roleId, permissionIds, e.getMessage(), e);
            return false;
        }
    }

    public List<Permission> getRolePermissions(Long roleId) {
        try {
            return permissionMapper.selectPermissionsByRoleId(roleId);
        } catch (Exception e) {
            log.error("查询角色权限失败，roleId：{}，错误：{}", roleId, e.getMessage(), e);
            return List.of();
        }
    }
} 