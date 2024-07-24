package com.liangzhicheng.modules.controller;

import com.liangzhicheng.modules.entity.OrderEntity;
import com.liangzhicheng.modules.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 现态：是指当前所处的状态。待支付
 * 条件：又称为"事件"，当一个条件被满足，将会触发一个动作或者执行一次状态的迁移。支付事件
 * 动作：条件满足后执行的动作，动作执行完毕后，可以迁移到新的状态，也可以仍旧保持原状态，动作不是必需的，当条件满足后，也可以不执行任何动作，直接迁移到新状态。状态转换为待发货
 *
 * 次态：条件满足后要迁移新的状态。"次态"是相对于"现态"而言，"次态"一旦被激活就转变成新的"现态"。待发货 注意事项
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/order")
public class OrderController {

    private final IOrderService orderService;

    /**
     * 根据id查询订单
     */
    @RequestMapping("/get-by-id/{id}")
    public OrderEntity getById(@PathVariable("id") Long id){
        return orderService.getById(id);
    }

    /**
     * 创建订单
     */
    @RequestMapping("/create")
    public String create(@RequestBody OrderEntity order){
        orderService.create(order);
        return "success";
    }

    /**
     * 订单支付
     */
    @RequestMapping("/pay")
    public String pay(@RequestParam("id") Long id){
        orderService.pay(id);
        return "success";
    }

    /**
     * 订单发货
     */
    @RequestMapping("/deliver")
    public String deliver(@RequestParam("id") Long id){
        orderService.deliver(id);
        return "success";
    }

    /**
     * 订单确认收货
     */
    @RequestMapping("/receive")
    public String receive(@RequestParam("id") Long id){
        orderService.receive(id);
        return "success";
    }

}