package com.redhat.training.helloworld;

import org.apache.camel.builder.RouteBuilder;

public class ApplicationRouteBuilder extends RouteBuilder {
    @Override
    public void configure() {
        from("scheduler:myScheduler?delay=2000")
        .routeId("Java DSL route")
        .setBody().simple("Current time is ${header.CamelTimerFiredTime}")
        .log("Sending message to the body logging route")
        .to("direct:log_body");
    }
}