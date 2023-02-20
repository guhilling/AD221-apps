package com.redhat.training.transform;

import org.apache.camel.builder.RouteBuilder;

public class SampleDataRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        OrderProducer orderProducer = new OrderProducer();
        System.setProperty("org.apache.activemq.SERIALIZABLE_PACKAGES", "*");
        from( "timer://demo?period=5000")
            .routeId("Add Samples")
            .process(exchange -> exchange.getIn().setBody(orderProducer.createOrder()))
            .to("activemq:queue:orderInput");
    }

}
