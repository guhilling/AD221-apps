package com.redhat.training.messaging;

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
class SoapRouteBuilderTest extends CamelQuarkusTestSupport {

    private static final String MOCK_RESULT_LOG = "mock:result_log";

    @EndpointInject(MOCK_RESULT_LOG)
    protected MockEndpoint resultLogEndpoint;

    @Inject
    protected ProducerTemplate producerTemplate;

    @Override
    protected RoutesBuilder[] createRouteBuilders() {
        return new RoutesBuilder[]{
            new SoapRouteBuilder(),
            new EnrichRouteBuilder()
        };
    }

    @Test
    void testSaopRoute() throws Exception {

	resultLogEndpoint.expectedMessageCount(1);

        String testOrder = "{\"ID\":5,\"Discount\":0.012,\"Delivered\":false,\"Desc\":\"Test Order 1\",\"Name\":\"customer-a\"}";

        // Sends messages to the start component
        producerTemplate.sendBody("direct:soap", testOrder);

        // Verifies that a message received
	    resultLogEndpoint.assertIsSatisfied();
    }

    @BeforeEach
    void doAdvice() throws Exception {
        AdviceWith.adviceWith(context(), SoapRouteBuilder.ROUTE_NAME, SoapRouteBuilderTest::adviceRoute);
    }

    private static void adviceRoute(AdviceWithRouteBuilder route) {
        route.interceptSendToEndpoint("direct:log_orders")
             .skipSendToOriginalEndpoint()
             .to(MOCK_RESULT_LOG);

    }

}