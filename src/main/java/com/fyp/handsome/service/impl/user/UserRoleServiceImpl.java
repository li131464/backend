package com.fyp.handsome.service.impl.user;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fyp.handsome.entity.Role;
import com.fyp.handsome.mapper.RoleMapper;
import com.fyp.handsome.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户角色服务实现类
 * @author ziye
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    // =================== 用户角色关系管理 ===================

    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        try {
            log.info("为用户分配角色，userId：{}，roleIds：{}", userId, roleIds);
            
            // 先删除用户的所有角色
            userMapper.deleteUserRoles(userId);
            
            // 批量插入新的角色关联
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Long roleId : roleIds) {
                    userMapper.insertUserRole(userId, roleId);
                }
            }
            
            log.info("用户角色分配成功，userId：{}，roleIds：{}", userId, roleIds);
            return true;
        } catch (Exception e) {
            log.error("用户角色分配失败，userId：{}，roleIds：{}，错误：{}", userId, roleIds, e.getMessage(), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean removeRolesFromUser(Long userId, List<Long> roleIds) {
        try {
            log.info("移除用户角色，userId：{}，roleIds：{}", userId, roleIds);
            
            if (roleIds != null && !roleIds.isEmpty()) {
                for (Long roleId : roleIds) {
                    userMapper.deleteUserRoleByRoleId(userId, roleId);
                }
            }
            
            log.info("移除用户角色成功，userId：{}，roleIds：{}", userId, roleIds);
            return true;
        } catch (Exception e) {
            log.error("移除用户角色失败，userId：{}，roleIds：{}，错误：{}", userId, roleIds, e.getMessage(), e);
            return false;
        }
    }

    public List<Role> getUserRoles(Long userId) {
        try {
            return roleMapper.selectRolesByUserId(userId);
        } catch (Exception e) {
            log.error("查询用户角色失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return List.of();
        }
    }
} 