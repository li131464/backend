package com.fyp.handsome.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fyp.handsome.entity.Permission;

/**
 * 权限Mapper接口
 * @author fyp
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    /**
     * 根据权限名称查询权限信息
     * @param permissionName 权限名称
     * @return 权限信息
     */
    Permission selectByPermissionName(@Param("permissionName") String permissionName);

    /**
     * 根据权限编码查询权限信息
     * @param permissionCode 权限编码
     * @return 权限信息
     */
    Permission selectByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 根据权限名称模糊查询权限列表
     * @param permissionName 权限名称
     * @return 权限列表
     */
    List<Permission> selectByPermissionNameLike(@Param("permissionName") String permissionName);

    /**
     * 根据权限类型查询权限列表
     * @param permissionType 权限类型
     * @return 权限列表
     */
    List<Permission> selectByPermissionType(@Param("permissionType") Integer permissionType);

    /**
     * 根据状态查询权限列表
     * @param status 状态
     * @return 权限列表
     */
    List<Permission> selectByStatus(@Param("status") Integer status);

    /**
     * 分页查询权限信息（带条件）
     * @param page 分页参数
     * @param permissionName 权限名称（可选）
     * @param permissionCode 权限编码（可选）
     * @param permissionType 权限类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    IPage<Permission> selectPageWithConditions(Page<Permission> page,
                                               @Param("permissionName") String permissionName,
                                               @Param("permissionCode") String permissionCode,
                                               @Param("permissionType") Integer permissionType,
                                               @Param("status") Integer status);

    /**
     * 根据角色ID查询权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限列表
     * @param userId 用户ID
     * @return 权限列表
     */
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 查询所有启用的权限
     * @return 启用的权限列表
     */
    List<Permission> selectEnabledPermissions();

    /**
     * 根据权限编码列表查询权限
     * @param permissionCodes 权限编码列表
     * @return 权限列表
     */
    List<Permission> selectByPermissionCodes(@Param("permissionCodes") List<String> permissionCodes);

    /**
     * 统计权限总数
     * @return 权限总数
     */
    Long countTotal();

    /**
     * 统计各类型权限数量
     * @return 统计结果列表
     */
    List<Object> countByPermissionType();

    /**
     * 统计各状态权限数量
     * @return 统计结果列表
     */
    List<Object> countByStatus();
} 