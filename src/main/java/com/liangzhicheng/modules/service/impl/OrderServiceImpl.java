package com.liangzhicheng.modules.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.enums.OrderStatusChangeEvent;
import com.liangzhicheng.common.enums.OrderStatusEnum;
import com.liangzhicheng.modules.entity.OrderEntity;
import com.liangzhicheng.modules.mapper.IOrderMapper;
import com.liangzhicheng.modules.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.liangzhicheng.common.enums.OrderStatusEnum.*;
import static com.liangzhicheng.common.enums.OrderStatusChangeEvent.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<IOrderMapper, OrderEntity> implements IOrderService {

    private final StateMachine<OrderStatusEnum, OrderStatusChangeEvent> orderStateMachine;
    private final StateMachinePersister<OrderStatusEnum, OrderStatusChangeEvent, String> stateMachinePersist;

    @Override
    public void create(OrderEntity order) {
        order.setStatus(WAIT_PAYMENT.getKey());
        baseMapper.insert(order);
    }

    @Override
    public void pay(Long id) {
        OrderEntity order = super.getById(id);
        log.info("线程名称【{}】，尝试支付，订单号【{}】", Thread.currentThread().getName(), id);
        if(!sendEvent(PAYED, order, Constants.PAY_TRANSITION)){
            log.error("线程名称【{}】，支付失败，状态异常，订单信息【{}】", Thread.currentThread().getName(), order);
            throw new RuntimeException("支付失败，订单状态异常");
        }
    }

    @Override
    public void deliver(Long id) {
        OrderEntity order = super.getById(id);
        log.info("线程名称【{}】，尝试发货，订单号【{}】", Thread.currentThread().getName(), id);
        if(!sendEvent(DELIVERY, order, Constants.DELIVER_TRANSITION)){
            log.error("线程名称【{}】，发货失败，状态异常，订单信息【{}】", Thread.currentThread().getName(), order);
            throw new RuntimeException("发货失败，订单状态异常");
        }
    }

    @Override
    public void receive(Long id) {
        OrderEntity order = super.getById(id);
        log.info("线程名称【{}】，尝试收货，订单号【{}】", Thread.currentThread().getName(), id);
        if(!sendEvent(RECEIVED, order, Constants.RECEIVE_TRANSITION)){
            log.error("线程名称【{}】，收货失败，状态异常，订单信息【{}】", Thread.currentThread().getName(), order);
            throw new RuntimeException("收货失败，订单状态异常");
        }
    }

    /**
     * 发送订单状态转换事件，synchronized修饰保证这个方法是线程安全的
     */
    private synchronized Boolean sendEvent(OrderStatusChangeEvent changeEvent, OrderEntity order, String key) {
        Boolean result = Boolean.FALSE;
        try{
            //启动状态机
            orderStateMachine.start();
            //尝试恢复状态机状态
            stateMachinePersist.restore(orderStateMachine, String.valueOf(order.getId()));
            Message message = MessageBuilder
                    .withPayload(changeEvent)
                    .setHeader(Constants.ORDER_HEADER, order)
                    .build();
            result = orderStateMachine.sendEvent(message);
            if(!result){
                return Boolean.FALSE;
            }
            //获取到监听的结果信息
            Integer flag = (Integer) orderStateMachine.getExtendedState().getVariables().get(key + order.getId());
            //操作完成之后，删除本次对应的key信息
            orderStateMachine.getExtendedState().getVariables().remove(key + order.getId());
            //如果事务执行成功，则持久化状态机，否则事务执行失败，订单业务执行异常
            if(Objects.equals(1, Integer.valueOf(flag))){
                stateMachinePersist.persist(orderStateMachine, String.valueOf(order.getId()));
            }else{
                return Boolean.FALSE;
            }
        }catch (Exception e){
            log.error("订单操作失败，异常信息【{}】", e.getMessage());
        }finally{
            orderStateMachine.stop();
        }
        return result;
    }

}