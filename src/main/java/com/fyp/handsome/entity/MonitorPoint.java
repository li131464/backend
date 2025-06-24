package com.fyp.handsome.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 监控点位实体类
 * 对应数据库表：monitor_point
 * 存储监控设备的地理位置和状态信息
 * @author ziye
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("monitor_point")
public class MonitorPoint {

    /**
     * 监控点ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 监控点名称
     */
    @TableField("point_name")
    private String pointName;

    /**
     * 监控点编码（唯一）
     */
    @TableField("point_code")
    private String pointCode;

    /**
     * 纬度（精度到小数点后6位）
     */
    @TableField("latitude")
    private BigDecimal latitude;

    /**
     * 经度（精度到小数点后6位）
     */
    @TableField("longitude")
    private BigDecimal longitude;

    /**
     * 详细地址
     */
    @TableField("address")
    private String address;

    /**
     * 状态：1-在线，0-离线，2-维护中
     */
    @TableField("status")
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

    /**
     * 根据地址名称获取对应的经纬度坐标
     * 目前支持：上海浦东、北京朝阳
     * 
     * @param locationName 地址名称
     * @return MonitorPoint对象，包含计算好的经纬度
     */
    public static MonitorPoint createFromLocationName(String locationName) {
        MonitorPoint point = new MonitorPoint();
        
        // 根据地址名称设置经纬度坐标
        switch (locationName) {
            case "上海浦东":
                // 上海浦东新区大致中心位置坐标
                point.setLatitude(new BigDecimal("31.220000"));  // 纬度
                point.setLongitude(new BigDecimal("121.540000")); // 经度
                point.setAddress("上海市浦东新区");
                break;
                
            case "北京朝阳":
                // 北京朝阳区大致中心位置坐标
                point.setLatitude(new BigDecimal("39.920000"));   // 纬度
                point.setLongitude(new BigDecimal("116.450000")); // 经度
                point.setAddress("北京市朝阳区");
                break;
                
            default:
                // 如果不是预设的位置，设置为null，后续可以手动填充
                point.setLatitude(null);
                point.setLongitude(null);
                point.setAddress(locationName);
                break;
        }
        
        // 设置默认状态为在线
        point.setStatus(1);
        
        return point;
    }
} 