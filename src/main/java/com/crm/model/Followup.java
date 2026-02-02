package com.crm.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

/**
 * 跟进记录实体类，对应followups表
 */
@Data
@Entity
@Table(name = "followups", indexes = {
    @Index(name = "idx_customer_id", columnList = "customer_id"),
    @Index(name = "idx_follow_time", columnList = "follow_time"),
    @Index(name = "idx_user_id", columnList = "user_id")
})
public class Followup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 跟进记录ID，自增主键

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;  // 客户ID，关联customers表

    @Column(name = "user_id", nullable = false)
    private Integer userId;  // 跟进人ID，关联users表

    @Column(name = "follow_time", nullable = false)
    private Date followTime;  // 跟进时间

    @Column(name = "follow_method", length = 20, nullable = false)
    private String followMethod;  // 跟进方式：电话、微信、面谈

    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;  // 跟进内容

    @Column(name = "next_follow_reminder")
    private Date nextFollowReminder;  // 下次跟进提醒时间

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;  // 创建时间

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;  // 更新时间

    // 外键约束：关联客户和跟进人
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Customer customer;  // 客户

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;  // 跟进人
}
