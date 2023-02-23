package com.redhat.training.transform;

import org.apache.camel.builder.RouteBuilder;

public class TransformRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        /* TODO: Marshal the XML data into JSON */
        /*
        from("activemq:queue:orderInput")
            .routeId("Transforming Orders")
            .marshal().jaxb()
            .log("XML Body: ${body}")
            .to("xj:identity?transformDirection=XML2JSON")
            .log("JSON Body: ${body}")
            .filter().jsonpath("$[?(@.delivered !='true')]")
            .wireTap("direct:jsonOrderLog")
        .to("mock:fulfillmentSystem");
         */
    }

}
