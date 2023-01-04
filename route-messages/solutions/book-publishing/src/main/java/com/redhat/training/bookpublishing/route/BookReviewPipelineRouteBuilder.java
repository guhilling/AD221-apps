package com.redhat.training.bookpublishing.route;

import org.apache.camel.builder.RouteBuilder;

import com.redhat.training.bookpublishing.strategy.RoutingSlipStrategy;

public class BookReviewPipelineRouteBuilder extends RouteBuilder {
    private static final String ROUTING_HEADER = "destination";

    @Override
    public void configure() {
        from("file://data/manuscripts?noop=true")
            .routeId("book-review-pipeline")
            .setHeader(ROUTING_HEADER).method(RoutingSlipStrategy.class)
            .log(String.format(
                "File: ${header.CamelFileName} - Destination: ${header.%s}",
                ROUTING_HEADER
            ))
        .routingSlip(header(ROUTING_HEADER));
    }
}
