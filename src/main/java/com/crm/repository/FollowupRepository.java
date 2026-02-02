package com.crm.repository;

import com.crm.model.Followup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 跟进记录数据访问接口，对应followups表
 */
@Repository
public interface FollowupRepository extends JpaRepository<Followup, Integer> {

    /**
     * 根据客户ID查询跟进记录，支持分页
     * @param customerId 客户ID
     * @param pageable 分页参数
     * @return 分页跟进记录列表
     */
    Page<Followup> findByCustomerId(Integer customerId, Pageable pageable);

    /**
     * 根据跟进方式统计跟进记录数量
     * @return 跟进方式分布统计数据
     */
    @Query("SELECT f.followMethod, COUNT(f.id) FROM Followup f GROUP BY f.followMethod")
    List<Object[]> countByFollowMethod();

    /**
     * 根据跟进时间范围统计跟进记录数量
     * @param startDateTime 开始时间
     * @param endDateTime 结束时间
     * @return 跟进记录数量
     */
    long countByFollowTimeBetween(java.time.LocalDateTime startDateTime, java.time.LocalDateTime endDateTime);

}
