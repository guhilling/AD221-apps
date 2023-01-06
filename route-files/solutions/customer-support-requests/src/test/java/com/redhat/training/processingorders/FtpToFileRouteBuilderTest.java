package com.redhat.training.processingorders;

import io.quarkus.test.junit.QuarkusTest;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.quarkus.test.CamelQuarkusTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class FtpToFileRouteBuilderTest extends CamelQuarkusTestSupport {

    @Produce("direct:ftp")
    protected ProducerTemplate template;

    @Inject
    protected CamelContext context;

    @EndpointInject("mock:file:customer_requests")
    protected MockEndpoint fileMock;

    @BeforeEach
    void doAdvice() throws Exception {
        AdviceWith.adviceWith(context(), "ftpRoute", FtpToFileRouteBuilderTest::enhanceRoute);
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new FtpToFileRouteBuilder();
    }

    @Test
    void testFTPFileContentIsWrittenToFile() throws Exception {
        fileMock.message(0).body().isEqualTo("Hello World");
        template.sendBody("direct:ftp", "Hello World");
        fileMock.assertIsSatisfied();
    }

    private static void enhanceRoute(AdviceWithRouteBuilder route) {
        route.replaceFromWith("direct:ftp");
        route.interceptSendToEndpoint("file:.*customer_requests.*")
             .skipSendToOriginalEndpoint()
             .to("mock:file:customer_requests");
    }

}
