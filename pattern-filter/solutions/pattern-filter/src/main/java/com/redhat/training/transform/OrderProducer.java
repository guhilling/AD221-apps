package com.redhat.training.transform;

import java.math.BigDecimal;
import java.util.Date;

import com.redhat.training.model.Customer;
import com.redhat.training.model.Order;
import com.redhat.training.model.OrderItem;

public class OrderProducer {
    private int orderNumber;

    public Order createOrder() {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setDelivered(++orderNumber % 2 == 0);
        order.setId(orderNumber);
        order.setCustomer(new Customer(
                          "Tony",
                          "Anderson",
                          "tanderson",
                          "password",
                          "tanderson@email.com",
                          false
                          )
        );
        order.getOrderItems().add(createOrderItem(2));
        order.getOrderItems().add(createOrderItem(3));
        return order;
    }

    public OrderItem createOrderItem(int id) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setExtPrice(BigDecimal.valueOf(110));
        return orderItem;
    }
}
