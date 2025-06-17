package com.fyp.handsome.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyp.handsome.entity.Role;

/**
 * 角色Mapper接口
 * @author fyp
 */
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 根据角色名称查询角色信息
     * @param roleName 角色名称
     * @return 角色信息
     */
    @Select("SELECT * FROM role_info WHERE role_name = #{roleName} AND status = 1")
    Role selectByRoleName(@Param("roleName") String roleName);

    /**
     * 根据角色编码查询角色信息
     * @param roleCode 角色编码
     * @return 角色信息
     */
    @Select("SELECT * FROM role_info WHERE role_code = #{roleCode} AND status = 1")
    Role selectByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据角色名称模糊查询角色列表
     * @param roleName 角色名称
     * @return 角色列表
     */
    List<Role> selectByRoleNameLike(@Param("roleName") String roleName);

    /**
     * 根据状态查询角色列表
     * @param status 状态
     * @return 角色列表
     */
    @Select("SELECT * FROM role_info WHERE status = #{status} ORDER BY create_time DESC")
    List<Role> selectByStatus(@Param("status") Integer status);

    /**
     * 分页查询角色信息（带条件）- 复杂动态SQL，使用XML实现
     * @param page 分页参数
     * @param roleName 角色名称（可选）
     * @param roleCode 角色编码（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Role> selectPageWithConditions(Page<Role> page,
                                         @Param("roleName") String roleName,
                                         @Param("roleCode") String roleCode,
                                         @Param("status") Integer status);

    /**
     * 查询角色及其权限信息 - 复杂多表关联，使用XML实现
     * @param roleId 角色ID
     * @return 角色及权限信息
     */
    Role selectRoleWithPermissions(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询用户的角色列表（多表关联查询）
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM role_info r " +
            "INNER JOIN user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1")
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 查询所有启用的角色
     * @return 启用的角色列表
     */
    @Select("SELECT * FROM role_info WHERE status = 1 ORDER BY create_time DESC")
    List<Role> selectEnabledRoles();

    /**
     * 统计角色总数
     * @return 角色总数
     */
    @Select("SELECT COUNT(*) FROM role_info WHERE status = 1")
    Long countTotal();

    /**
     * 统计各状态角色数量
     * @return 统计结果列表
     */
    @Select("SELECT status, COUNT(*) as count FROM role_info GROUP BY status")
    List<Object> countByStatus();
} 