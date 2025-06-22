package com.fyp.handsome.service.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fyp.handsome.entity.Permission;
import com.fyp.handsome.entity.Role;
import com.fyp.handsome.entity.User;
import com.fyp.handsome.mapper.PermissionMapper;
import com.fyp.handsome.mapper.RoleMapper;
import com.fyp.handsome.mapper.UserMapper;
import com.fyp.handsome.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务实现类
 * @author fyp
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    
    // 简化的密码编码器（避免Spring Security依赖）
    // private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // 简单的token存储（实际项目中应使用Redis）
    private final Map<String, Long> tokenUserMap = new ConcurrentHashMap<>();
    private final Map<Long, String> userTokenMap = new ConcurrentHashMap<>();

    // =================== 用户认证 ===================

    @Override
    public Map<String, Object> login(String username, String password) {
        try {
            log.info("用户尝试登录，username：{}", username);
            
            // 查询用户信息
            User user = getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username)
                    .eq(User::getStatus, 1)); // 只查询启用状态的用户
            if (user == null) {
                log.warn("登录失败，用户不存在：{}", username);
                return createLoginFailResult("用户不存在");
            }
            
            // 检查用户状态
            if (user.getStatus() != 1) {
                log.warn("登录失败，用户已被禁用：{}", username);
                return createLoginFailResult("用户已被禁用");
            }
            
            // 验证密码（使用BCrypt）
            if (!verifyPassword(password, user.getPassword())) {
                log.warn("登录失败，密码错误：{}", username);
                return createLoginFailResult("密码错误");
            }
            
            // 更新最后登录时间
            userMapper.updateLastLoginTime(user.getId());
            
            // 生成token
            String token = generateToken(user);
            String refreshToken = generateRefreshToken(user);
            
            // 存储token映射关系
            tokenUserMap.put(token, user.getId());
            userTokenMap.put(user.getId(), token);
            
            // 构建登录成功结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("token", token);
            result.put("refreshToken", refreshToken);
            result.put("user", user);
            result.put("roles", getUserRoles(user.getId()));
            result.put("permissions", getUserPermissions(user.getId()));
            
            log.info("用户登录成功，username：{}", username);
            return result;
            
        } catch (Exception e) {
            log.error("用户登录异常，username：{}，错误：{}", username, e.getMessage(), e);
            return createLoginFailResult("系统异常，请稍后重试");
        }
    }

    @Override
    public boolean logout(Long userId) {
        try {
            log.info("用户登出，userId：{}", userId);
            
            // 移除token映射关系
            String token = userTokenMap.remove(userId);
            if (token != null) {
                tokenUserMap.remove(token);
            }
            
            log.info("用户登出成功，userId：{}", userId);
            return true;
        } catch (Exception e) {
            log.error("用户登出失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            log.info("刷新token，refreshToken：{}", refreshToken);
            
            // 简化实现：从refreshToken中解析用户ID
            // 实际项目中应该使用JWT或其他方式
            if (refreshToken.startsWith("refresh_")) {
                String userIdStr = refreshToken.substring(8); // 去掉 "refresh_" 前缀
                Long userId = Long.valueOf(userIdStr.split("_")[0]);
                
                User user = getById(userId);
                if (user != null && user.getStatus() == 1) {
                    // 生成新的token
                    String newToken = generateToken(user);
                    String newRefreshToken = generateRefreshToken(user);
                    
                    // 更新token映射
                    String oldToken = userTokenMap.get(userId);
                    if (oldToken != null) {
                        tokenUserMap.remove(oldToken);
                    }
                    tokenUserMap.put(newToken, userId);
                    userTokenMap.put(userId, newToken);
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("token", newToken);
                    result.put("refreshToken", newRefreshToken);
                    return result;
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "refreshToken无效");
            return result;
        } catch (Exception e) {
            log.error("刷新token失败，refreshToken：{}，错误：{}", refreshToken, e.getMessage(), e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "token刷新失败");
            return result;
        }
    }

    @Override
    public User validateToken(String token) {
        try {
            log.debug("验证token：{}", token);
            
            Long userId = tokenUserMap.get(token);
            if (userId != null) {
                User user = getById(userId);
                if (user != null && user.getStatus() == 1) {
                    return user;
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("token验证失败，token：{}，错误：{}", token, e.getMessage(), e);
            return null;
        }
    }

    // =================== 用户管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(User user) {
        try {
            log.info("用户注册，username：{}", user.getUsername());
            
            // 检查用户名是否已存在
            if (getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, user.getUsername())
                    .last("LIMIT 1")) != null) {
                log.warn("注册失败，用户名已存在：{}", user.getUsername());
                return false;
            }
            
            // 检查邮箱是否已存在
            if (StringUtils.hasText(user.getEmail()) && 
                getOne(new LambdaQueryWrapper<User>()
                        .eq(User::getEmail, user.getEmail())
                        .last("LIMIT 1")) != null) {
                log.warn("注册失败，邮箱已存在：{}", user.getEmail());
                return false;
            }
            
            // 密码加密（使用BCrypt）
            user.setPassword(encryptPassword(user.getPassword()));
            
            // 设置默认状态
            if (user.getStatus() == null) {
                user.setStatus(1);
            }
            
            // 保存用户
            boolean result = save(user);
            
            if (result) {
                log.info("用户注册成功，userId：{}", user.getId());
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("用户注册失败，username：{}，错误：{}", user.getUsername(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        try {
            return updateById(user);
        } catch (Exception e) {
            log.error("更新用户失败，userId：{}，错误：{}", user.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        try {
            // 先移除token映射
            logout(userId);
            return removeById(userId);
        } catch (Exception e) {
            log.error("删除用户失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUsers(List<Long> userIds) {
        try {
            // 先移除所有用户的token映射
            userIds.forEach(this::logout);
            return removeByIds(userIds);
        } catch (Exception e) {
            log.error("批量删除用户失败，userIds：{}，错误：{}", userIds, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) {
        try {
            // 如果禁用用户，则移除token
            if (status == 0) {
                logout(userId);
            }
            
            User user = new User();
            user.setId(userId);
            user.setStatus(status);
            return updateById(user);
        } catch (Exception e) {
            log.error("更新用户状态失败，userId：{}，status：{}，错误：{}", userId, status, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId, String newPassword) {
        try {
            User user = new User();
            user.setId(userId);
            user.setPassword(encryptPassword(newPassword));
            return updateById(user);
        } catch (Exception e) {
            log.error("重置密码失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        try {
            // 获取用户当前密码
            User user = getById(userId);
            if (user == null) {
                log.warn("修改密码失败，用户不存在：{}", userId);
                return false;
            }
            
            // 验证旧密码
            if (!verifyPassword(oldPassword, user.getPassword())) {
                log.warn("修改密码失败，旧密码错误：{}", userId);
                return false;
            }
            
            // 更新新密码
            user.setPassword(encryptPassword(newPassword));
            boolean result = updateById(user);
            
            if (result) {
                log.info("用户密码修改成功，userId：{}", userId);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("修改密码失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return false;
        }
    }

    // =================== 用户查询 ===================

    @Override
    public User getUserByUsername(String username) {
        try {
            return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        } catch (Exception e) {
            log.error("根据用户名查询用户失败，username：{}，错误：{}", username, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            return getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        } catch (Exception e) {
            log.error("根据邮箱查询用户失败，email：{}，错误：{}", email, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public User getUserByPhone(String phone) {
        try {
            return getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        } catch (Exception e) {
            log.error("根据手机号查询用户失败，phone：{}，错误：{}", phone, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public IPage<User> getUsersPage(Page<User> page, String username, String realName, String email, Integer status) {
        try {
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            
            if (StringUtils.hasText(username)) {
                wrapper.like(User::getUsername, username);
            }
            if (StringUtils.hasText(realName)) {
                wrapper.like(User::getRealName, realName);
            }
            if (StringUtils.hasText(email)) {
                wrapper.like(User::getEmail, email);
            }
            if (status != null) {
                wrapper.eq(User::getStatus, status);
            }
            
            return page(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询用户失败，错误：{}", e.getMessage(), e);
            return new Page<>();
        }
    }

    @Override
    public List<User> getUsersByRoleId(Long roleId) {
        try {
            return userMapper.selectUsersByRoleId(roleId);
        } catch (Exception e) {
            log.error("根据角色查询用户失败，roleId：{}，错误：{}", roleId, e.getMessage(), e);
            return List.of();
        }
    }

    // =================== 角色管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRole(Role role) {
        try {
            return roleMapper.insert(role) > 0;
        } catch (Exception e) {
            log.error("新增角色失败，roleName：{}，错误：{}", role.getRoleName(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        try {
            return roleMapper.updateById(role) > 0;
        } catch (Exception e) {
            log.error("更新角色失败，roleId：{}，错误：{}", role.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        try {
            return roleMapper.deleteById(roleId) > 0;
        } catch (Exception e) {
            log.error("删除角色失败，roleId：{}，错误：{}", roleId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Role> getAllRoles() {
        try {
            return roleMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getStatus, 1));
        } catch (Exception e) {
            log.error("查询所有角色失败，错误：{}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public IPage<Role> getRolesPage(Page<Role> page, String roleName, String roleCode, Integer status) {
        try {
            LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
            
            if (StringUtils.hasText(roleName)) {
                wrapper.like(Role::getRoleName, roleName);
            }
            if (StringUtils.hasText(roleCode)) {
                wrapper.like(Role::getRoleCode, roleCode);
            }
            if (status != null) {
                wrapper.eq(Role::getStatus, status);
            }
            
            return roleMapper.selectPage(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询角色失败，错误：{}", e.getMessage(), e);
            return new Page<>();
        }
    }

    // =================== 权限管理 ===================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addPermission(Permission permission) {
        try {
            return permissionMapper.insert(permission) > 0;
        } catch (Exception e) {
            log.error("新增权限失败，permissionName：{}，错误：{}", permission.getPermissionName(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(Permission permission) {
        try {
            return permissionMapper.updateById(permission) > 0;
        } catch (Exception e) {
            log.error("更新权限失败，permissionId：{}，错误：{}", permission.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(Long permissionId) {
        try {
            return permissionMapper.deleteById(permissionId) > 0;
        } catch (Exception e) {
            log.error("删除权限失败，permissionId：{}，错误：{}", permissionId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Permission> getAllPermissions() {
        try {
            return permissionMapper.selectList(new LambdaQueryWrapper<Permission>().eq(Permission::getStatus, 1));
        } catch (Exception e) {
            log.error("查询所有权限失败，错误：{}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public IPage<Permission> getPermissionsPage(Page<Permission> page, String permissionName, String permissionCode, 
                                                Integer permissionType, Integer status) {
        try {
            LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
            
            if (StringUtils.hasText(permissionName)) {
                wrapper.like(Permission::getPermissionName, permissionName);
            }
            if (StringUtils.hasText(permissionCode)) {
                wrapper.like(Permission::getPermissionCode, permissionCode);
            }
            if (permissionType != null) {
                wrapper.eq(Permission::getPermissionType, permissionType);
            }
            if (status != null) {
                wrapper.eq(Permission::getStatus, status);
            }
            
            return permissionMapper.selectPage(page, wrapper);
        } catch (Exception e) {
            log.error("分页查询权限失败，错误：{}", e.getMessage(), e);
            return new Page<>();
        }
    }

    // =================== 用户角色关系管理 ===================

    @Override
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

    @Override
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

    @Override
    public List<Role> getUserRoles(Long userId) {
        try {
            return roleMapper.selectRolesByUserId(userId);
        } catch (Exception e) {
            log.error("查询用户角色失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return List.of();
        }
    }

    // =================== 角色权限关系管理 ===================

    @Override
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

    @Override
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

    @Override
    public List<Permission> getRolePermissions(Long roleId) {
        try {
            return permissionMapper.selectPermissionsByRoleId(roleId);
        } catch (Exception e) {
            log.error("查询角色权限失败，roleId：{}，错误：{}", roleId, e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public List<Permission> getUserPermissions(Long userId) {
        try {
            return permissionMapper.selectPermissionsByUserId(userId);
        } catch (Exception e) {
            log.error("查询用户权限失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return List.of();
        }
    }

    // =================== 权限验证 ===================

    @Override
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

    @Override
    public boolean hasRole(Long userId, String roleCode) {
        try {
            List<Role> roles = getUserRoles(userId);
            return roles.stream()
                    .anyMatch(role -> role.getRoleCode().equals(roleCode));
        } catch (Exception e) {
            log.error("检查用户角色失败，userId：{}，roleCode：{}，错误：{}", userId, roleCode, e.getMessage(), e);
            return false;
        }
    }

    // =================== 统计信息 ===================

    @Override
    public Long getUserTotalCount() {
        try {
            return count();
        } catch (Exception e) {
            log.error("统计用户总数失败，错误：{}", e.getMessage(), e);
            return 0L;
        }
    }

    @Override
    public Map<String, Long> getUserCountByStatus() {
        try {
            List<Object> results = userMapper.countByStatus();
            Map<String, Long> countMap = new HashMap<>();
            
            // 处理查询结果，转换为Map格式
            for (Object result : results) {
                if (result instanceof Object[] row) {
                    Integer status = (Integer) row[0];
                    Long count = ((Number) row[1]).longValue();
                    String statusKey = status == 1 ? "active" : "inactive";
                    countMap.put(statusKey, count);
                }
            }
            
            return countMap;
        } catch (Exception e) {
            log.error("统计用户状态分布失败，错误：{}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> getUserStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // 用户总数
            Long totalCount = getUserTotalCount();
            statistics.put("totalCount", totalCount);
            
            // 各状态用户数量
            Map<String, Long> statusCount = getUserCountByStatus();
            statistics.put("statusCount", statusCount);
            
            // 今日新增用户数量（简化实现，可根据需要扩展）
            Long todayCount = count(new LambdaQueryWrapper<User>()
                    .ge(User::getCreateTime, java.time.LocalDate.now().atStartOfDay()));
            statistics.put("todayCount", todayCount);
            
            // 最近登录用户数量（最近7天有登录记录的用户）
            Long recentLoginCount = count(new LambdaQueryWrapper<User>()
                    .ge(User::getLastLoginTime, java.time.LocalDateTime.now().minusDays(7)));
            statistics.put("recentLoginCount", recentLoginCount);
            
            return statistics;
        } catch (Exception e) {
            log.error("获取用户统计信息失败，错误：{}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    // =================== 私有辅助方法 ===================

    /**
     * 创建登录失败结果
     */
    private Map<String, Object> createLoginFailResult(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        return result;
    }

    /**
     * 生成访问token（简化实现）
     */
    private String generateToken(User user) {
        return "token_" + user.getId() + "_" + System.currentTimeMillis();
    }

    /**
     * 生成刷新token（简化实现）
     */
    private String generateRefreshToken(User user) {
        return "refresh_" + user.getId() + "_" + UUID.randomUUID().toString();
    }

    /**
     * 密码加密（简化实现，实际项目中建议使用BCrypt）
     */
    private String encryptPassword(String password) {
        // 简化实现：使用MD5或直接加前缀（实际项目中应使用BCrypt）
        return "encrypted_" + password;
    }

    /**
     * 验证密码（简化实现，实际项目中建议使用BCrypt）
     */
    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        // 简化实现：直接比较（实际项目中应使用BCrypt）
        return ("encrypted_" + rawPassword).equals(encodedPassword);
    }
} 