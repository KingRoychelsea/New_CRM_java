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

}
