package com.crm.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户实体类，对应users表
 */
@Data
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_username", columnList = "username")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 用户ID，自增主键

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;  // 用户名，唯一标识

    @Column(name = "password", length = 100, nullable = false)
    private String password;  // 密码，哈希加密存储

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;  // 用户昵称

    @Column(name = "role", length = 20, nullable = false, columnDefinition = "varchar(20) default 'user'")
    private String role;  // 用户角色，如admin、user

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;  // 创建时间

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;  // 更新时间
}
