package com.redhat.training.processor;

import org.apache.camel.builder.RouteBuilder;

public class FileRouteBuilder extends RouteBuilder {

    private static final String SEPARATOR = System.getProperty("line.separator");

    @Override
    public void configure() throws Exception {
        from( "file:orders/incoming?noop=true" )
                // .process()
                .to( "file:orders/outgoing?fileName=orders2.csv" );
    }
}
