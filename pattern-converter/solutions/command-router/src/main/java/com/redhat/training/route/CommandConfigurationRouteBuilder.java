package com.redhat.training.route;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.dataformat.BindyType;

import com.redhat.training.model.CommandConfiguration;
import com.redhat.training.model.CommandConfigurationCSVRecord;

public class CommandConfigurationRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        onException(Exception.class).continued(true);
        
        from("file:../resources/data?noop=true&fileName=config.csv")
        .routeId("air-purifier-configuration-route")
        .unmarshal()
        .bindy(BindyType.Csv, CommandConfigurationCSVRecord.class)
        .split(body())
        // TODO: Comment-out or remove the following conversion to JSON
        //.marshal(new JacksonDataFormat(CommandConfigurationCSVRecord.class))
        .convertBodyTo(CommandConfiguration.class)
        .log("sending body: ${body}")
        .removeHeaders("CamelHttp*")
        .setHeader(Exchange.HTTP_METHOD, constant("POST"))
        .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
        .to("http://localhost:8081/commands")
        .setBody(simple("${header.CamelHttpResponseCode}"))
        .to("direct:logReturnCode");

        from("direct:logReturnCode")
        .log("${body}");

    }
}
