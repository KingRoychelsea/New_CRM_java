package com.crm.util;

import com.crm.model.Customer;
import com.crm.model.Followup;
import com.crm.model.User;
import com.crm.repository.CustomerRepository;
import com.crm.repository.FollowupRepository;
import com.crm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 测试数据生成器，用于生成测试数据
 */
@Component
public class TestDataGenerator implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FollowupRepository followupRepository;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();
    private final String[] customerSources = {"网站", "电话", "微信", "朋友介绍", "展会"};
    private final String[] followMethods = {"电话", "微信", "面谈"};
    private final String[] followContents = {
        "初次沟通，了解需求",
        "介绍产品功能",
        "讨论价格方案",
        "跟进合同签署",
        "售后服务跟进"
    };
    private final String[] names = {
        "张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
        "郑一", "王二", "陈三", "林四", "黄五", "杨六", "马七", "牛八"
    };
    private final String[] companies = {
        "阿里巴巴", "腾讯", "百度", "京东", "字节跳动",
        "美团", "拼多多", "小米", "华为", "OPPO"
    };

    /**
     * 生成测试数据
     */
    public void generateTestData() {
        // 确保有管理员用户
        ensureAdminUser();
        
        // 生成客户数据
        generateCustomers();
        
        // 生成跟进记录数据
        generateFollowups();
        
        System.out.println("测试数据生成完成！");
    }

    /**
     * 确保有管理员用户
     */
    private void ensureAdminUser() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(PasswordUtils.encryptPassword("123456"));
            admin.setNickname("管理员");
            admin.setRole("admin");
            userRepository.save(admin);
            System.out.println("创建管理员用户: admin / 123456");
        }
    }

    /**
     * 生成客户数据
     */
    private void generateCustomers() {
        // 如果已有客户数据，则不再生成
        if (customerRepository.count() > 0) {
            System.out.println("已有客户数据，跳过生成");
            return;
        }

        List<Customer> customers = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 生成30个客户，分布在最近30天内
        for (int i = 0; i < 30; i++) {
            Customer customer = new Customer();
            customer.setName(names[random.nextInt(names.length)]);
            customer.setPhone("138" + String.format("%08d", random.nextInt(100000000)));
            customer.setEmail(customer.getName() + "@example.com");
            customer.setCompany(companies[random.nextInt(companies.length)]);
            customer.setPosition("经理");
            customer.setSource(customerSources[random.nextInt(customerSources.length)]);
            customer.setNotes("测试客户");
            customer.setCreatedBy(1); // 管理员ID
            // 随机分布在最近30天内
            customer.setCreatedAt(now.minusDays(random.nextInt(30)));
            customers.add(customer);
        }

        customerRepository.saveAll(customers);
        System.out.println("生成了 " + customers.size() + " 个客户数据");
    }

    /**
     * 生成跟进记录数据
     */
    private void generateFollowups() {
        // 如果已有跟进记录数据，则不再生成
        if (followupRepository.count() > 0) {
            System.out.println("已有跟进记录数据，跳过生成");
            return;
        }

        List<Customer> customers = customerRepository.findAll();
        if (customers.isEmpty()) {
            System.out.println("无客户数据，无法生成跟进记录");
            return;
        }

        List<Followup> followups = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 为每个客户生成1-3条跟进记录
        for (Customer customer : customers) {
            int followupCount = 1 + random.nextInt(3);
            for (int i = 0; i < followupCount; i++) {
                Followup followup = new Followup();
                followup.setCustomerId(customer.getId());
                followup.setUserId(1); // 管理员ID
                // 跟进时间在客户创建时间之后
                LocalDateTime customerCreatedAt = customer.getCreatedAt();
                LocalDateTime followTime = customerCreatedAt.plusDays(random.nextInt(10));
                followup.setFollowTime(followTime);
                followup.setFollowMethod(followMethods[random.nextInt(followMethods.length)]);
                followup.setContent(followContents[random.nextInt(followContents.length)]);
                // 随机设置下次跟进提醒时间
                if (random.nextBoolean()) {
                    followup.setNextFollowReminder(followTime.plusDays(7 + random.nextInt(14)));
                }
                followups.add(followup);
            }
        }

        followupRepository.saveAll(followups);
        System.out.println("生成了 " + followups.size() + " 条跟进记录数据");
    }

    /**
     * 实现CommandLineRunner接口的run方法，在应用程序启动时自动执行
     * @param args 命令行参数
     */
    @Override
    public void run(String... args) {
        System.out.println("开始生成测试数据...");
        generateTestData();
        System.out.println("测试数据生成完成！");
    }
}
