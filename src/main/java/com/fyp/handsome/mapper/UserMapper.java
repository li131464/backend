package com.fyp.handsome.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyp.handsome.entity.User;

/**
 * 用户信息Mapper接口
 * @author ziye
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM user_info WHERE username = #{username} AND status = 1")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户信息
     * @param email 邮箱
     * @return 用户信息
     */
    @Select("SELECT * FROM user_info WHERE email = #{email} AND status = 1")
    User selectByEmail(@Param("email") String email);

    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    @Select("SELECT * FROM user_info WHERE phone = #{phone} AND status = 1")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据用户名模糊查询用户列表
     * @param username 用户名
     * @return 用户列表
     */
    List<User> selectByUsernameLike(@Param("username") String username);

    /**
     * 根据真实姓名模糊查询用户列表
     * @param realName 真实姓名
     * @return 用户列表
     */
    List<User> selectByRealNameLike(@Param("realName") String realName);

    /**
     * 根据状态查询用户列表
     * @param status 状态
     * @return 用户列表
     */
    @Select("SELECT * FROM user_info WHERE status = #{status} ORDER BY create_time DESC")
    List<User> selectByStatus(@Param("status") Integer status);

    /**
     * 分页查询用户信息（带条件）- 复杂动态SQL，使用XML实现
     * @param page 分页参数
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param email 邮箱（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<User> selectPageWithConditions(Page<User> page,
                                         @Param("username") String username,
                                         @Param("realName") String realName,
                                         @Param("email") String email,
                                         @Param("status") Integer status);

    /**
     * 查询用户及其角色信息 - 复杂多表关联，使用XML实现
     * @param userId 用户ID
     * @return 用户及角色信息
     */
    User selectUserWithRoles(@Param("userId") Long userId);

    /**
     * 查询用户及其权限信息 - 复杂多表关联，使用XML实现
     * @param userId 用户ID
     * @return 用户及权限信息
     */
    User selectUserWithPermissions(@Param("userId") Long userId);

    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE user_info SET last_login_time = NOW() WHERE id = #{userId}")
    int updateLastLoginTime(@Param("userId") Long userId);

    /**
     * 统计用户总数
     * @return 用户总数
     */
    @Select("SELECT COUNT(*) FROM user_info WHERE status = 1")
    Long countTotal();

    /**
     * 统计各状态用户数量
     * @return 统计结果列表
     */
    @Select("SELECT status, COUNT(*) as count FROM user_info GROUP BY status")
    List<Object> countByStatus();

    /**
     * 根据角色ID查询用户列表
     * @param roleId 角色ID
     * @return 用户列表
     */
    @Select("SELECT u.* FROM user_info u " +
            "INNER JOIN user_role ur ON u.id = ur.user_id " +
            "WHERE ur.role_id = #{roleId} AND u.status = 1")
    List<User> selectUsersByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除用户的所有角色关联
     * @param userId 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    int deleteUserRoles(@Param("userId") Long userId);

    /**
     * 插入用户角色关联
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Insert("INSERT INTO user_role (user_id, role_id, create_time) VALUES (#{userId}, #{roleId}, NOW())")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户指定的角色关联
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user_role WHERE user_id = #{userId} AND role_id = #{roleId}")
    int deleteUserRoleByRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
} 