package com.itheima.reggie.controller;

import com.itheima.reggie.commom.R;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.service.impl.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @PostMapping("/submit")
    public R<String> save(@RequestBody Orders orders){
        ordersService.submit(orders);
        log.info(orders.toString());
        return R.success("提交成功");
    }
}
