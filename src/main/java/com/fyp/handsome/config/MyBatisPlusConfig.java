package com.fyp.handsome.config;

import java.time.LocalDateTime;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

/**
 * MyBatis Plus 配置类
 * @author fyp
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 自动填充处理器
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            
            /**
             * 插入时自动填充
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                
                // 自动填充创建时间
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
                
                // 自动填充更新时间
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
            }

            /**
             * 更新时自动填充
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                LocalDateTime now = LocalDateTime.now();
                
                // 自动填充更新时间
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
            }
        };
    }
} 