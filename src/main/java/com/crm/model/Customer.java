package com.crm.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 客户实体类，对应customers表
 */
@Data
@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_name_phone", columnList = "name, phone"),
    @Index(name = "idx_source", columnList = "source"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 客户ID，自增主键

    @Column(name = "name", length = 50, nullable = false)
    private String name;  // 客户姓名

    @Column(name = "phone", length = 20, nullable = false)
    private String phone;  // 手机号

    @Column(name = "email", length = 100)
    private String email;  // 邮箱

    @Column(name = "company", length = 100)
    private String company;  // 公司名称

    @Column(name = "position", length = 50)
    private String position;  // 职位

    @Column(name = "source", length = 50)
    private String source;  // 客户来源

    @Column(name = "notes", columnDefinition = "text")
    private String notes;  // 备注信息

    @Column(name = "created_by")
    private Integer createdBy;  // 创建人ID，关联users表

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;  // 创建时间

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;  // 更新时间

    // 外键约束：关联创建人
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User creator;  // 创建人
}
