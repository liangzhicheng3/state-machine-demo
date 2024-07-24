package com.liangzhicheng.modules.service.listener;

import com.liangzhicheng.common.constant.Constants;
import com.liangzhicheng.common.enums.OrderStatusChangeEvent;
import com.liangzhicheng.common.enums.OrderStatusEnum;
import com.liangzhicheng.config.aop.annotation.ExecuteResult;
import com.liangzhicheng.modules.entity.OrderEntity;
import com.liangzhicheng.modules.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.liangzhicheng.common.enums.OrderStatusEnum.*;

@RequiredArgsConstructor
@Slf4j
@WithStateMachine(name = "orderStateMachine")
@Component("orderStatusListener")
public class OrderStatusListener {

    private final IOrderService orderService;
    private final StateMachine<OrderStatusEnum, OrderStatusChangeEvent> orderStateMachine;

    @ExecuteResult(key = Constants.PAY_TRANSITION)
    @Transactional(rollbackFor = Exception.class)
    @OnTransition(source = "WAIT_PAYMENT", target = "WAIT_DELIVER")
    public void payTransition(Message<OrderStatusChangeEvent> message) {
        OrderEntity order = (OrderEntity) message.getHeaders().get(Constants.ORDER_HEADER);
        log.info("支付，状态机反馈信息【{}】", message.getHeaders().toString());
        order.setStatus(WAIT_DELIVER.getKey());
        orderService.updateById(order);
        //TODO 其他业务
        //模拟异常
        if(Objects.equals(order.getName(), "A")){
            throw new RuntimeException("业务执行异常");
        }
    }

    @ExecuteResult(key = Constants.DELIVER_TRANSITION)
    @Transactional(rollbackFor = Exception.class)
    @OnTransition(source = "WAIT_DELIVER", target = "WAIT_RECEIVE")
    public void deliverTransition(Message<OrderStatusChangeEvent> message) {
        OrderEntity order = (OrderEntity) message.getHeaders().get(Constants.ORDER_HEADER);
        log.info("发货，状态机反馈信息【{}】", message.getHeaders().toString());
        order.setStatus(WAIT_RECEIVE.getKey());
        orderService.updateById(order);
        //TODO 其他业务
    }

    @ExecuteResult(key = Constants.RECEIVE_TRANSITION)
    @Transactional(rollbackFor = Exception.class)
    @OnTransition(source = "WAIT_RECEIVE", target = "FINISH")
    public void receiveTransition(Message<OrderStatusChangeEvent> message) {
        OrderEntity order = (OrderEntity) message.getHeaders().get(Constants.ORDER_HEADER);
        log.info("收货，状态机反馈信息【{}】", message.getHeaders().toString());
        order.setStatus(FINISH.getKey());
        orderService.updateById(order);
        //TODO 其他业务
    }

}