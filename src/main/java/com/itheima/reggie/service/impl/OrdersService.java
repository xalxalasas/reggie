package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    public void  submit(Orders orders);//用户下单
}
