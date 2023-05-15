package com.redhat.training.processor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.camel.builder.RouteBuilder;

public class FileRouteBuilder extends RouteBuilder {

    private static final String SEPARATOR = System.getProperty("line.separator");

    @Override
    public void configure() {
        from( "file:orders/incoming?noop=true" )
                .process(exchange -> {
                    String inputMessage = exchange.getIn().getBody( String.class );

                    AtomicReference<Long> counter = new AtomicReference<>(1L);

                    String processedLines = Stream.of(inputMessage.split(SEPARATOR))
                        .map( l -> counter.getAndUpdate( p -> p + 1 ).toString() + "," + l )
                        .collect(Collectors.joining(SEPARATOR));

                    exchange.getIn().setBody( processedLines );
                })
                .to( "file:orders/outgoing?fileName=orders2.csv" );
    }
}
