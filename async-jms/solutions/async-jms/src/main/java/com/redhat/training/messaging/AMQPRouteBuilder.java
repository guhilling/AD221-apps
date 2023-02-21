package com.redhat.training.messaging;

import org.apache.camel.builder.RouteBuilder;

public class AMQPRouteBuilder extends RouteBuilder {

	public static final String ROUTE_NAME = "amqp-order-input";

	@Override
	public void configure() {
		// TODO: receive messages from AMQP queue and send  to the log-orders route
		from("amqp:queue:amqp_order_input")
			.routeId(ROUTE_NAME)
			.log("AMQPRouteBuilder: Processing Non-delivered Orders")
			.to("direct:log_orders");
	}
}
