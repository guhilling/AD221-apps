package com.redhat.training.messaging;

import java.util.Date;

import com.redhat.training.model.Order;

public class OrderProducer {
    private int orderNumber;

    public Order createOrder() {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setDelivered(++orderNumber % 2 == 0);
        order.setId(orderNumber);
        return order;
    }
}
