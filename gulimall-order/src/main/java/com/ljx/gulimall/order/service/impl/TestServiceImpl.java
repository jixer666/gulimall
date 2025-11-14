package com.ljx.gulimall.order.service.impl;

import com.ljx.gulimall.order.dao.OrderDao;
import com.ljx.gulimall.order.service.TestService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private OrderDao orderDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save() {
        orderDao.saveTest(new Test(2, "222"));
        test1();
        orderDao.saveTest(new Test(3, "333"));
    }
    @Transactional(rollbackFor = Exception.class)
    public void test1() {
        orderDao.saveTest(new Test(1, "111"));
        int a = 1 / 0;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Test {
        private Integer id;
        private String content;
    }
}
