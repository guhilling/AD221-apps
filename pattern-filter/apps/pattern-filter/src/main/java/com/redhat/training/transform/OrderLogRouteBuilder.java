package com.redhat.training.transform;

import org.apache.camel.builder.RouteBuilder;

public class OrderLogRouteBuilder extends RouteBuilder {

    public void configure() throws Exception {
        // TODO: add direct route to mock order log end point
        /*
        from("direct:jsonOrderLog")
        .routeId("Log Orders")
        .log("Order received: ${body}")
        .to("mock:orderLog");
        */
    }
}
