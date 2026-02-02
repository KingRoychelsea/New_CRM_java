package com.crm.repository;

import com.crm.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 客户数据访问接口，对应customers表
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * 根据姓名和手机号模糊查询客户，支持分页
     * @param name 客户姓名
     * @param phone 客户手机号
     * @param pageable 分页参数
     * @return 分页客户列表
     */
    Page<Customer> findByNameContainingAndPhoneContaining(String name, String phone, Pageable pageable);

    /**
     * 根据来源查询客户，支持分页
     * @param source 客户来源
     * @param pageable 分页参数
     * @return 分页客户列表
     */
    Page<Customer> findBySource(String source, Pageable pageable);

    /**
     * 根据姓名、手机号和来源模糊查询客户，支持分页
     * @param name 客户姓名
     * @param phone 客户手机号
     * @param source 客户来源
     * @param pageable 分页参数
     * @return 分页客户列表
     */
    Page<Customer> findByNameContainingAndPhoneContainingAndSource(String name, String phone, String source, Pageable pageable);

    /**
     * 根据来源统计客户数量
     * @return 客户来源分布统计数据
     */
    @Query("SELECT c.source, COUNT(c.id) FROM Customer c GROUP BY c.source")
    List<Object[]> countBySource();

    /**
     * 根据创建时间范围统计客户数量
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 客户数量
     */
    long countByCreatedAtBetween(java.time.LocalDateTime startDateTime, java.time.LocalDateTime endDateTime);

}
