package com.redhat.training.transform;

import java.util.Date;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;

import com.redhat.training.model.Order;
import com.redhat.training.model.OrderItem;

public class SampleDataRouteBuilder extends RouteBuilder {

    private int orderNumber;

    @Override
    public void configure() throws Exception {
        from( "timer://demo?period=5000")
            .routeId("Add Samples")
            .process(exchange -> exchange.getIn().setBody(createOrder()))
            .to("activemq:queue:orderInput");
    }

    private Order createOrder() {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setDelivered(++orderNumber%2==0);
        order.setId(orderNumber);
        order.getOrderItems().add(createOrderItem(2));
        order.getOrderItems().add(createOrderItem(3));
        return order;
    }

    private OrderItem createOrderItem(int id) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        return orderItem;
    }
}
