package com.fyp.handsome.service.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户管理服务实现类
 * @author ziye
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl extends ServiceImpl<UserMapper, User> {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final UserAuthServiceImpl userAuthService;

    // =================== 用户管理 ===================

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
            user.setPassword(userAuthService.encryptPassword(user.getPassword()));
            
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

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user) {
        try {
            return updateById(user);
        } catch (Exception e) {
            log.error("更新用户失败，userId：{}，错误：{}", user.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long userId) {
        try {
            // 先移除token映射
            userAuthService.logout(userId);
            return removeById(userId);
        } catch (Exception e) {
            log.error("删除用户失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUsers(List<Long> userIds) {
        try {
            // 先移除所有用户的token映射
            userIds.forEach(userAuthService::logout);
            return removeByIds(userIds);
        } catch (Exception e) {
            log.error("批量删除用户失败，userIds：{}，错误：{}", userIds, e.getMessage(), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) {
        try {
            // 如果禁用用户，则移除token
            if (status == 0) {
                userAuthService.logout(userId);
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

    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long userId, String newPassword) {
        try {
            User user = new User();
            user.setId(userId);
            user.setPassword(userAuthService.encryptPassword(newPassword));
            return updateById(user);
        } catch (Exception e) {
            log.error("重置密码失败，userId：{}，错误：{}", userId, e.getMessage(), e);
            return false;
        }
    }

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
            String encryptedOldPassword = userAuthService.encryptPassword(oldPassword);
            if (!encryptedOldPassword.equals(user.getPassword())) {
                log.warn("修改密码失败，旧密码错误：{}", userId);
                return false;
            }
            
            // 更新新密码
            user.setPassword(userAuthService.encryptPassword(newPassword));
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

    public User getUserByUsername(String username) {
        try {
            return getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        } catch (Exception e) {
            log.error("根据用户名查询用户失败，username：{}，错误：{}", username, e.getMessage(), e);
            return null;
        }
    }

    public User getUserByEmail(String email) {
        try {
            return getOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        } catch (Exception e) {
            log.error("根据邮箱查询用户失败，email：{}，错误：{}", email, e.getMessage(), e);
            return null;
        }
    }

    public User getUserByPhone(String phone) {
        try {
            return getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        } catch (Exception e) {
            log.error("根据手机号查询用户失败，phone：{}，错误：{}", phone, e.getMessage(), e);
            return null;
        }
    }

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

    public List<User> getUsersByRoleId(Long roleId) {
        try {
            return userMapper.selectUsersByRoleId(roleId);
        } catch (Exception e) {
            log.error("根据角色查询用户失败，roleId：{}，错误：{}", roleId, e.getMessage(), e);
            return List.of();
        }
    }

    // =================== 角色管理 ===================

    @Transactional(rollbackFor = Exception.class)
    public boolean addRole(Role role) {
        try {
            return roleMapper.insert(role) > 0;
        } catch (Exception e) {
            log.error("新增角色失败，roleName：{}，错误：{}", role.getRoleName(), e.getMessage(), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(Role role) {
        try {
            return roleMapper.updateById(role) > 0;
        } catch (Exception e) {
            log.error("更新角色失败，roleId：{}，错误：{}", role.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        try {
            return roleMapper.deleteById(roleId) > 0;
        } catch (Exception e) {
            log.error("删除角色失败，roleId：{}，错误：{}", roleId, e.getMessage(), e);
            return false;
        }
    }

    public List<Role> getAllRoles() {
        try {
            return roleMapper.selectList(new LambdaQueryWrapper<Role>().eq(Role::getStatus, 1));
        } catch (Exception e) {
            log.error("查询所有角色失败，错误：{}", e.getMessage(), e);
            return List.of();
        }
    }

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

    @Transactional(rollbackFor = Exception.class)
    public boolean addPermission(Permission permission) {
        try {
            return permissionMapper.insert(permission) > 0;
        } catch (Exception e) {
            log.error("新增权限失败，permissionName：{}，错误：{}", permission.getPermissionName(), e.getMessage(), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updatePermission(Permission permission) {
        try {
            return permissionMapper.updateById(permission) > 0;
        } catch (Exception e) {
            log.error("更新权限失败，permissionId：{}，错误：{}", permission.getId(), e.getMessage(), e);
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deletePermission(Long permissionId) {
        try {
            return permissionMapper.deleteById(permissionId) > 0;
        } catch (Exception e) {
            log.error("删除权限失败，permissionId：{}，错误：{}", permissionId, e.getMessage(), e);
            return false;
        }
    }

    public List<Permission> getAllPermissions() {
        try {
            return permissionMapper.selectList(new LambdaQueryWrapper<Permission>().eq(Permission::getStatus, 1));
        } catch (Exception e) {
            log.error("查询所有权限失败，错误：{}", e.getMessage(), e);
            return List.of();
        }
    }

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

    // =================== 统计信息 ===================

    public Long getUserTotalCount() {
        try {
            return count();
        } catch (Exception e) {
            log.error("统计用户总数失败，错误：{}", e.getMessage(), e);
            return 0L;
        }
    }

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
} 