package com.crm.service;

import com.crm.model.Customer;
import com.crm.repository.CustomerRepository;
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
 * 客户服务类，处理客户相关的业务逻辑
 */
@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * 添加客户
     * @param customer 客户对象
     * @return 添加的客户对象
     */
    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * 根据客户ID获取客户信息
     * @param id 客户ID
     * @return 客户对象
     */
    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id).orElse(null);
    }

    /**
     * 更新客户信息
     * @param customer 客户对象
     * @return 更新后的客户对象
     */
    public Customer updateCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * 删除客户
     * @param id 客户ID
     * @return 是否删除成功
     */
    public boolean deleteCustomer(Integer id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * 获取客户列表，支持分页和筛选
     * @param page 页码（从1开始）
     * @param limit 每页数量
     * @param name 客户姓名（模糊查询）
     * @param phone 客户手机号（模糊查询）
     * @param source 客户来源
     * @return 分页客户列表
     */
    public Page<Customer> getCustomers(int page, int limit, String name, String phone, String source) {
        // 构建分页参数，按创建时间倒序排序
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 根据筛选条件查询
        if (source != null && !source.isEmpty()) {
            if (name != null && !name.isEmpty() && phone != null && !phone.isEmpty()) {
                return customerRepository.findByNameContainingAndPhoneContainingAndSource(name, phone, source, pageable);
            } else if (name != null && !name.isEmpty()) {
                return customerRepository.findByNameContainingAndPhoneContaining(name, "", pageable);
            } else if (phone != null && !phone.isEmpty()) {
                return customerRepository.findByNameContainingAndPhoneContaining("", phone, pageable);
            } else {
                return customerRepository.findBySource(source, pageable);
            }
        } else {
            return customerRepository.findByNameContainingAndPhoneContaining(name != null ? name : "", phone != null ? phone : "", pageable);
        }
    }

    /**
     * 获取客户来源分布统计
     * @return 客户来源分布统计数据
     */
    public Map<String, Long> getCustomerSourceStatistics() {
        List<Object[]> results = customerRepository.countBySource();
        Map<String, Long> statistics = new HashMap<>();
        for (Object[] result : results) {
            if (result[0] != null) {
                statistics.put(result[0].toString(), (Long) result[1]);
            }
        }
        return statistics;
    }

    /**
     * 获取客户数量统计（按时间段）
     * @param days 天数
     * @return 客户数量统计数据
     */
    public Map<String, Long> getCustomerCountStatistics(int days) {
        Map<String, Long> statistics = new HashMap<>();
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dateStr = date.toString();
            LocalDateTime startDateTime = date.atStartOfDay();
            LocalDateTime endDateTime = date.plusDays(1).atStartOfDay().minusSeconds(1);
            long count = customerRepository.countByCreatedAtBetween(startDateTime, endDateTime);
            statistics.put(dateStr, count);
        }
        
        return statistics;
    }

}
