package com.redhat.training.bookpublishing.route;

import org.apache.camel.builder.RouteBuilder;

import com.redhat.training.bookpublishing.strategy.RoutingSlipStrategy;

public class BookReviewPipelineRouteBuilder extends RouteBuilder {
    private static final String ROUTING_HEADER = "destination";

    @Override
    public void configure() {
        // TODO: Create a route for the book review pipeline
    }
}
