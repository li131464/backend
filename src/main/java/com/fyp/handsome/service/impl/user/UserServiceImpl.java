package com.fyp.handsome.service.impl.user;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyp.handsome.entity.Permission;
import com.fyp.handsome.entity.Role;
import com.fyp.handsome.entity.User;
import com.fyp.handsome.mapper.UserMapper;
import com.fyp.handsome.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务实现类（主服务类）
 * @author ziye
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 拆分后的子服务
    private final UserAuthServiceImpl userAuthService;
    private final UserManagementServiceImpl userManagementService;
    private final UserRoleServiceImpl userRoleService;
    private final UserPermissionServiceImpl userPermissionService;
    private final RolePermissionServiceImpl rolePermissionService;

    // =================== 用户认证 ===================

    @Override
    public Map<String, Object> login(String username, String password) {
        return userAuthService.login(username, password);
    }

    @Override
    public boolean logout(Long userId) {
        return userAuthService.logout(userId);
    }

    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        return userAuthService.refreshToken(refreshToken);
    }

    @Override
    public User validateToken(String token) {
        return userAuthService.validateToken(token);
    }

    // =================== 用户管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(User user) {
        return userManagementService.register(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        return userManagementService.updateUser(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        return userManagementService.deleteUser(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUsers(List<Long> userIds) {
        return userManagementService.deleteUsers(userIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) {
        return userManagementService.updateUserStatus(userId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId, String newPassword) {
        return userManagementService.resetPassword(userId, newPassword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        return userManagementService.changePassword(userId, oldPassword, newPassword);
    }

    // =================== 用户查询 ===================

    @Override
    public User getUserByUsername(String username) {
        return userManagementService.getUserByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return userManagementService.getUserByEmail(email);
    }

    @Override
    public User getUserByPhone(String phone) {
        return userManagementService.getUserByPhone(phone);
    }

    @Override
    public IPage<User> getUsersPage(Page<User> page, String username, String realName, String email, Integer status) {
        return userManagementService.getUsersPage(page, username, realName, email, status);
    }

    @Override
    public List<User> getUsersByRoleId(Long roleId) {
        return userManagementService.getUsersByRoleId(roleId);
    }

    // =================== 角色管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRole(Role role) {
        return userManagementService.addRole(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        return userManagementService.updateRole(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        return userManagementService.deleteRole(roleId);
    }

    @Override
    public List<Role> getAllRoles() {
        return userManagementService.getAllRoles();
    }

    @Override
    public IPage<Role> getRolesPage(Page<Role> page, String roleName, String roleCode, Integer status) {
        return userManagementService.getRolesPage(page, roleName, roleCode, status);
    }

    // =================== 权限管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPermission(Permission permission) {
        return userManagementService.addPermission(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(Permission permission) {
        return userManagementService.updatePermission(permission);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(Long permissionId) {
        return userManagementService.deletePermission(permissionId);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return userManagementService.getAllPermissions();
    }

    @Override
    public IPage<Permission> getPermissionsPage(Page<Permission> page, String permissionName, String permissionCode, 
                                                Integer permissionType, Integer status) {
        return userManagementService.getPermissionsPage(page, permissionName, permissionCode, permissionType, status);
    }

    // =================== 用户角色关系管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRolesToUser(Long userId, List<Long> roleIds) {
        return userRoleService.assignRolesToUser(userId, roleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeRolesFromUser(Long userId, List<Long> roleIds) {
        return userRoleService.removeRolesFromUser(userId, roleIds);
    }

    @Override
    public List<Role> getUserRoles(Long userId) {
        return userRoleService.getUserRoles(userId);
    }

    // =================== 角色权限关系管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        return rolePermissionService.assignPermissionsToRole(roleId, permissionIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        return rolePermissionService.removePermissionsFromRole(roleId, permissionIds);
    }

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        return rolePermissionService.getRolePermissions(roleId);
    }

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        return userPermissionService.getUserPermissions(userId);
    }

    // =================== 权限验证 ===================

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        return userPermissionService.hasPermission(userId, permissionCode);
    }

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        return userPermissionService.hasRole(userId, roleCode);
    }

    // =================== 统计信息 ===================

    @Override
    public Long getUserTotalCount() {
        return userManagementService.getUserTotalCount();
    }

    @Override
    public Map<String, Long> getUserCountByStatus() {
        return userManagementService.getUserCountByStatus();
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        return userManagementService.getUserStatistics();
    }
} 