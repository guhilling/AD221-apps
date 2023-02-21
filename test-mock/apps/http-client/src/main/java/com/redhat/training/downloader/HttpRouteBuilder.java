package com.redhat.training.downloader;

import org.apache.camel.builder.RouteBuilder;

public class HttpRouteBuilder extends RouteBuilder {

    @Override
    public void configure() {
        // TODO: use property placeholders
        from("{{http_route.start}}")
        .id("http-route")
            .to("{{http_route.server}}/greeting")
            .to("file:out?fileName=response.txt");
    }
}

