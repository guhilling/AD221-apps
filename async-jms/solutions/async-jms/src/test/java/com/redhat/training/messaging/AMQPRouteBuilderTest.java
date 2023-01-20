package com.redhat.training.messaging;

import io.quarkus.test.junit.QuarkusTest;

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
class AMQPRouteBuilderTest extends CamelQuarkusTestSupport {

    private static final String MOCK_RESULT = "mock:result";
     
    @EndpointInject(MOCK_RESULT)
    protected MockEndpoint resultEndpoint;

    @Produce("amqp:queue:amqp_order_input")
    protected ProducerTemplate producerTemplate;

    @Override
    protected RoutesBuilder createRouteBuilder() {
        return new AMQPRouteBuilder();
    }

    @Test
    void testLogOrderRoute() throws Exception {
        // Sets an assertion

        String exectedJson = "{\"ID\":5,\"Discount\":0.012,\"Delivered\":false,\"Desc\":\"Test Order\"}";

        resultEndpoint.expectedBodiesReceived(exectedJson);

        // Sends a message to the start component
        producerTemplate.sendBody(exectedJson);

        // Verifies that the mock component received 1 message
        resultEndpoint.assertIsSatisfied();
    }

    @BeforeEach
    void doAdvice() throws Exception {
        AdviceWith.adviceWith(context(), AMQPRouteBuilder.ROUTE_NAME,
                              AMQPRouteBuilderTest::adviceRoute);
    }

    private static void adviceRoute(AdviceWithRouteBuilder route) {
        route.interceptSendToEndpoint("direct:log_orders")
             .skipSendToOriginalEndpoint()
             .to(MOCK_RESULT);
    }

}
