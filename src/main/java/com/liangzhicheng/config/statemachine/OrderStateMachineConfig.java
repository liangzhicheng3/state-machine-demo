package com.liangzhicheng.config.statemachine;

import com.liangzhicheng.common.enums.OrderStatusChangeEvent;
import com.liangzhicheng.common.enums.OrderStatusEnum;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static com.liangzhicheng.common.enums.OrderStatusEnum.*;
import static com.liangzhicheng.common.enums.OrderStatusChangeEvent.*;

@EnableStateMachine(name = "orderStateMachine")
@Configuration
public class OrderStateMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderStatusChangeEvent> {

    /**
     * 配置状态
     */
    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderStatusChangeEvent> states) throws Exception {
        states
                .withStates()
                .initial(WAIT_PAYMENT)
                .states(EnumSet.allOf(OrderStatusEnum.class));
    }

    /**
     * 配置状态转换事件关系
     */
    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderStatusChangeEvent> transitions) throws Exception {
        transitions
                //支付事件:待支付->待发货
                .withExternal().source(WAIT_PAYMENT).target(WAIT_DELIVER).event(PAYED)
                .and()
                //发货事件:待发货->待收货
                .withExternal().source(WAIT_DELIVER).target(WAIT_RECEIVE).event(DELIVERY)
                .and()
                //收货事件:待收货->已完成
                .withExternal().source(WAIT_RECEIVE).target(FINISH).event(RECEIVED);
    }

}