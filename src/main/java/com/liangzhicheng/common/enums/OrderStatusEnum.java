package com.liangzhicheng.common.enums;

import lombok.Getter;

@Getter
public enum OrderStatusEnum {

    WAIT_PAYMENT(1, "待支付"),
    WAIT_DELIVER(2, "待发货"),
    WAIT_RECEIVE(3, "待收货"),
    FINISH(4, "已完成");

    private final Integer key;
    private final String desc;

    OrderStatusEnum(Integer key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public static OrderStatusEnum getByKey(Integer key){
        for(OrderStatusEnum orderStatus : values()){
            if(orderStatus.getKey().equals(key)){
                return orderStatus;
            }
        }
        throw new RuntimeException("enum not exists");
    }

}