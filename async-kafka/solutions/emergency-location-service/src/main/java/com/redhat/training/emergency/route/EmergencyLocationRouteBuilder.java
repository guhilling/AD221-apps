package com.redhat.training.emergency.route;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;

import com.redhat.training.emergency.model.Location;

public class EmergencyLocationRouteBuilder extends RouteBuilder {


    @Override
    public void configure() {
        onException(Exception.class).setBody(constant("errorOccured")).maximumRedeliveries(0).continued(true);

        from("file://../resources/data?fileName=locations.csv&noop=true")
        .routeId("emergency-location-route")
        .transform(body())
        .log("file read and transformed")
        .unmarshal()
        .bindy(BindyType.Csv, Location.class)
        .split(body())
        // TODO: Produce to Location data to Kafka instead of writing it to the database
        .to("kafka:locations")
        .to("direct:logger");


        // TODO: Consume Location data from `locations` Kafka topic and write it to the database
        from("kafka:locations")
        .routeId("kafka-consumer-route")
        .setBody(simple("insert into locations values('${body.latitude}','${body.longitude}')"))
        .to("direct:logger");

        from("direct:logger")
        .routeId("log-route")
        .log("Location data transferred");

    }
}
