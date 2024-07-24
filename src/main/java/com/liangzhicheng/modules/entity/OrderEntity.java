package com.liangzhicheng.modules.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_order")
public class OrderEntity extends Model<OrderEntity> {

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编码
     */
    private String orderCode;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 订单名称
     */
    private String name;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 删除标记：0未删除，1已删除
     */
    private Integer deleteFlag;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}