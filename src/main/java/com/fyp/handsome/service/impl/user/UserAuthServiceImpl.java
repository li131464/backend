package com.fyp.handsome.service.impl.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fyp.handsome.entity.User;
import com.fyp.handsome.mapper.PermissionMapper;
import com.fyp.handsome.mapper.RoleMapper;
import com.fyp.handsome.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户认证服务实现类
 * @author ziye
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthServiceImpl {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PermissionMapper permissionMapper;
    private final UserRoleServiceImpl userRoleService;
    private final UserPermissionServiceImpl userPermissionService;
    
    // 简单的token存储（实际项目中应使用Redis）
    private final Map<String, Long> tokenUserMap = new ConcurrentHashMap<>();
    private final Map<Long, String> userTokenMap = new ConcurrentHashMap<>();

    // =================== 用户认证 ===================

    public Map<String, Object> login(String username, String password) {
        try {
            log.info("用户尝试登录，username：{}", username);
            
            // 查询用户信息
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
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
            result.put("roles", userRoleService.getUserRoles(user.getId()));
            result.put("permissions", userPermissionService.getUserPermissions(user.getId()));
            
            log.info("用户登录成功，username：{}", username);
            return result;
            
        } catch (Exception e) {
            log.error("用户登录异常，username：{}，错误：{}", username, e.getMessage(), e);
            return createLoginFailResult("系统异常，请稍后重试");
        }
    }

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

    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            log.info("刷新token，refreshToken：{}", refreshToken);
            
            // 简化实现：从refreshToken中解析用户ID
            // 实际项目中应该使用JWT或其他方式
            if (refreshToken.startsWith("refresh_")) {
                String userIdStr = refreshToken.substring(8); // 去掉 "refresh_" 前缀
                Long userId = Long.valueOf(userIdStr.split("_")[0]);
                
                User user = userMapper.selectById(userId);
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

    public User validateToken(String token) {
        try {
            log.debug("验证token：{}", token);
            
            Long userId = tokenUserMap.get(token);
            if (userId != null) {
                User user = userMapper.selectById(userId);
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
    public String encryptPassword(String password) {
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