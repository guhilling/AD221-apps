package com.redhat.training.downloader;

import io.quarkus.test.junit.QuarkusTest;

import javax.inject.Inject;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class HttpRouteBuilderTest extends CamelQuarkusTestSupport {

    @Inject
    protected ProducerTemplate template;

    // TODO: add @EndpointInject annotation
    // @EndpointInject("mock:http:localhost")
    protected MockEndpoint httpMockEndpoint;

    // TODO: add @EndpointInject annotation
    // @EndpointInject("mock:file:out")
    protected MockEndpoint fileMockEndpoint;

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new HttpRouteBuilder();
    }

    @Test
    void testFileRecievesContentFromHttpClient() throws InterruptedException {
        // TODO: add httpMockEndpoint behaviour
        // httpMockEndpoint.whenAnyExchangeReceived(e -> e.getIn().setBody("Hello test!"));

        // TODO: add fileMockEndpoint expectations
        /*
        fileMockEndpoint.expectedMessageCount(1);
        fileMockEndpoint.expectedBodiesReceived("Hello test!");
        */

        template.sendBody("direct:start", null);

        // TODO: assert fileMockEndpoint
        // fileMockEndpoint.assertIsSatisfied();
    }

    @BeforeEach
    void doAdvice() throws Exception {
        AdviceWith.adviceWith(context(), "http-route", this::adviceRoute);
    }

    private void adviceRoute(AdviceWithRouteBuilder route) {
        route.interceptSendToEndpoint("http://localhost/greeting")
             .skipSendToOriginalEndpoint()
             .to("mock:http:localhost");
        route.interceptSendToEndpoint("file:out?fileName=response.txt")
             .skipSendToOriginalEndpoint()
             .to("mock:file:out");
    }

}
