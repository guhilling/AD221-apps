package com.redhat.training.emergency.route;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.processor.idempotent.kafka.KafkaIdempotentRepository;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.redhat.training.emergency.model.Location;

public class EmergencyLocationRouteBuilder extends RouteBuilder {

    @ConfigProperty(name = "camel.component.kafka.brokers")
    String brokers;

    @Produces
    @ApplicationScoped
    @Named("kafkaIdempotentRepository")
    KafkaIdempotentRepository kafkaIdempotentRepository() {
        return new KafkaIdempotentRepository("idempotent-topic", brokers);
    }

    @Override
    public void configure() {
        onException(Exception.class).setBody(constant("errorOccured")).maximumRedeliveries(0).continued(true);

        from("file://../resources/data?fileName=locations.csv&noop=true")
        .routeId("emergency-location-route")
        .transform(body())
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
        .to("jdbc:dataSource")
        .to("direct:logger");

        from("direct:logger")
        .log("Location data transferred");

    }
}
