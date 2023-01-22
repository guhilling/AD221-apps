package com.redhat.training.bookpublishing.route;

import com.redhat.training.bookpublishing.strategy.DynamicRoutingStrategy;
import org.apache.camel.builder.RouteBuilder;

public class BookPrintingPipelineRouteBuilder extends RouteBuilder {
    private final String ROUTING_HEADER = "destination";

    @Override
    public void configure() throws Exception {
        // TODO: Create a route for the printing pipeline
    }
}
