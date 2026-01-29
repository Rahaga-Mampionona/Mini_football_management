package com.rahaga;
import com.rahaga.enums.OrderStatusEnum;
import com.rahaga.enums.OrderTypeEnum;

public class Order {
    private Integer id;
    private String reference;
    private OrderTypeEnum orderType;
    private OrderStatusEnum orderStatus;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public OrderTypeEnum getOrderType() { return orderType; }
    public void setOrderType(OrderTypeEnum orderType) { this.orderType = orderType; }

    public OrderStatusEnum getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatusEnum orderStatus) { this.orderStatus = orderStatus; }
}
    

