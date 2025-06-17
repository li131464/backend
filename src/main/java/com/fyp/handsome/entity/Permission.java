package com.fyp.handsome.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限信息实体类
 * 对应数据库表：permission_info
 * @author fyp
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permission_info")
public class Permission {

    /**
     * 权限ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 权限编码
     */
    @TableField("permission_code")
    private String permissionCode;

    /**
     * 权限描述
     */
    @TableField("permission_description")
    private String permissionDescription;

    /**
     * 权限类型：1-菜单，2-按钮，3-接口
     */
    @TableField("permission_type")
    private Integer permissionType;

    /**
     * 状态：1-正常，0-删除
     */
    @TableField("status")
    @TableLogic(value = "1", delval = "0")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
} 