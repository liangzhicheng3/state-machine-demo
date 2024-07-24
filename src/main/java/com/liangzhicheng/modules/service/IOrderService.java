package com.liangzhicheng.modules.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liangzhicheng.modules.entity.OrderEntity;

public interface IOrderService extends IService<OrderEntity> {

    void create(OrderEntity order);

    void pay(Long id);

    void deliver(Long id);

    void receive(Long id);

}