package com.crm.service;

import com.crm.model.Followup;
import com.crm.repository.FollowupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 跟进记录服务类，处理跟进记录相关的业务逻辑
 */
@Service
@Transactional
public class FollowupService {

    @Autowired
    private FollowupRepository followupRepository;

    /**
     * 添加跟进记录
     * @param followup 跟进记录对象
     * @return 添加的跟进记录对象
     */
    public Followup addFollowup(Followup followup) {
        return followupRepository.save(followup);
    }

    /**
     * 根据跟进记录ID获取跟进记录信息
     * @param id 跟进记录ID
     * @return 跟进记录对象
     */
    public Followup getFollowupById(Integer id) {
        return followupRepository.findById(id).orElse(null);
    }

    /**
     * 删除跟进记录
     * @param id 跟进记录ID
     * @return 是否删除成功
     */
    public boolean deleteFollowup(Integer id) {
        if (followupRepository.existsById(id)) {
            followupRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 获取跟进记录列表，支持分页和按客户筛选
     * @param page 页码（从1开始）
     * @param limit 每页数量
     * @param customerId 客户ID
     * @return 分页跟进记录列表
     */
    public Page<Followup> getFollowups(int page, int limit, Integer customerId) {
        // 构建分页参数，按跟进时间倒序排序
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "followTime"));

        // 根据客户ID筛选
        if (customerId != null) {
            return followupRepository.findByCustomerId(customerId, pageable);
        } else {
            return followupRepository.findAll(pageable);
        }
    }

    /**
     * 获取跟进方式分布统计
     * @return 跟进方式分布统计数据
     */
    public Map<String, Long> getFollowupMethodStatistics() {
        List<Object[]> results = followupRepository.countByFollowMethod();
        Map<String, Long> statistics = new HashMap<>();
        for (Object[] result : results) {
            if (result[0] != null) {
                statistics.put(result[0].toString(), (Long) result[1]);
            }
        }
        return statistics;
    }

    /**
     * 获取跟进记录数量统计（按时间段）
     * @param days 天数
     * @return 跟进记录数量统计数据
     */
    public Map<String, Long> getFollowupCountStatistics(int days) {
        Map<String, Long> statistics = new HashMap<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.toString();
            LocalDateTime startDateTime = date.atStartOfDay();
            LocalDateTime endDateTime = date.plusDays(1).atStartOfDay().minusSeconds(1);
            long count = followupRepository.countByFollowTimeBetween(startDateTime, endDateTime);
            statistics.put(dateStr, count);
        }
        
        return statistics;
    }

}
