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

}
