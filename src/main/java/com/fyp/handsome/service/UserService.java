package com.fyp.handsome.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fyp.handsome.entity.Permission;
import com.fyp.handsome.entity.Role;
import com.fyp.handsome.entity.User;

/**
 * 用户服务接口
 * 负责用户认证、权限管理逻辑
 * @author ziye
 */
public interface UserService extends IService<User> {

    // =================== 用户认证 ===================

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含token等信息）
     */
    Map<String, Object> login(String username, String password);

    /**
     * 用户登出
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean logout(Long userId);

    /**
     * 刷新token
     * @param refreshToken 刷新令牌
     * @return 新的token信息
     */
    Map<String, Object> refreshToken(String refreshToken);

    /**
     * 验证用户身份
     * @param token 访问令牌
     * @return 用户信息
     */
    User validateToken(String token);

    // =================== 用户管理 ===================

    /**
     * 用户注册
     * @param user 用户信息
     * @return 是否成功
     */
    boolean register(User user);

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 是否成功
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId);

    /**
     * 批量删除用户
     * @param userIds 用户ID列表
     * @return 是否成功
     */
    boolean deleteUsers(List<Long> userIds);

    /**
     * 启用/禁用用户
     * @param userId 用户ID
     * @param status 状态（1-启用，0-禁用）
     * @return 是否成功
     */
    boolean updateUserStatus(Long userId, Integer status);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(Long userId, String newPassword);

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    // =================== 用户查询 ===================

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱查询用户
     * @param email 邮箱
     * @return 用户信息
     */
    User getUserByEmail(String email);

    /**
     * 根据手机号查询用户
     * @param phone 手机号
     * @return 用户信息
     */
    User getUserByPhone(String phone);

    /**
     * 分页查询用户（带条件）
     * @param page 分页参数
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param email 邮箱（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<User> getUsersPage(Page<User> page, String username, String realName, String email, Integer status);

    /**
     * 根据角色ID查询用户列表
     * @param roleId 角色ID
     * @return 用户列表
     */
    List<User> getUsersByRoleId(Long roleId);

    // =================== 角色管理 ===================

    /**
     * 新增角色
     * @param role 角色信息
     * @return 是否成功
     */
    boolean addRole(Role role);

    /**
     * 更新角色
     * @param role 角色信息
     * @return 是否成功
     */
    boolean updateRole(Role role);

    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 是否成功
     */
    boolean deleteRole(Long roleId);

    /**
     * 查询所有角色
     * @return 角色列表
     */
    List<Role> getAllRoles();

    /**
     * 分页查询角色
     * @param page 分页参数
     * @param roleName 角色名称（可选）
     * @param roleCode 角色编码（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Role> getRolesPage(Page<Role> page, String roleName, String roleCode, Integer status);

    // =================== 权限管理 ===================

    /**
     * 新增权限
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean addPermission(Permission permission);

    /**
     * 更新权限
     * @param permission 权限信息
     * @return 是否成功
     */
    boolean updatePermission(Permission permission);

    /**
     * 删除权限
     * @param permissionId 权限ID
     * @return 是否成功
     */
    boolean deletePermission(Long permissionId);

    /**
     * 查询所有权限
     * @return 权限列表
     */
    List<Permission> getAllPermissions();

    /**
     * 分页查询权限
     * @param page 分页参数
     * @param permissionName 权限名称（可选）
     * @param permissionCode 权限编码（可选）
     * @param permissionType 权限类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Permission> getPermissionsPage(Page<Permission> page, String permissionName, String permissionCode, 
                                         Integer permissionType, Integer status);

    // =================== 用户角色关系管理 ===================

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean assignRolesToUser(Long userId, List<Long> roleIds);

    /**
     * 移除用户角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 是否成功
     */
    boolean removeRolesFromUser(Long userId, List<Long> roleIds);

    /**
     * 查询用户的角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getUserRoles(Long userId);

    // =================== 角色权限关系管理 ===================

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds);

    /**
     * 移除角色权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 是否成功
     */
    boolean removePermissionsFromRole(Long roleId, List<Long> permissionIds);

    /**
     * 查询角色的权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getRolePermissions(Long roleId);

    /**
     * 查询用户的权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> getUserPermissions(Long userId);

    // =================== 权限验证 ===================

    /**
     * 检查用户是否有指定权限
     * @param userId 用户ID
     * @param permissionCode 权限编码
     * @return 是否有权限
     */
    boolean hasPermission(Long userId, String permissionCode);

    /**
     * 检查用户是否有指定角色
     * @param userId 用户ID
     * @param roleCode 角色编码
     * @return 是否有角色
     */
    boolean hasRole(Long userId, String roleCode);

    // =================== 统计信息 ===================

    /**
     * 统计用户总数
     * @return 用户总数
     */
    Long getUserTotalCount();

    /**
     * 统计各状态用户数量
     * @return 统计结果
     */
    Map<String, Long> getUserCountByStatus();

    /**
     * 获取用户统计概览
     * @return 统计概览信息
     */
    Map<String, Object> getUserStatistics();
} 